package project.system.common.utils;

import lombok.Data;
import lombok.experimental.Accessors;
/*
@author DKR
@CreateDate 2020-7-09
@update
@description 短信发送服务的参数类
*/
@Data
@Accessors(chain = true)
public class SMSParameter {
    /**
     * 验证码
     */
    private String verifyCode;

    /**
     * 手机号码
     */
    private String phone;


    public SMSParameter(String phone, String verifyCode) {
        this.phone = phone;
        this.verifyCode = verifyCode;
    }
}
