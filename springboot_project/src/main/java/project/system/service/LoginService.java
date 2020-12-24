package project.system.service;


import project.system.response.request.LoginRequest;
import project.system.response.response.TokenInfoResponse;

/*
@author zws
@CreateDate 2020-11-18
@update
@description 登录登出相关接口
*/
public interface LoginService {

    TokenInfoResponse checkLogin(String token);

    void setLoginResult(String token, TokenInfoResponse tokenInfoResponse);

}
