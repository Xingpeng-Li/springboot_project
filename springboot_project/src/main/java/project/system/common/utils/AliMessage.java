package project.system.common.utils;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
public class AliMessage {
    //aliyuncs的参数
    @Value("${aliyun.accessKeyID}")
    private String accessKeyID;
    @Value("${aliyun.accessKeySecret}")
    private String accessKeySecret;
    //短信api的请求地址  固定
    @Value("${aliyun.domain}")
    private String domain;
    //指定地域名称 短信API的就是 ap-nanjing 不能改变
    @Value("${aliyun.regionId}")
    private String regionId;
    //您的申请签名
    @Value("${aliyun.signName}")
    private String signName;
    //你的模板
    @Value("${aliyun.templateCode}")
    private String templateCode;
    //你的模板
    @Value("${aliyun.smsEffectiveTime}")
    private String smsEffectiveTime;

    /**
     * 发送短信接口
     *
     * @param smsParams 包含手机号和验证码
     * @return rep 返回发送成功与否的信息
     */
    public String sendSms(SMSParameter smsParams) {

        String rep = "error";

        // 指定地域名称 短信API的就是 ap-nanjing 不能改变  后边填写您的  accessKey 和 accessKey Secret
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyID, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        // 创建通用的请求对象
        CommonRequest request = new CommonRequest();
        // 指定请求方式
        request.setSysMethod(MethodType.POST);
        // 短信api的请求地址  固定
        request.setSysDomain(domain);
        //签名算法版本  固定
        request.setSysVersion("2017-05-25");
        //请求 API 的名称
        request.setSysAction("SendSms");
        //指定地域名称
        request.putQueryParameter("RegionId", regionId);
        // 要给哪个手机号发送短信  指定手机号
        request.putQueryParameter("PhoneNumbers", smsParams.getPhone());
        // 您的申请签名
        request.putQueryParameter("SignName", signName);
        // 您申请的模板 code
        request.putQueryParameter("TemplateCode", templateCode);

        Map<String, Object> params = new HashMap<>();
        //这里的key就是短信模板中的 ${xxxx}
        params.put("code", smsParams.getVerifyCode());

        // 放入参数  需要把 map转换为json格式  使用fastJson进行转换
        request.putQueryParameter("TemplateParam", JSON.toJSONString(params));

        try {
            CommonResponse response = client.getCommonResponse(request);
            //logger.info(JSON.parseObject(response.getData(), Map.class).get("Message").toString());
            if(response.getHttpResponse().isSuccess()){
                rep = "success";
            }

        } catch (ServerException e) {
            e.printStackTrace();
            rep = e.getErrMsg();
        } catch (ClientException e) {
            e.printStackTrace();
            rep = e.getErrMsg();
        }

        return rep;
    }
//    /**
//     * 指定模板 ID 单发短信
//     * @param smsParams
//     */
//    public String sendSms(SMSParameter smsParams) {
//        String rep = "error";
//        try {
//            String verifyCode = smsParams.getVerifyCode();
//            // 数组具体的元素个数和模板中变量个数必须一致
//            String[] params = {verifyCode,smsEffectiveTime};
//            SmsSingleSender smsSingleSender = new SmsSingleSender(appId, appKey);
//            // 签名参数未提供或者为空时，会使用默认签名发送短信
//            SmsSingleSenderResult smsSingleSenderResult = smsSingleSender.sendWithParam("86", smsParams.getPhone(),
//                    templateId, params, smsSign, "", "");
//            System.out.println(smsSingleSenderResult);
//            // 如果返回码不是0，说明配置有错，返回错误信息
//            if (smsSingleSenderResult.result == 0) {
//                rep = "success";
//            } else {
//                rep = smsSingleSenderResult.errMsg;
//            }
//        } catch (HTTPException e) {
//            // HTTP响应码错误
//            e.printStackTrace();
//        } catch (JSONException e) {
//            // json解析错误
//            e.printStackTrace();
//        } catch (IOException e) {
//            // 网络IO错误
//            e.printStackTrace();
//        }catch (Exception e) {
//            // 网络IO错误
//            e.printStackTrace();
//        }
//        return rep;
//    }

    public SMSParameter GetVerifyCodeParam(String phoneNumber){
        Random random = new Random();
        Integer verifyCode = random.nextInt(1000000);
        SMSParameter smsParameter=new SMSParameter(phoneNumber,verifyCode.toString());
        return smsParameter;
    }
}

