package com.project.mapper;

import com.project.domain.HealthPunchin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HealthPunchinMapper {
    int deleteByPrimaryKey(Integer punchinId);

    int insert(HealthPunchin record);

    int insertSelective(HealthPunchin record);

    HealthPunchin selectByPrimaryKey(Integer punchinId);

    int updateByPrimaryKeySelective(HealthPunchin record);

    int updateByPrimaryKey(HealthPunchin record);

    HealthPunchin[] selectAll();

    //新添加——徐必成
    HealthPunchin[] selectByUserID(int userId);
}
