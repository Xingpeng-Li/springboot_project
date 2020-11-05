package project.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import project.system.domain.WorkReport;

@Mapper
public interface WorkReportMapper {
    int deleteByPrimaryKey(Integer reportId);

    int insert(WorkReport record);

    int insertSelective(WorkReport record);

    WorkReport selectByPrimaryKey(Integer reportId);

    int updateByPrimaryKeySelective(WorkReport record);

    int updateByPrimaryKey(WorkReport record);
}
