package project.system.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import project.system.common.utils.RequestUtil;
import project.system.domain.Backlog;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;
import project.system.response.CommonReturnType;
import project.system.response.response.TokenInfoResponse;
import project.system.service.BackLogService;
import project.system.service.LoginService;
import project.system.service.TokenService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zws
 * @version 1.0
 * @class BackLogController
 * @description 用于处理待办事项
 * @date 2020/12/5 11:43
 */
@RestController
@RequestMapping("/backlog")
public class BackLogController {
    @Resource
    BackLogService backLogService;
    @Resource
    LoginService loginService;
    @Resource
    TokenService tokenService;

    //创建待办事项接口
    @ApiOperation("创建待办事项接口")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success"),
            @ApiResponse(code = 1, message = "用户未登录"),
            @ApiResponse(code = 20012, message = "token过期,需要重新登录"),
            @ApiResponse(code = 10002, message = "未知错误"),
            @ApiResponse(code = 10003, message = "数据库错误")
    })
    @PostMapping(value = "/create")
    public CommonReturnType createBackLog(HttpServletRequest request)
    {
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        Boolean isFinished=false;
        Boolean isOverTime=false;
        String endTime=request.getParameter("endTime");
        //通过token获取UserId
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            String userId = tokenInfoResponse.getUserId();
            backLogService.createBackLog(Integer.parseInt(userId),title,description,isFinished,isOverTime,endTime);
            return CommonReturnType.create(null);
        }
        else{
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }

    }
    /*
     * 修改待办事项接口
     * @author zws
     * @since 2020/12/5
     * @param [request]
     * @return project.system.response.CommonReturnType
     **/
    @ApiOperation("修改待办事项接口")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success"),
            @ApiResponse(code = 1, message = "用户未登录"),
            @ApiResponse(code = 20012, message = "token过期,需要重新登录"),
            @ApiResponse(code = 10002, message = "未知错误"),
            @ApiResponse(code = 10003, message = "数据库错误")
    })
    @PostMapping(value = "/update")
    public CommonReturnType updateBackLog(HttpServletRequest request)
    {
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        Boolean isFinished=false;
        Boolean isOverTime=false;
        String endTime=request.getParameter("endTime");
        String backlogId=request.getParameter("id");
        //通过token获取UserId
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            String userId = tokenInfoResponse.getUserId();
            backLogService.updateBackLog(Integer.parseInt(backlogId),Integer.parseInt(userId),title,description,isFinished,isOverTime,endTime);
            return CommonReturnType.create(null);
        }
        else{
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }

    }

    //获取未完成待办事项列表
    @ApiOperation("获取未完成待办事项列表")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success"),
            @ApiResponse(code = 1, message = "用户未登录"),
            @ApiResponse(code = 20012, message = "token过期,需要重新登录"),
            @ApiResponse(code = 10002, message = "未知错误"),
            @ApiResponse(code = 10003, message = "数据库错误")
    })
    @GetMapping(value = "/getNotFinished")
    public CommonReturnType getNotFinishedBackLogs(HttpServletRequest request)
    {
        //通过token获取UserId
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            String userId = tokenInfoResponse.getUserId();
            List<Backlog> backlogList=backLogService.getNotFinishedBackLogs(Integer.parseInt(userId));
            return CommonReturnType.create(backlogList);
        }
        else{
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }
    //获取已完成待办事项列表
    @ApiOperation("获取已完成待办事项列表")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success"),
            @ApiResponse(code = 1, message = "用户未登录"),
            @ApiResponse(code = 20012, message = "token过期,需要重新登录"),
            @ApiResponse(code = 10002, message = "未知错误"),
            @ApiResponse(code = 10003, message = "数据库错误")
    })
    @GetMapping(value = "/getFinished")
    public CommonReturnType  getFinishedBackLogs(HttpServletRequest request)
    {
        //通过token获取UserId
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            String userId = tokenInfoResponse.getUserId();
            List<Backlog> backlogList=backLogService.getFinishedBackLogs(Integer.parseInt(userId));
            return CommonReturnType.create(backlogList);
        }
        else{
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }

    //完成待办事项
    @ApiOperation("完成待办事项")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success"),
            @ApiResponse(code = 1, message = "用户未登录"),
            @ApiResponse(code = 20012, message = "token过期,需要重新登录"),
            @ApiResponse(code = 10002, message = "未知错误"),
            @ApiResponse(code = 10003, message = "数据库错误")
    })
    @PostMapping(value = "/finish")
    public CommonReturnType finishBackLog(HttpServletRequest request)
    {
        String backLogId = request.getParameter("backLogId");
        //获取token检查是否登录
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token) && !tokenService.isExpiration(token)) {
            backLogService.finishBackLog(Integer.parseInt(backLogId));
            return CommonReturnType.create("待办事项完成！");
        }
        else{
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }

    //删除待办事项
    @ApiOperation("完成待办事项")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success"),
            @ApiResponse(code = 1, message = "用户未登录"),
            @ApiResponse(code = 20012, message = "token过期,需要重新登录"),
            @ApiResponse(code = 10002, message = "未知错误"),
            @ApiResponse(code = 10003, message = "数据库错误")
    })
    @PostMapping(value = "/delete")
    public CommonReturnType deleteBackLog(HttpServletRequest request)
    {
        String backLogId = request.getParameter("backLogId");
        //获取token检查是否登录
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token) && !tokenService.isExpiration(token)) {
            backLogService.deleteBackLog(Integer.parseInt(backLogId));
            return CommonReturnType.create(null);
        }
        else{
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }
}