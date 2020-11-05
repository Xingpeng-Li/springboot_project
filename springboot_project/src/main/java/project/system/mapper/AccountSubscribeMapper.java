package com.project.mapper;

import com.project.domain.AccountSubscribe;
import org.apache.ibatis.annotations.Mapper;

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
}
