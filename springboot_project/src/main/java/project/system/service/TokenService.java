package com.project.service;
/*
@author DKR
@CreateDate 2020-7-10
@update
@description 包含token相关的服务，生成token，验证token是否有效，从token中获取用户信息
*/
public interface TokenService {
    String createToken(Integer userId);
    Integer getUserId(String token);
    boolean isExpiration(String token);
}
