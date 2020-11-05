package project.system.common.utils;

import com.tencentcloudapi.asr.v20190614.models.DescribeTaskStatusRequest;
import com.tencentcloudapi.asr.v20190614.models.DescribeTaskStatusResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.asr.v20190614.AsrClient;
import com.tencentcloudapi.asr.v20190614.models.CreateRecTaskRequest;
import com.tencentcloudapi.asr.v20190614.models.CreateRecTaskResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;

/*
@author DKR
@CreateDate 2020-7-17
@update 2020-7-17 DKR 实现录音文件转换文字的请求函数，和查看转换结果的函数
@description 与腾讯云语音识别接口对接的类
*/
@Component
public class TencentSpeechRecognition {
    @Value("${tx.cos.secretId}")
    private String secretId;
    @Value("${tx.cos.secretKey}")
    private String secretKey;
    //转换任务请求发起函数
    public Long createRecognitionTask(String url)
    {
        try{
            //对接配置
            Credential cred = new Credential(secretId, secretKey);
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("asr.tencentcloudapi.com");
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            AsrClient client = new AsrClient(cred, "ap-shanghai", clientProfile);
            //设置请求参数
            String params = "{\"EngineModelType\":\"16k_zh\",\"ChannelNum\":1,\"ResTextFormat\":0,\"SourceType\":0,\"Url\":\""+url+"\"}";
            CreateRecTaskRequest req = CreateRecTaskRequest.fromJsonString(params, CreateRecTaskRequest.class);
            CreateRecTaskResponse resp = client.CreateRecTask(req);
            System.out.println(CreateRecTaskRequest.toJsonString(resp));
            return resp.getData().getTaskId();//返回请求的TaskId
        } catch (TencentCloudSDKException e) {
           // System.out.println(e.toString());
            throw new BusinessException(EmBusinessError.RECOGNITION_FAIL);//抛出请求异常
        }
    }
    //查看转换任务结果函数
    public String getRecognitionResult(Long taskId){
        try{
            //对接配置
            Credential cred = new Credential(secretId,secretKey);
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("asr.tencentcloudapi.com");
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            AsrClient client = new AsrClient(cred, "", clientProfile);
            //设置请求参数
            String params = "{\"TaskId\":"+taskId+"}";
            DescribeTaskStatusRequest req = DescribeTaskStatusRequest.fromJsonString(params, DescribeTaskStatusRequest.class);
            DescribeTaskStatusResponse resp = client.DescribeTaskStatus(req);
            System.out.println(DescribeTaskStatusResponse.toJsonString(resp));
            if(resp.getData().getStatusStr().equals("doing"))
                throw new BusinessException(EmBusinessError.RECOGNITION_PROCESSING);//任务还在进行中
            else if(resp.getData().getStatusStr().equals("waiting"))
                throw new BusinessException(EmBusinessError.RECOGNITION_WAITING);//任务在等待队列中
            else if(resp.getData().getStatusStr().equals("failed"))
                throw new BusinessException(EmBusinessError.RECOGNITION_RESULT_FAIL);//转换失败
            else {
                String pattern="([0-9]:[0-9]{1,3}.[0-9]{3},[0-9]:[0-9]{1,3}.[0-9]{1,3})";
                return resp.getData().getResult().replaceAll(pattern,"")
                        .replace("[]","")
                        .replace("\n","");
            }//转换成功,返回转换后的文字字符
        } catch (TencentCloudSDKException e) {
           // System.out.println(e.toString());
            throw new BusinessException(EmBusinessError.RECOGNITION_RESULT_FAIL);//抛出请求异常
        }
    }
}
