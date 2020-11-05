package project.system.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import project.system.common.utils.RequestUtil;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;
import project.system.response.CommonReturnType;
import project.system.response.response.TokenInfoResponse;
import project.system.service.ApplicationService;
import project.system.service.LoginService;
import project.system.service.TokenService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/*
 @author:李星鹏
 @createDate:2020/7/17
 @description:审批功能后端Controller:发送审批、查看审批、处理审批
 */
@Controller
public class ApplicationController extends BaseController {
    @Resource
    LoginService loginService;
    @Resource
    ApplicationService applicationService;
    @Resource
    TokenService tokenService;

    //发送审批
    @ApiOperation("发送审批接口")
    @ApiResponses({
            @ApiResponse(code = 200,message = "success"),
            @ApiResponse(code = 1,message = "用户未登录"),
            @ApiResponse(code = 1,message = "登录已过期"),
            @ApiResponse(code = 10006,message = "日期格式不正确"),
            @ApiResponse(code = 10007,message = "开始时间不能晚于结束时间"),
    })
    @GetMapping("/application/send")
    public CommonReturnType sendApplication(HttpServletRequest request){
        String type = request.getParameter("type");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        String reason = request.getParameter("reason");
        String approverPhoneNumber = request.getParameter("approverPhoneNumber");
        String secondApproverPhoneNumber = request.getParameter("secondApproverPhoneNumber");
        //通过token获取UserId
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token) && !tokenService.isExpiration(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            String userId = tokenInfoResponse.getUserId();
            applicationService.sendApplication(type,startTime,endTime,reason,Integer.parseInt(userId),
                    approverPhoneNumber,secondApproverPhoneNumber);
            return CommonReturnType.create("发送成功！");
        }
        else{
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }

    //点击消息查看审批详细
    @ApiOperation("查看审批接口")
    @ApiResponses({
            @ApiResponse(code = 200,message = "success"),
            @ApiResponse(code = 1,message = "用户未登录"),
            @ApiResponse(code = 1,message = "登录已过期"),
    })
    @GetMapping("/application/get")
    public CommonReturnType getApplication(HttpServletRequest request){
        String notificationId = request.getParameter("notificationId");
        //获取token检查是否登录
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token) && !tokenService.isExpiration(token)) {
            Map<String,Object> map = applicationService.getApplication(Integer.parseInt(notificationId));
            return CommonReturnType.create(map);
        }
        else{
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }

    //处理审批
    @ApiOperation("处理审批接口")
    @ApiResponses({
            @ApiResponse(code = 200,message = "success"),
            @ApiResponse(code = 1,message = "用户未登录"),
            @ApiResponse(code = 1,message = "登录已过期"),
    })
    @GetMapping("/application/dispose")
    public CommonReturnType disposeApplication(HttpServletRequest request){
        String notificationId = request.getParameter("notificationId");
        String isAgree = request.getParameter("isAgree");
        //获取token
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token) && !tokenService.isExpiration(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            String userId = tokenInfoResponse.getUserId();
            applicationService.disposeApplication(Integer.parseInt(notificationId),isAgree,Integer.parseInt(userId));
            return CommonReturnType.create("处理成功");
        }
        else{
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }
}
