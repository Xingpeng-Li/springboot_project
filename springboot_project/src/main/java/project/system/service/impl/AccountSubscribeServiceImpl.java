package com.project.service.impl;

import com.project.domain.PublicAccount;
import com.project.mapper.AccountSubscribeMapper;
import com.project.mapper.PublicAccountMapper;
import com.project.service.AccountSubscribeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/*
@author WL
@CreateDate 2020-7-21
@update
@description 公众号订阅业务逻辑编写
*/
@Service
public class AccountSubscribeServiceImpl implements AccountSubscribeService {

    @Resource
    private AccountSubscribeMapper accountSubscribeMapper;

    @Resource
    private PublicAccountMapper publicAccountMapper;

    //根据用户id查询订阅的所有公众号
    @Override
    public List<PublicAccount> selectByUserId(Integer userId) {
        List<String> publicAccountNames = accountSubscribeMapper.selectByUserId(userId);
        return publicAccountNames.stream().map(publicAccountMapper::selectByName).collect(Collectors.toList());
    }
}
