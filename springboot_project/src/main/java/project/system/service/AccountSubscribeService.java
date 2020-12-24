package project.system.service;

import project.system.domain.PublicAccount;

import java.util.List;

public interface AccountSubscribeService {
    //根据用户id查询用户用户订阅的所有公众号
    List<PublicAccount> selectByUserId(Integer userId);
}
