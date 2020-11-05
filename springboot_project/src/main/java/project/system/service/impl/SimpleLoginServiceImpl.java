package com.project.service.impl;

import com.project.response.request.LoginRequest;
import com.project.response.response.TokenInfoResponse;
import com.project.service.LoginService;
import com.project.service.manager.SimpleCoreManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/*
@author WL
@CreateDate 2020-7-10
@update
@description 登录接口简单实现，与视频会议相关
*/
@Service
public class SimpleLoginServiceImpl implements LoginService {

    @Resource
    private SimpleCoreManager simpleCoreManager;

    //清除浏览器缓存
    @Override
    public TokenInfoResponse logout(LoginRequest loginParam) {
        if (Objects.nonNull(loginParam) && Objects.nonNull(loginParam.getToken())) {
            simpleCoreManager.removeToken(loginParam.getToken());
        }
        return null;
    }

    //检查用户是否登录
    @Override
    public TokenInfoResponse checkLogin(String token) {
        if (Objects.isNull(token)) {
            return null;
        }
        return simpleCoreManager.getReadOnlyTokenInfo().get(token);
    }

    //用户登录信息写入服务器缓存
    @Override
    public void setLoginResult(String token, TokenInfoResponse tokenInfoResponse) {
        simpleCoreManager.addTokenInfo(token, tokenInfoResponse);
    }
}
