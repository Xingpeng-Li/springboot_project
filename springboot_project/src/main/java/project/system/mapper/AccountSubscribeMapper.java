package project.system.mapper;


import org.apache.ibatis.annotations.Mapper;
import project.system.domain.AccountSubscribe;

import java.util.List;

@Mapper
public interface AccountSubscribeMapper {
    int deleteByPrimaryKey(Integer subscribeId);

    int insert(AccountSubscribe record);

    int insertSelective(AccountSubscribe record);

    AccountSubscribe selectByPrimaryKey(Integer subscribeId);

    int updateByPrimaryKeySelective(AccountSubscribe record);

    int updateByPrimaryKey(AccountSubscribe record);

    List<String> selectByUserId(Integer userId);

    int deleteByUserAndAccount(Integer userId,String publicAccountName);
}
