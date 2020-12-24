package project.system.service.impl;

import org.springframework.stereotype.Service;
import project.system.domain.PublicAccount;
import project.system.mapper.AccountSubscribeMapper;
import project.system.mapper.PublicAccountMapper;
import project.system.service.AccountSubscribeService;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

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
