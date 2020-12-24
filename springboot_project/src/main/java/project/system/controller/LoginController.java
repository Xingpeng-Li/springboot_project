package project.system.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import project.system.common.constant.RequestConstant;
import project.system.common.utils.Md5Utils;
import project.system.common.utils.RequestUtil;
import project.system.common.utils.Validator;
import project.system.domain.User;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;
import project.system.response.CommonReturnType;
import project.system.response.request.LoginRequest;
import project.system.response.response.TokenInfoResponse;
import project.system.service.LoginService;
import project.system.service.TokenService;
import project.system.service.UserService;
import project.system.service.manager.SimpleCoreManager;
import project.system.vo.UserVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * @author zws
 * @description 实现登录接口，分为账号密码登录和验证码登录
 * @create 2020/11/28 20:52
 * @update 2020/11/28 20:52
 * @param
 * @return
 **/
@Api("登录接口")
@RestController
public class LoginController extends BaseController{
    @Resource
    UserService userService;
    @Resource
    TokenService tokenService;
    @Resource
    private SimpleCoreManager simpleCoreManager;
    @Resource
    private LoginService loginService;
    @ApiOperation("手机号验证码登录接口")
    @ApiResponses({
            @ApiResponse(code = 200,message = "success",response = TokenInfoResponse.class),
            @ApiResponse(code = 20001,message = "手机号没有注册"),
            @ApiResponse(code = 2005,message = "手机号填写错误"),
            @ApiResponse(code = 2006, message = "验证码填写错误"),
            @ApiResponse(code = 2008, message = "验证码过期")
    })
    @PostMapping("/loginByPhone")
    public CommonReturnType login(HttpServletRequest request, HttpServletResponse response){
        //从前端获取需要的参数
        String phoneNumber=request.getParameter("phoneNumber");
        String verificationCode=request.getParameter("verificationCode");
        if(!Validator.isMobile(phoneNumber)){
            throw new BusinessException(EmBusinessError.USER_PHONE_ERROR);
        }
        //从session中获取正确的验证码
        JSONObject json;
        try{
            json = (JSONObject)request.getSession(false).getAttribute("verifyCode");
        }
        catch (Exception e){
            throw new BusinessException(EmBusinessError.USER_VERIFICATION_CODE_ERROR);
        }
        //判断数据库是否包含发起登录请求的用户
        User user=userService.getUserByPhoneNumber(phoneNumber);
        if(user==null) {
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);//用户不存在，没有注册
        }
        if(json == null){
            throw new BusinessException(EmBusinessError.USER_VERIFICATION_CODE_ERROR);//Session中验证码丢失
        }
        if(!json.getString("phoneNumber").equals(phoneNumber)){
            throw new BusinessException(EmBusinessError.USER_PHONE_ERROR);//用户的电话号码不正确
        }
        if(!json.getString("verifyCode").equals(verificationCode)){
           throw new BusinessException(EmBusinessError.USER_VERIFICATION_CODE_ERROR);//验证码输入错误
        }
        if((System.currentTimeMillis() - json.getLong("createTime")) > 1000 * 60 * 2){
            throw new BusinessException(EmBusinessError.USER_VERIFICATION_CODE_EXPIRED);//验证码过期
        }
        TokenInfoResponse tokenInfoResponse=new TokenInfoResponse();
        tokenInfoResponse.setUserId(String.valueOf(user.getUserId()));
        tokenInfoResponse.setUserName(user.getUserName());
        String token=tokenService.createToken(user.getUserId());//下发token
        //设置返回的字段值
        tokenInfoResponse.setIsLogin(true);
        tokenInfoResponse.setToken(token);
        tokenInfoResponse.setCompanyId(user.getCompanyId());
        tokenInfoResponse.setDeptId(user.getDeptId());
        if(user.getCompanyId()!=0)//判断用户是否已经加入公司
        {
            tokenInfoResponse.setHaveJoinedCompany(true);
        }
        else
        {
            tokenInfoResponse.setHaveJoinedCompany(false);
        }
        simpleCoreManager.addTokenInfo(token,tokenInfoResponse);
        UserVo userView=userService.convertFromUserToUserView(user);
        //将token存入cookie
        RequestUtil.setCookieValue(RequestConstant.TOKEN, tokenInfoResponse.getToken(), response);
        RequestUtil.setCookieValue("userName",user.getUserName(),response);
        RequestUtil.setCookieValue("userId", user.getUserId().toString(), response);
        return CommonReturnType.create(tokenInfoResponse);////登录成功返回用户信息
    }
    @ApiOperation("手机号密码登录接口")
    @ApiResponses({
            @ApiResponse(code = 200,message = "success",response = TokenInfoResponse.class),
            @ApiResponse(code = 20011,message = "用户名或者密码错误")
    })
    @PostMapping("/login")
    public  Object loginByPassword(HttpServletRequest request, HttpServletResponse response) {
        //从前端获取需要的参数
        String phoneNumber = request.getParameter("phoneNumber");
        String password = request.getParameter("password");
        //对前端传来的密码加密处理后与数据库的密码比对
        String userPassword = Md5Utils.inputPassToDBPass(password, phoneNumber);
        User user = new User();
        user.setUserPhonenumber(phoneNumber);
        user.setUserPassword(userPassword);
        //验证用户
        User verifiedUser = userService.verifyUser(user);
        if (verifiedUser == null) {
            throw new BusinessException(EmBusinessError.USER_LOGIN_VERIFY_FAIL);//用户名或者命名错误，或者用户不存在
        }
        else {
            //下发token，登录成功
            TokenInfoResponse tokenInfoResponse = new TokenInfoResponse();
            tokenInfoResponse.setUserId(String.valueOf(verifiedUser.getUserId()));
            tokenInfoResponse.setUserName(verifiedUser.getUserName());
            String token = tokenService.createToken(verifiedUser.getUserId());
            //设置返回的字段值
            tokenInfoResponse.setIsLogin(true);
            tokenInfoResponse.setToken(token);
            tokenInfoResponse.setCompanyId(verifiedUser.getCompanyId());
            tokenInfoResponse.setDeptId(verifiedUser.getDeptId());
            UserVo userView = userService.convertFromUserToUserView(verifiedUser);
            if (verifiedUser.getCompanyId() != 0)//判断用户是否已经加入公司
            {
                tokenInfoResponse.setHaveJoinedCompany(true);
            }
            else
            {
                tokenInfoResponse.setHaveJoinedCompany(false);
            }
            simpleCoreManager.addTokenInfo(token, tokenInfoResponse);
            //将token存入cookie
            RequestUtil.setCookieValue(RequestConstant.TOKEN, tokenInfoResponse.getToken(), response);
            RequestUtil.setCookieValue("userName", verifiedUser.getUserName(), response);
            RequestUtil.setCookieValue("userId", verifiedUser.getUserId().toString(), response);
            return CommonReturnType.create(tokenInfoResponse);//登录成功返回用户信息
        }
    }
    @ApiOperation("用户登出接口")
    //退出登录操作，清除浏览器cookie，清除服务器缓存的用户登录信息
    @GetMapping(value = "/logout")
    public CommonReturnType logout(HttpServletRequest request, HttpServletResponse response) {
        String token = RequestUtil.getCookievalue(request);
        if ( StringUtils.isNotBlank(token)) {
            LoginRequest loginParam = new LoginRequest();
            loginParam.setToken(token);
        }
        RequestUtil.clearCookie(RequestConstant.TOKEN, response);
        return CommonReturnType.create(null);
    }
}
