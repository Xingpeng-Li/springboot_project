package project.system.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.system.common.utils.RequestUtil;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;
import project.system.response.CommonReturnType;
import project.system.response.response.TokenInfoResponse;
import project.system.service.BackLogService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author zws
 * @version 1.0
 * @class BackLogController
 * @description 用于处理待办事项
 * @date 2020/12/5 11:43
 */
@RestController
public class BackLogController {
    @Resource
    BackLogService backLogService;
    //创建待办事项接口
    @ApiOperation("创建待办事项接口")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success"),
            @ApiResponse(code = 1, message = "用户未登录"),
            @ApiResponse(code = 20012, message = "token过期,需要重新登录"),
            @ApiResponse(code = 10002, message = "未知错误"),
            @ApiResponse(code = 10003, message = "数据库错误")
    })
    @GetMapping(value = "/createBackLog")
    public void createBackLog(@RequestParam("userId")Integer userId,
                              @RequestParam("endTime") Date endTime,
                                       HttpServletRequest request)
    {

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
    @GetMapping(value = "/getNotFinishedBackLogs")
    public CommonReturnType getNotFinishedBackLogs(HttpServletRequest request)
    {
        return CommonReturnType.create(null);
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
    @GetMapping(value = "/getFinishedBackLogs")
    public CommonReturnType  getFinishedBackLogs(HttpServletRequest request)
    {
        return CommonReturnType.create(null);
    }
    //获取待办事项截至时间
    @ApiOperation("获取待办事项截至时间")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success"),
            @ApiResponse(code = 1, message = "用户未登录"),
            @ApiResponse(code = 20012, message = "token过期,需要重新登录"),
            @ApiResponse(code = 10002, message = "未知错误"),
            @ApiResponse(code = 10003, message = "数据库错误")
    })
    @GetMapping(value = "/getBackLogEndTime")
    public CommonReturnType getBackLogEndTime(@RequestParam("backLogId")Integer backLogId,
                              HttpServletRequest request)
    {

        return CommonReturnType.create(null);
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
    @GetMapping(value = "/finishBackLog")
    public void finishBackLog(@RequestParam("backLogId")Integer backLogId,
                              HttpServletRequest request)
    {
        backLogService.finishBackLog(backLogId);

    }
    //待办事项超时
    @ApiOperation("待办事项超时")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success"),
            @ApiResponse(code = 1, message = "用户未登录"),
            @ApiResponse(code = 20012, message = "token过期,需要重新登录"),
            @ApiResponse(code = 10002, message = "未知错误"),
            @ApiResponse(code = 10003, message = "数据库错误")
    })
    @GetMapping(value = "/overTimeBackLog")
    public void overTimeBackLog(@RequestParam("backLogId")Integer backLogId,
                              HttpServletRequest request)
    {
        backLogService.overBackLog(backLogId);
    }



}