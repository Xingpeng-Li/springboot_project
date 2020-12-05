package project.system.mapper;

import project.system.domain.Backlog;

public interface BacklogMapper {
    int deleteByPrimaryKey(Integer backlogId);

    int insert(Backlog record);

    int insertSelective(Backlog record);

    Backlog selectByPrimaryKey(Integer backlogId);

    int updateByPrimaryKeySelective(Backlog record);

    int updateByPrimaryKey(Backlog record);
}