package project.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.project.common.utils.RequestUtil;
import com.project.domain.User;
import com.project.error.BusinessException;
import com.project.error.EmBusinessError;
import com.project.response.CommonReturnType;
import com.project.response.response.TokenInfoResponse;
import com.project.service.LoginService;
import com.project.service.TokenService;
import com.project.service.UserService;
import com.project.view.UserDetailView;
import com.project.view.UserView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/*
@author DKR
@CreateDate 2020-7-16
@update 2020-7-16 DKR 获取部门联系人和公司联系人
        2020-7-21 DKR 添加查看和修改个人信息接口，修改了返回数据的类型
        2020-7-22 DKR 修改个人信息后改变cookie值
@description 获取联系人相关api
*/
@Api("用户接口")
@RestController
public class UserController extends BaseController {
    @Resource
    private LoginService loginService;
    @Resource
    private TokenService tokenService;
    @Resource
    private UserService userService;
    @ApiOperation("获取部门联系人接口")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success", response = UserView.class, responseContainer = "List"),
            @ApiResponse(code = 1, message = "用户未登录"),
            @ApiResponse(code = 20012, message = "token过期,需要重新登录"),
            @ApiResponse(code = 10002, message = "未知错误")
    })
    @RequestMapping(value = "/getDeptContacts",method = RequestMethod.GET)
    public CommonReturnType getDeptContact(HttpServletRequest request){
        String token= RequestUtil.getCookievalue(request);
        TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
        if(tokenInfoResponse==null||tokenService.isExpiration(token))
            throw new BusinessException(EmBusinessError.UNLOGIN);
        //查找部门联系人
        List<UserView> contacts=userService.getMyDeptContacts(Integer.parseInt(tokenInfoResponse.getUserId()));
        return CommonReturnType.create(contacts);
    }
    @ApiResponses({
            @ApiResponse(code = 200, message = "success", response = UserView.class, responseContainer = "List"),
            @ApiResponse(code = 1, message = "用户未登录"),
            @ApiResponse(code = 20012, message = "token过期,需要重新登录"),
            @ApiResponse(code = 10002, message = "未知错误")
    })
    @ApiOperation("获取公司联系人接口")
    @RequestMapping(value = "/getCompanyContacts",method = RequestMethod.GET)
    public CommonReturnType getCompanyContact(HttpServletRequest request){
        String token= RequestUtil.getCookievalue(request);
        TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
        if(tokenInfoResponse==null||tokenService.isExpiration(token))
            throw new BusinessException(EmBusinessError.UNLOGIN);
        //查找公司联系人
        List<UserView> contacts=userService.getMyCompContacts(Integer.parseInt(tokenInfoResponse.getUserId()));
        return CommonReturnType.create(contacts);
    }
    @ApiResponses({
            @ApiResponse(code = 200, message = "success", response = UserView.class, responseContainer = "List"),
            @ApiResponse(code = 1, message = "用户未登录"),
            @ApiResponse(code = 20012, message = "token过期,需要重新登录"),
            @ApiResponse(code = 10002, message = "未知错误")
    })
    @ApiOperation("获取没有部门的联系人接口")
    @RequestMapping(value = "/getNoDeptContacts",method = RequestMethod.GET)
    public  CommonReturnType getNoDeptContacts(HttpServletRequest request){
        String token= RequestUtil.getCookievalue(request);
        TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
        if(tokenInfoResponse==null||tokenService.isExpiration(token))
            throw new BusinessException(EmBusinessError.UNLOGIN);
        //查找没有部门的联系人
        List<UserView> contacts=userService.getMyNoDeptUsers(Integer.parseInt(tokenInfoResponse.getUserId()));
        return CommonReturnType.create(contacts);
    }
    @ApiOperation("修改个人信息接口")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success", response = UserDetailView.class),
            @ApiResponse(code = 1, message = "用户未登录"),
            @ApiResponse(code = 20012, message = "token过期,需要重新登录"),
            @ApiResponse(code = 10002, message = "未知错误"),
            @ApiResponse(code = 10003, message = "数据库错误"),
            @ApiResponse(code = 2006, message = "验证码填写错误"),
            @ApiResponse(code = 2008, message = "验证码过期")
    })
    @RequestMapping(value = "/updatePersonalInfo",method = RequestMethod.GET)
    public CommonReturnType updatePersonInfo(@RequestParam(value = "phoneNumber",required = false)String phoneNumber,
                                             @RequestParam(value = "password",required = false)String password,
                                             @RequestParam(value = "userName",required = false)String userName,
                                             @RequestParam(value = "verificationCode",required = true)String verificationCode,
                                             HttpServletRequest request,
                                             HttpServletResponse response){
        String token= RequestUtil.getCookievalue(request);
        TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
        if(tokenInfoResponse==null||tokenService.isExpiration(token))
            throw new BusinessException(EmBusinessError.UNLOGIN);
        JSONObject json;
        try{
            json = (JSONObject)request.getSession(false).getAttribute("verifyCode");
        }
        catch (Exception e){
            throw new BusinessException(EmBusinessError.USER_VERIFICATION_CODE_ERROR);
        }
        if(!json.getString("verifyCode").equals(verificationCode)){
            throw new BusinessException(EmBusinessError.USER_VERIFICATION_CODE_ERROR);//验证码输入错误
        }
        if((System.currentTimeMillis() - json.getLong("createTime")) > 1000 * 60 * 2){
            throw new BusinessException(EmBusinessError.USER_VERIFICATION_CODE_EXPIRED);//验证码过期
        }
        User user=userService.getUserById(Integer.parseInt(tokenInfoResponse.getUserId()));
        //需要判断用户是否修改了电话号码
        User newUser=new User(Integer.parseInt(tokenInfoResponse.getUserId())
                ,userName,password,phoneNumber==null?user.getUserPhonenumber():phoneNumber);
        if(userService.modifyMyInfo(newUser))
        {
            if(userName!=null)
                RequestUtil.setCookieValue("userName",userName,response);
            return CommonReturnType.create(null);
        }
        else
            throw new BusinessException(EmBusinessError.DB_ERROR);
    }
    @ApiOperation("查看个人信息接口")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success", response = UserDetailView.class),
            @ApiResponse(code = 1, message = "用户未登录"),
            @ApiResponse(code = 20012, message = "token过期,需要重新登录"),
            @ApiResponse(code = 10002, message = "未知错误"),
            @ApiResponse(code = 10003, message = "数据库错误")
    })
    @RequestMapping(value = "/getMyPersonalInfo",method = RequestMethod.GET)
    public CommonReturnType getMyPersonalInfo(HttpServletRequest request){
        String token= RequestUtil.getCookievalue(request);
        TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
        if(tokenInfoResponse==null||tokenService.isExpiration(token))
            throw new BusinessException(EmBusinessError.UNLOGIN);
        UserDetailView userDetailView=userService.getUserInfo(Integer.parseInt(tokenInfoResponse.getUserId()));
        if(userDetailView==null)
            throw new BusinessException(EmBusinessError.DB_ERROR);
        else
            return CommonReturnType.create(userDetailView);
    }
}
