package project.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import project.system.domain.Backlog;

import java.util.Date;
import java.util.List;

@Mapper
public interface BacklogMapper {
    int deleteByPrimaryKey(Integer backlogId);

    int insert(Backlog record);

    int insertSelective(Backlog record);

    Backlog selectByPrimaryKey(Integer backlogId);

    int updateByPrimaryKeySelective(Backlog record);

    int updateByPrimaryKey(Backlog record);

    List<Backlog> selectFinishedBacklogsByUserId(Integer userId);

    List<Backlog> selectUnFinishedBacklogsByUserId(Integer userId);

    void checkOverTimedBacklogs(Date currentTime);

    List<Backlog> getChangedBacklogs(Date currentTime);

}