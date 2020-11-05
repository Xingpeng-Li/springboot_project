package project.system.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import project.system.common.utils.RequestUtil;
import project.system.common.utils.TencentCosClient;
import project.system.common.utils.TencentSpeechRecognition;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;
import project.system.response.CommonReturnType;
import project.system.response.response.TokenInfoResponse;
import project.system.service.LoginService;
import project.system.service.TokenService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/*
@author DKR
@CreateDate 2020-7-17
@update 2020-7-17 DKR 实现录音文件转换文字接口，和查看转换后文字的接口
@description 语音识别相关api
*/
@RestController
public class SpeechRecognitionController extends BaseController {
    @Resource
    LoginService loginService;
    @Resource
    TokenService tokenService;
    @Resource
    TencentCosClient tencentCosClient;
    @Resource
    TencentSpeechRecognition tencentSpeechRecognition;
    @RequestMapping(value = "/requestRecognition",method = RequestMethod.POST)
    public CommonReturnType requestRecognition(@RequestParam("voiceFile")MultipartFile voiceFile,
                                               HttpServletRequest request,
                                               HttpServletResponse response){
        String token= RequestUtil.getCookievalue(request);
        TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
        if(tokenInfoResponse==null||tokenService.isExpiration(token))
            throw new BusinessException(EmBusinessError.UNLOGIN);
        Long taskId=
                tencentSpeechRecognition.createRecognitionTask(
                        tencentCosClient.uploadCloudFile(voiceFile,"0",voiceFile.getOriginalFilename())
                );
        if(taskId!=null)
        {
            RequestUtil.setCookieValue("taskId",taskId.toString(),response);
            return CommonReturnType.create(null);
        }
        else
            throw new BusinessException(EmBusinessError.RECOGNITION_FAIL);
    }
    @RequestMapping(value = "/getRecognitionResult",method = RequestMethod.GET)
    public CommonReturnType getRecognitionResult(HttpServletRequest request){
         String token= RequestUtil.getCookievalue(request);
        TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
        if(tokenInfoResponse==null||tokenService.isExpiration(token))
            throw new BusinessException(EmBusinessError.UNLOGIN);
        Long taskId=RequestUtil.getTaskIdCookie(request);
        String result=tencentSpeechRecognition.getRecognitionResult(taskId);
        if(!result.equals(""))
        {return CommonReturnType.create(result);}
        else throw new BusinessException(EmBusinessError.RECOGNITION_RESULT_FAIL);
    }
}