package com.project.mapper;

import com.project.domain.WorkReport;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WorkReportMapper {
    int deleteByPrimaryKey(Integer reportId);

    int insert(WorkReport record);

    int insertSelective(WorkReport record);

    WorkReport selectByPrimaryKey(Integer reportId);

    int updateByPrimaryKeySelective(WorkReport record);

    int updateByPrimaryKey(WorkReport record);
}
