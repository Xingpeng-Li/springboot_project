package project.system.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import project.system.common.utils.RequestUtil;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;
import project.system.response.CommonReturnType;
import project.system.response.response.TokenInfoResponse;
import project.system.service.AnnouncementService;
import project.system.service.TokenService;
import project.system.service.LoginService;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/*
 @author:李星鹏
 @createDate:2020/7/21
 @description:发送公告、查看公告
 */

@Controller
public class AnnouncementController extends BaseController {

    @Resource
    TokenService tokenService;
    @Resource
    LoginService loginService;
    @Resource
    AnnouncementService announcementService;

    //发布公告(可添加附件发送也可以不添加附件发送)
    @ApiOperation("发布公告接口")
    @ApiResponses({
            @ApiResponse(code = 200,message = "success"),
            @ApiResponse(code = 1,message = "用户未登录"),
            @ApiResponse(code = 1,message = "登录已过期"),
            @ApiResponse(code = 20013,message = "用户没有权限"),
    })
    @PostMapping("/announcement/send")
    public CommonReturnType sendAnnouncement(@RequestParam(value = "file", required = false)MultipartFile file,
                                             HttpServletRequest request){
        String type = request.getParameter("type");   //公告类型
        String title = request.getParameter("title");   //公告标题
        String body = request.getParameter("body");   //公告正文(主体)
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token) && !tokenService.isExpiration(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            String userId = tokenInfoResponse.getUserId();
            announcementService.sendAnnouncement(type,title,body,Integer.parseInt(userId),file);
            return CommonReturnType.create("发送成功");
        }
        else{
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }

    @ApiOperation("接收公告接口")
    @ApiResponses({
            @ApiResponse(code = 200,message = "success"),
            @ApiResponse(code = 1,message = "用户未登录"),
            @ApiResponse(code = 1,message = "登录已过期"),
    })
    //接收公告
    @GetMapping("/announcement/get")
    public CommonReturnType getAnnouncement(HttpServletRequest request){
        String type = request.getParameter("type");   //公告类型
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token) && !tokenService.isExpiration(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            String userId = tokenInfoResponse.getUserId();
            List<Map<String,Object>> announcementList = announcementService.getAnnouncement(type, Integer.parseInt(userId));
            return CommonReturnType.create(announcementList);
        }
        else{
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }

}
