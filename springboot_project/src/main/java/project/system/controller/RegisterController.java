package project.system.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.system.common.utils.Md5Utils;
import project.system.common.utils.Validator;
import project.system.domain.User;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;
import project.system.response.CommonReturnType;
import project.system.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
/*
@author DKR
@CreateDate 2020-7-09
@update 2020-7-09 实现注册验证码发送功能
        2020-7-10 session验证验证码
        2020-7-11 注册根据用户的电话号码和用户填写的密码生成密码
@description 发送注册验证码api和注册api
*/
@Api("注册接口")
@RestController
public class RegisterController extends BaseController {
    @Resource
    UserService userService;
//    @Resource
//    TencentMessage tecentMessage;
    @ApiOperation(value = "验证码接口",notes = "type为register为注册验证码,其他type不会验证手机号是否已经注册")
    @ApiResponses({
            @ApiResponse(code = 200,message = "success"),
            @ApiResponse(code = 2004,message = "电话号码已经注册"),
            @ApiResponse(code = 2005,message = "手机号格式错误"),
            @ApiResponse(code = 2007,message = "验证码发送失败")
    })
    @RequestMapping("/verificationCode")
    public Object verificationCode(HttpServletRequest request)
    {
        //从前端获取需要的参数
        String phoneNumber=request.getParameter("phoneNumber");
        String type=request.getParameter("type");
        // 生成随机的六位数验证码
        if(!Validator.isMobile(phoneNumber)){
            throw new BusinessException(EmBusinessError.USER_PHONE_ERROR);//电话号码格式错误
        }
        if(type.equals("register")) {
            if (userService.PhoneNumberExist(phoneNumber)) {
                throw new BusinessException(EmBusinessError.USER_PHONE_EXIST);//电话号码已被注册
            }
        }
        String verificationCode = "123456";
        //TODO: 邮件实现
        if(true) {
            HttpSession session=request.getSession();//短信发送成功
            request.getSession().removeAttribute("verifyCode");
            //将验证码存到session中,同时存入创建时间,以json存放，使用阿里的fastjson
            JSONObject json = null;
            json = new JSONObject();
            json.put("phoneNumber", phoneNumber);
            json.put("verifyCode", verificationCode);
            json.put("createTime", System.currentTimeMillis());
            request.getSession().setAttribute("verifyCode", json);
            return CommonReturnType.create(null);
        }
        else throw new BusinessException(EmBusinessError.USER_VERIFICATION_CODE_SEND_FAIL);
//        SMSParameter smsParameter= tecentMessage.GetVerifyCodeParam(phoneNumber);//生成短信发送参数对象
//        if(tecentMessage.sendSms(smsParameter).equals("success")) {
//            HttpSession session=request.getSession();//短信发送成功
//            request.getSession().removeAttribute("verifyCode");
//            //将验证码存到session中,同时存入创建时间,以json存放，使用阿里的fastjson
//            JSONObject json = null;
//            json = new JSONObject();
//            json.put("phoneNumber", smsParameter.getPhone());
//            json.put("verifyCode", smsParameter.getVerifyCode());
//            json.put("createTime", System.currentTimeMillis());
//            request.getSession().setAttribute("verifyCode", json);
//            return CommonReturnType.create(null);
//        }
//        else throw new BusinessException(EmBusinessError.USER_VERIFICATION_CODE_SEND_FAIL);
    }
    @ApiOperation(value = "注册接口")
    @ApiResponses({
            @ApiResponse(code = 200,message = "success"),
            @ApiResponse(code = 2005,message = "手机号填写错误"),
            @ApiResponse(code = 2006,message = "验证码填写错误"),
            @ApiResponse(code = 2008,message = "验证码过期")
    })
    @RequestMapping("/register")
    public Object register(HttpServletRequest request){
        //从前端获取相应的参数
        String userName=request.getParameter("userName");
        String password = request.getParameter("password");
        String phoneNumber = request.getParameter("phoneNumber");
        String verificationCode = request.getParameter("verificationCode");
        //从session中获取正确的验证码
        JSONObject json = (JSONObject)request.getSession(false).getAttribute("verifyCode");
        if(json == null){
            throw new BusinessException(EmBusinessError.USER_VERIFICATION_CODE_ERROR);//session中不存在验证码
        }
        if(!json.getString("phoneNumber").equals(phoneNumber)){
            throw new BusinessException(EmBusinessError.USER_PHONE_ERROR);//电话号码填写错误
        }
        if(!json.getString("verifyCode").equals(verificationCode)){
            throw new BusinessException(EmBusinessError.USER_VERIFICATION_CODE_ERROR);//验证码填写错误
        }
        if((System.currentTimeMillis() - json.getLong("createTime")) > 1000 * 60 * 2){
            throw new BusinessException(EmBusinessError.USER_VERIFICATION_CODE_EXPIRED);//验证码过期
        }
        User user=new User();
        user.setUserName(userName);
        String userPassword;
        userPassword= Md5Utils.inputPassToDBPass(password,phoneNumber+"miaowhu");//对用户的密码进行加密
      //  System.out.println(userPassword);
        user.setUserPassword(userPassword);
        user.setUserPhonenumber(phoneNumber);
        if(userService.InsertUser(user))//用户插入数据库成功，注册成功
            return CommonReturnType.create(null);
        else throw new BusinessException(EmBusinessError.DB_ERROR);//数据库错误，用户注册失败
    }
}
