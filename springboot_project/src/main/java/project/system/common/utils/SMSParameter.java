package project.system.common.utils;

import lombok.Data;
import lombok.experimental.Accessors;
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
