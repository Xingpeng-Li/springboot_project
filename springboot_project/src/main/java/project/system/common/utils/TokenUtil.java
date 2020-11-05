package project.system.common.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
/*
@author DKR
@CreateDate 2020-7-10
@update
@description jwt框架生成token的基本配置类
*/
@Component
public class TokenUtil {
    @Value("${config.jwt.secret}")
    private String secret;
    @Value("${config.jwt.expire}")
    private Long expire;
    @Value("${config.jwt.header}")
    private  String header;
    @Value("${config.jwt.prefix}")
    private  String prefix;
    @Value("${config.jwt.iss}")
    private  String iss;

    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Long getExpire() {
        return expire;
    }

    public void setExpire(Long  expire) {
        this.expire = expire;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
