package project.system.service;

import project.system.common.utils.SMSParameter;

public interface SendSmsService {
    /**
     * 发送短信的接口
     *
     * @param phoneNum 手机号
     * @param message     消息
     * @return
     */
    boolean sendSms(String phoneNum, String message);
    public SMSParameter GetVerifyCodeParam(String phoneNumber);
}
