package project.system.service;


import project.system.response.request.LoginRequest;
import project.system.response.response.TokenInfoResponse;

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
