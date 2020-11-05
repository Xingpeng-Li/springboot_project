package com.project.mapper;

import com.project.domain.Application;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApplicationMapper {
    int deleteByPrimaryKey(Integer applicationId);

    int insert(Application record);

    int insertSelective(Application record);

    Application selectByPrimaryKey(Integer applicationId);

    int updateByPrimaryKeySelective(Application record);

    int updateByPrimaryKey(Application record);
}
