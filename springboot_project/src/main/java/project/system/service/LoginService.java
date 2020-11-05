package com.project.service;

import com.project.response.request.LoginRequest;
import com.project.response.response.TokenInfoResponse;

/*
@author WL
@CreateDate 2020-7-09
@update
@description 登录登出相关接口
*/
public interface LoginService {

    TokenInfoResponse logout(LoginRequest loginParam);

    TokenInfoResponse checkLogin(String token);

    void setLoginResult(String token, TokenInfoResponse tokenInfoResponse);

}
