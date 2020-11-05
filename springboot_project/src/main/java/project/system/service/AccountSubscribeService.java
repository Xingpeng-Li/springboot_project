package com.project.service;

import com.project.domain.PublicAccount;

import java.util.List;

/*
@author WL
@CreateDate 2020-7-22
@update
@description
*/
public interface AccountSubscribeService {
    //根据用户id查询用户用户订阅的所有公众号
    List<PublicAccount> selectByUserId(Integer userId);
}
