package project.system.service.impl;


import org.springframework.stereotype.Service;
import project.system.domain.AccountSubscribe;
import project.system.domain.PublicAccount;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;
import project.system.mapper.AccountSubscribeMapper;
import project.system.mapper.PublicAccountMapper;
import project.system.service.AccountSubscribeService;
import project.system.service.PublicAccountService;

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
        List<PublicAccount> publicAccounts = accountSubscribeService.selectByUserId(userId);//已订阅的数据库里面查找
        List<PublicAccount> publicAccounts1 = publicAccountMapper.selectByUserId(userId);//公众号的数据库里面查找
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

    @Override
    public PublicAccount selectByName(String name) {
        return publicAccountMapper.selectByName(name);
    }

    @Override
    public PublicAccount selectByPrimaryKey(Integer publicaccountId) {
        return publicAccountMapper.selectByPrimaryKey(publicaccountId);
    }

    @Override
    public List<PublicAccount> selectByCompanyId(Integer companyId) {
        return publicAccountMapper.selectByCompanyId(companyId);
    }

    @Override
    public List<PublicAccount> selectByUserId(Integer userId) {
        return publicAccountMapper.selectByUserId(userId);
    }

    @Override
    public List<PublicAccount> selectByKey(String key) {
        return publicAccountMapper.selectByKey(key);
    }
}
