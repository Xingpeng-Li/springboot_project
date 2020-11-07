package project.system.service;

import project.system.domain.PublicAccount;

import java.util.List;

/*
@author WL
@CreateDate 2020-7-22
@update
@description
*/
public interface PublicAccountService {
    //订阅公众号
    void subscribe(Integer userId,Integer publicAccountId);

    //创建公司公众号
    void addCompanyPublicAccount(PublicAccount publicAccount);

    PublicAccount selectByName(String name);

    PublicAccount selectByPrimaryKey(Integer publicaccountId);

    List<PublicAccount> selectByUserId(Integer userId);

    List<PublicAccount> selectByCompanyId(Integer companyId);

    List<PublicAccount> selectByKey(String key);
}
