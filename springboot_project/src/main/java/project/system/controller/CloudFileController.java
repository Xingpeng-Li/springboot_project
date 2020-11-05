package project.system.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import project.system.common.utils.RequestUtil;
import project.system.domain.Cloudfile;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;
import project.system.response.CommonReturnType;
import project.system.response.response.TokenInfoResponse;
import project.system.service.CloudFileService;
import project.system.service.LoginService;
import project.system.service.TokenService;
import project.system.view.CloudFileView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
/*
@author DKR
@CreateDate 2020-7-14
@update 2020-7-14 DKR 实现文件的上传、复制、删除、获取文件列表接口
        2020-7-16 DKR 修改获取文件列表接口，完善异常处理
        2020-7-21 DKR 修改了返回的数据类型
@description 云空间相关api
*/

@RequestMapping("/cloudFile")
@RestController
public class CloudFileController extends BaseController {
    @Resource
    private LoginService loginService;
    @Resource
    CloudFileService cloudFileService;
    @Resource
    TokenService tokenService;
    //上传文件接口
    @ApiOperation(value = "上传文件接口")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success"),
            @ApiResponse(code = 1, message = "用户未登录"),
            @ApiResponse(code = 20012, message = "token过期,需要重新登录"),
            @ApiResponse(code = 10002, message = "未知错误"),
            @ApiResponse(code = 10003, message = "数据库错误"),
            @ApiResponse(code = 60001,message = "文件上传失败"),
            @ApiResponse(code = 60008,message = "服务器接收文件失败")
    })
    @RequestMapping(value = "/uploadFile",method = RequestMethod.POST)
    public CommonReturnType uploadFile(@RequestParam("file")MultipartFile file,
                                       HttpServletRequest request){
        //登录有效性判断
        String token= RequestUtil.getCookievalue(request);
        TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
        if(tokenInfoResponse==null||tokenService.isExpiration(token)) {
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
        //发起文件上传请求，文件上传成功返回true
        if(cloudFileService.uploadFile(file,Integer.parseInt(tokenInfoResponse.getUserId()))) {
            return CommonReturnType.create(null);//返回请求成功响应
        }
        else {
            throw new BusinessException(EmBusinessError.FILE_UPLOAD_FAIL);//抛出上传失败异常
        }
    }
    //保存文件接口
    @ApiOperation(value = "保存文件接口",notes = "将他人的云空间文件保存到自己的云空间")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success"),
            @ApiResponse(code = 1, message = "用户未登录"),
            @ApiResponse(code = 20012, message = "token过期,需要重新登录"),
            @ApiResponse(code = 10002, message = "未知错误"),
            @ApiResponse(code = 10003, message = "数据库错误"),
            @ApiResponse(code = 60006,message = "文件保存失败")
    })
    @RequestMapping(value = "/copyfile",method = RequestMethod.GET)
    public CommonReturnType copyfile(@RequestParam("fileId")Integer fileId,
                                     HttpServletRequest request) {
        //登录有效性判断
        String token= RequestUtil.getCookievalue(request);
        TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
        Cloudfile cloudfile=cloudFileService.getFileById(fileId);
        Integer userId=Integer.parseInt(tokenInfoResponse.getUserId());
        if(cloudfile==null) {//要复制的目标文件不存在
            throw new BusinessException(EmBusinessError.DB_ERROR);//抛出文件不存在的异常
        }
        if(cloudFileService.copyFile(cloudfile,userId)){
            return CommonReturnType.create(null);
        }//复制成功
        else{
            throw new BusinessException(EmBusinessError.FILE_COPY_FAIL);//复制失败
        }

    }
    @ApiOperation(value = "获取文件列表接口",notes = "后端分页获取")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success",response = CloudFileView.class,responseContainer = "List"),
            @ApiResponse(code = 1, message = "用户未登录"),
            @ApiResponse(code = 20012, message = "token过期,需要重新登录"),
            @ApiResponse(code = 10002, message = "未知错误"),
            @ApiResponse(code = 10003, message = "数据库错误")
    })
    //分页获取云空间文件列表
    @RequestMapping(value = "/getMyFiles",method = RequestMethod.GET)
    public CommonReturnType getMyFiles(@RequestParam(name = "pageNumber",required = false)String pageNumber,
                                       @RequestParam(name = "pageSize",required = false)String pageSize,
                                       HttpServletRequest request){
        //参数预处理
        if (pageNumber == null) {
            pageNumber = "1";
        }
        if (pageSize == null) {
            pageSize = "5";
        }
        //登录有效性验证
        String token= RequestUtil.getCookievalue(request);
        TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
        if(tokenInfoResponse==null||tokenService.isExpiration(token)) {
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
        //获取文件列表
        List<CloudFileView> cloudFiles =cloudFileService.getMyFiles(Integer.parseInt(tokenInfoResponse.getUserId()),Integer.parseInt(pageNumber),Integer.parseInt(pageSize));
        return CommonReturnType.create(cloudFiles);//成功返回文件列表
    }
    //删除文件接口
    @ApiOperation("删除文件接口")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success"),
            @ApiResponse(code = 1, message = "用户未登录"),
            @ApiResponse(code = 20012, message = "token过期,需要重新登录"),
            @ApiResponse(code = 10002, message = "未知错误"),
            @ApiResponse(code = 10003, message = "数据库错误")
    })
    @RequestMapping(value = "/deleteFile",method = RequestMethod.GET)
    public CommonReturnType deleteFile(@RequestParam("fileId")Integer fileId,
                                       HttpServletRequest request)
    {
        //登录有效性验证
        String token= RequestUtil.getCookievalue(request);
        TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
        if(tokenInfoResponse==null||tokenService.isExpiration(token)) {
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
        if(cloudFileService.deleteFile(fileId)) {
            return CommonReturnType.create(null);//删除成功
        }
        else {
            throw new BusinessException(EmBusinessError.DB_ERROR);//删除失败
        }
    }
    @ApiOperation(value = "获取所有文件列表接口",notes = "前端分页")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success",response = CloudFileView.class,responseContainer = "List"),
            @ApiResponse(code = 1, message = "用户未登录"),
            @ApiResponse(code = 20012, message = "token过期,需要重新登录"),
            @ApiResponse(code = 10002, message = "未知错误"),
            @ApiResponse(code = 10003, message = "数据库错误")
    })
    //获取用户所有云空间文件列表
    @RequestMapping(value = "/getAll",method = RequestMethod.GET)
    public CommonReturnType getAll(HttpServletRequest request){
        //登录有效性验证
        String token= RequestUtil.getCookievalue(request);
        TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
        if(tokenInfoResponse==null||tokenService.isExpiration(token)) {
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
        //获取文件列表
        List<CloudFileView> cloudFiles=cloudFileService.getAllFiles(Integer.parseInt(tokenInfoResponse.getUserId()));
        return CommonReturnType.create(cloudFiles);//成功返回文件列表
    }
    @ApiOperation(value = "搜索文件接口",notes = "前端分页")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success",response = CloudFileView.class,responseContainer = "List"),
            @ApiResponse(code = 1, message = "用户未登录"),
            @ApiResponse(code = 20012, message = "token过期,需要重新登录"),
            @ApiResponse(code = 10002, message = "未知错误"),
            @ApiResponse(code = 10003, message = "数据库错误")
    })
    //搜索文件接口
    @RequestMapping(value = "/search",method = RequestMethod.GET)
    public CommonReturnType search(@RequestParam("key")String key,
                                   HttpServletRequest request){
        //登录有效性验证
        String token= RequestUtil.getCookievalue(request);
        TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
        //获取搜索结果
        List<CloudFileView> cloudFiles=cloudFileService.searchFiles(key,Integer.parseInt(tokenInfoResponse.getUserId()));
        return CommonReturnType.create(cloudFiles);//成功返回文件列表
    }
}
