package project.system.controller;

import com.project.common.utils.RequestUtil;
import com.project.domain.Notification;
import com.project.error.BusinessException;
import com.project.error.EmBusinessError;
import com.project.mapper.NotificationMapper;
import com.project.response.CommonReturnType;
import com.project.response.response.TokenInfoResponse;
import com.project.service.LoginService;
import com.project.service.NotificationService;
import com.project.service.TokenService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
/*
 @author:李星鹏
 @createDate:2020/7/16
 @description:消息提醒
 */

@Controller
public class NotificationController extends BaseController {

    @Resource
    TokenService tokenService;
    @Resource
    LoginService loginService;
    @Resource
    NotificationService notificationService;
    @Resource
    NotificationMapper notificationMapper;

    @ApiOperation("查看提醒接口")
    @ApiResponses({
            @ApiResponse(code = 200,message = "success"),
            @ApiResponse(code = 1,message = "用户未登录"),
            @ApiResponse(code = 1,message = "登录已过期"),
    })
    //返回用户收到的所有提醒
    @GetMapping("/notification/get")
    public CommonReturnType getAllNotification(HttpServletRequest request){
        //通过token获取UserId
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token) && !tokenService.isExpiration(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            String userId = tokenInfoResponse.getUserId();
            List<Map<String,Object>> notificationList =  notificationService.getNotification(Integer.parseInt(userId));
            return CommonReturnType.create(notificationList);
        }
        else{
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }

    @ApiOperation("删除提醒接口")
    @ApiResponses({
            @ApiResponse(code = 200,message = "success"),
            @ApiResponse(code = 1,message = "用户未登录"),
            @ApiResponse(code = 1,message = "登录已过期"),
    })
    //删除一个提醒
    @GetMapping("/notification/delete")
    public CommonReturnType deleteNotification(HttpServletRequest request){
        String notificationId = request.getParameter("notificationId");
        //通过token获取UserId
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token) && !tokenService.isExpiration(token)) {
            notificationService.deleteNotification(Integer.parseInt(notificationId));
            return CommonReturnType.create("删除成功");
        }
        else{
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }

    @ApiOperation("设置提醒已阅接口")
    //设置一个提醒已阅
    @GetMapping("/notification/read")
    public CommonReturnType readNotification(HttpServletRequest request){
        String notificationId = request.getParameter("notificationId");
        //通过token获取UserId
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token) && !tokenService.isExpiration(token)) {
            Notification notification = notificationMapper.selectByPrimaryKey(Integer.parseInt(notificationId));
            notification.setNotificationChecked("是");
            notificationMapper.updateByPrimaryKeySelective(notification);
            return CommonReturnType.create(null);
        }
        else{
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }
}
