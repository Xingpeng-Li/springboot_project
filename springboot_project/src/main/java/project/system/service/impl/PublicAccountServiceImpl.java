package com.project.service.impl;

import com.project.domain.AccountSubscribe;
import com.project.domain.PublicAccount;
import com.project.error.BusinessException;
import com.project.error.EmBusinessError;
import com.project.mapper.AccountSubscribeMapper;
import com.project.mapper.PublicAccountMapper;
import com.project.service.AccountSubscribeService;
import com.project.service.PublicAccountService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/*
@author WL
@CreateDate 2020-7-21
@update
@description 公众号业务逻辑操作
*/
@Service
public class PublicAccountServiceImpl implements PublicAccountService {

    @Resource
    private PublicAccountMapper publicAccountMapper;

    @Resource
    private AccountSubscribeMapper accountSubscribeMapper;

    @Resource
    private AccountSubscribeService accountSubscribeService;


    //处理用户订阅公众号具体业务逻辑
    @Override
    public void subscribe(Integer userId, Integer publicAccountId) {
        PublicAccount publicAccount = publicAccountMapper.selectByPrimaryKey(publicAccountId);
        if (publicAccount == null) {
            throw new BusinessException(EmBusinessError.PUBLIC_ACCOUNT_NOT_EXISTS);
        }
        List<PublicAccount> publicAccounts = accountSubscribeService.selectByUserId(userId);
        List<PublicAccount> publicAccounts1 = publicAccountMapper.selectByUserId(userId);
        boolean hasSubscribed = publicAccounts.stream().anyMatch(account -> publicAccountId.equals(account.getPublicaccountId())) || publicAccounts1.stream().anyMatch(account -> publicAccountId.equals(account.getPublicaccountId()));

        if (hasSubscribed) {
            throw new BusinessException(EmBusinessError.PUBLIC_ACCOUNT_HAS_SUBSCRIBED);
        }
        AccountSubscribe accountSubscribe = new AccountSubscribe();
        accountSubscribe.setPublicaccountName(publicAccount.getPublicaccountName());
        accountSubscribe.setUserId(userId);
        int affectedRows = accountSubscribeMapper.insertSelective(accountSubscribe);
        if (affectedRows == 0) {
            throw new BusinessException(EmBusinessError.DB_ERROR);
        }
    }

    //处理创建公司公众号具体业务逻辑
    @Override
    public void addCompanyPublicAccount(PublicAccount publicAccount) {
        int affectedRows = publicAccountMapper.insertSelective(publicAccount);
        if (affectedRows == 0)
            throw new BusinessException(EmBusinessError.DB_ERROR);
    }
}
