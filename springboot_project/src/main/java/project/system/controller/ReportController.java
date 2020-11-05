package project.system.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import project.system.common.utils.RequestUtil;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;
import project.system.mapper.NotificationMapper;
import project.system.response.CommonReturnType;
import project.system.response.response.TokenInfoResponse;
import project.system.service.LoginService;
import project.system.service.ReportService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/*
 @author:李星鹏
 @createDate:2020/7/14
 @description:上传工作报告、批阅工作报告
 */
@Controller
public class ReportController extends BaseController {

    @Resource
    LoginService loginService;

    @Resource
    ReportService reportService;

    @Resource
    NotificationMapper notificationMapper;

    @ApiOperation("发送报告接口")
    @ApiResponses({
            @ApiResponse(code = 200,message = "success"),
            @ApiResponse(code = 1,message = "用户未登录"),
            @ApiResponse(code = 1,message = "登录已过期"),
            @ApiResponse(code = 20013,message = "用户没有权限"),
    })
    @PostMapping("/report/send")
    public CommonReturnType sendReport(MultipartFile file, HttpServletRequest request) throws IOException {
        //报告类型
        String type = request.getParameter("type");
        //是否加水印
        String addWatermark = request.getParameter("addWatermark");
        //批阅人
        String approverPhoneNumber = request.getParameter("approverPhoneNumber");
        //抄送人
        String secondApproverPhoneNumber = request.getParameter("secondApproverPhoneNumber");
        //通过token获取UserId
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            String userId = tokenInfoResponse.getUserId();
            reportService.sendReport(file,addWatermark,type,Integer.parseInt(userId),approverPhoneNumber,secondApproverPhoneNumber);
            return CommonReturnType.create("发送成功！");
        }
        else{
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }

    @ApiOperation("下载报告接口")
    @ApiResponses({
            @ApiResponse(code = 200,message = "success"),
            @ApiResponse(code = 1,message = "用户未登录"),
            @ApiResponse(code = 1,message = "登录已过期"),
            @ApiResponse(code = 20013,message = "用户没有权限"),
    })
    @GetMapping("/report/get")
    public CommonReturnType getReportUrl(HttpServletRequest request){
        //提醒Id
        String notificationId = request.getParameter("notificationId");
        //报告url就存储在提醒数据库的body中
        String url = reportService.getReportUrl(Integer.parseInt(notificationId));
        return CommonReturnType.create(url);
    }

    @ApiOperation("批阅报告接口")
    @ApiResponses({
            @ApiResponse(code = 200,message = "success"),
            @ApiResponse(code = 1,message = "用户未登录"),
            @ApiResponse(code = 1,message = "登录已过期"),
            @ApiResponse(code = 20013,message = "用户没有权限"),
    })
    @PostMapping("/report/return")
    public CommonReturnType returnReport(MultipartFile file,HttpServletRequest request) throws IOException{
        //提醒Id
        String notificationId = request.getParameter("notificationId");

        reportService.returnReport(file,Integer.parseInt(notificationId));

        return CommonReturnType.create("发送成功");
    }
}
