package project.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;
import project.system.response.CommonReturnType;
import project.system.service.SendSmsService;

@RestController
public class SendSmsController {
    @Autowired
    private SendSmsService sendSmsService;

    @PostMapping(value = "/sendSms")
    public CommonReturnType sendSms(String phoneNum,String verifyCode) {
        boolean sendFlag = sendSmsService.sendSms(phoneNum,verifyCode);
        if (sendFlag){
            return CommonReturnType.create("发送成功！");
        }else {
            throw new BusinessException(EmBusinessError.USER_PHONE_ERROR);
        }
    }
}
