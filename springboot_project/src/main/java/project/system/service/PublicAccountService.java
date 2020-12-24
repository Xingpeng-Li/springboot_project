package project.system.service;

import project.system.domain.PublicAccount;

import java.util.List;
public interface PublicAccountService {
    //订阅公众号
    void subscribe(Integer userId,Integer publicAccountId);
    //取消订阅公众号
    void unsubscribe(Integer userId,Integer publicAccountId);

    //创建公司公众号
    void addCompanyPublicAccount(PublicAccount publicAccount);

    PublicAccount selectByName(String name);

    PublicAccount selectByPrimaryKey(Integer publicaccountId);

    List<PublicAccount> selectByUserId(Integer userId);

    List<PublicAccount> selectByCompanyId(Integer companyId);

    List<PublicAccount> selectByKey(String key);
}
