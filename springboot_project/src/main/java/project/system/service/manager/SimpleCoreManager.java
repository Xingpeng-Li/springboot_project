package project.system.service.manager;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import project.system.response.response.TokenInfoResponse;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/*
@author 李星鹏
@CreateDate 2020-7-09
@update
@description 处理服务器缓存
*/
@Configuration
@Slf4j
public class SimpleCoreManager {

    /**
     * 缓存用户登入信息
     */
    private static final Map<String, TokenInfoResponse> TOKEN_INFO = new ConcurrentHashMap<>(128);

    public Map<String, TokenInfoResponse> getReadOnlyTokenInfo() {
        return Collections.unmodifiableMap(TOKEN_INFO);
    }

    public void addTokenInfo(String token, TokenInfoResponse tokenInfoResponse) {
        TOKEN_INFO.put(token, tokenInfoResponse);
    }
}
