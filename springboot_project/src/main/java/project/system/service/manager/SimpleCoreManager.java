package com.project.service.manager;

import com.project.error.BusinessException;
import com.project.error.EmBusinessError;
import com.project.response.response.TokenInfoResponse;
import io.openvidu.java.client.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/*
@author WL
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

    /**
     * 缓存会议-成员token信息
     */
    private static final Map<String, List<String>> GROUP_REF_MEMBER = new ConcurrentHashMap<>(16);

    /**
     * 缓存-openvidu录像id
     */
    private static final Map<String, String> OPENVIDU_RECORDINGS = new ConcurrentHashMap<>(16);

    @Resource
    private OpenVidu openVidu;

    @Bean
    public OpenVidu openVidu(@Value("${openvidu.secret}") String secret, @Value("${openvidu.publicurl:${openvidu.url}}") String openviduUrl) {
        return new OpenVidu(openviduUrl, secret);
    }

    public Map<String, TokenInfoResponse> getReadOnlyTokenInfo() {
        return Collections.unmodifiableMap(TOKEN_INFO);
    }

    public Map<String, List<String>> getReadOnlyGroupRefMember() {
        return Collections.unmodifiableMap(GROUP_REF_MEMBER);
    }

    public Map<String, String> getOpenviduRecordings() {
        return Collections.unmodifiableMap(OPENVIDU_RECORDINGS);
    }

    public void addTokenInfo(String token, TokenInfoResponse tokenInfoResponse) {
        TOKEN_INFO.put(token, tokenInfoResponse);
    }

    public void addOpenviduRecord(String groupUniqueId, String recordingId) {
        OPENVIDU_RECORDINGS.put(groupUniqueId, recordingId);
    }

    public void addOpenviduToken(String token, String openviduToken) throws BusinessException {
        if (Objects.isNull(TOKEN_INFO.get(token)) || !Boolean.TRUE.equals(TOKEN_INFO.get(token).getIsLogin())) {
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
        TOKEN_INFO.get(token).setOpenviduToken(openviduToken);
    }

    public void addGroupMember(String groupUniqueId, String token) throws BusinessException {
        if (Objects.isNull(TOKEN_INFO.get(token)) || !Boolean.TRUE.equals(TOKEN_INFO.get(token).getIsLogin())) {
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
        TOKEN_INFO.get(token).setGroupUniqueId(groupUniqueId);
        if (!GROUP_REF_MEMBER.containsKey(groupUniqueId)) {
            GROUP_REF_MEMBER.put(groupUniqueId, new CopyOnWriteArrayList<>());
        }
        GROUP_REF_MEMBER.get(groupUniqueId).add(token);
    }

    public void removeToken(String token) {
        TokenInfoResponse tokenInfoResponse = TOKEN_INFO.get(token);
        if (Objects.nonNull(tokenInfoResponse)) {
            String groupUniqueId = tokenInfoResponse.getGroupUniqueId();
            if (StringUtils.isNotBlank(groupUniqueId)) {
                // 将用户token移除会议
                GROUP_REF_MEMBER.get(groupUniqueId).remove(token);
                // 会议里没有成员时关闭会议
                if (GROUP_REF_MEMBER.get(groupUniqueId).size() <= 0) {
                    GROUP_REF_MEMBER.remove(groupUniqueId);
                }
            }
        }
    }

    public Session getOpenviduSession(SessionProperties properties) throws OpenViduJavaClientException, OpenViduHttpException {
        return this.openVidu.createSession(properties);
    }

    public Session getOpenviduSessionById(String sessionId) {
        if (Objects.isNull(sessionId) || CollectionUtils.isEmpty(openVidu.getActiveSessions())) {
            return null;
        }
        return openVidu.getActiveSessions().stream().filter(session -> sessionId.equals(session.getSessionId())).findFirst().orElse(null);
    }
}
