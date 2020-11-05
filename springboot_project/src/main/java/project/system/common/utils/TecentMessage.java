package com.project.common.utils;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Random;
/*
@author DKR
@CreateDate 2020-7-09
@update 2020-7-09 实现注册验证码的生产和注册短信的发送
        2020-7-10 实现其他动态验证码短信的发送
        2020-7-11 更改短信的模板号
@description 与腾讯云短信发送服务接口对接的类
*/
@Component
public class TecentMessage {
    // 短信应用 SDK AppID
    @Value("${tx.sms.appId}")
    int appId;
    // 短信应用SDK AppKey
    @Value("${tx.sms.appKey}")
    String appKey;
    // 短信模板ID
    @Value("${tx.sms.templateId}")
    int templateId ;
    // 签名
    @Value("${tx.sms.smsSign}")
    String smsSign ;
    //验证码有效时间
    @Value("${tx.sms.smsEffectiveTime}")
    String smsEffectiveTime ;

    /**
     * 指定模板 ID 单发短信
     * @param smsParams
     */
    public String sendSms(SMSParameter smsParams) {
        String rep = "error";
        try {
            String verifyCode = smsParams.getVerifyCode();
            // 数组具体的元素个数和模板中变量个数必须一致
            String[] params = {verifyCode,smsEffectiveTime};
            SmsSingleSender smsSingleSender = new SmsSingleSender(appId, appKey);
            // 签名参数未提供或者为空时，会使用默认签名发送短信
            SmsSingleSenderResult smsSingleSenderResult = smsSingleSender.sendWithParam("86", smsParams.getPhone(),
                    templateId, params, smsSign, "", "");
            System.out.println(smsSingleSenderResult);
            // 如果返回码不是0，说明配置有错，返回错误信息
            if (smsSingleSenderResult.result == 0) {
                rep = "success";
            } else {
                rep = smsSingleSenderResult.errMsg;
            }
        } catch (HTTPException e) {
            // HTTP响应码错误
            e.printStackTrace();
        } catch (JSONException e) {
            // json解析错误
            e.printStackTrace();
        } catch (IOException e) {
            // 网络IO错误
            e.printStackTrace();
        }catch (Exception e) {
            // 网络IO错误
            e.printStackTrace();
        }
        return rep;
    }
    public SMSParameter GetVerifyCodeParam(String phoneNumber){
        Random random = new Random();
        Integer verifyCode = random.nextInt(1000000);
        SMSParameter smsParameter=new SMSParameter(phoneNumber,verifyCode.toString());
        return smsParameter;
    }
}
