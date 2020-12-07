package project.system.mapper;

import project.system.domain.Backlog;

public interface BacklogMapper {
    int deleteByPrimaryKey(Integer backlogId);

    int insert(Backlog record);

    int insertSelective(Backlog record);

    Backlog selectByPrimaryKey(Integer backlogId);

    int updateByPrimaryKeySelective(Backlog record);

    int updateByPrimaryKey(Backlog record);

    /*
     * @author zws
     * @description 查询已完成的待办事项
     * @create 2020/12/7 11:40
     * @update 2020/12/7 11:40
     * @param [userId]
     * @return project.system.domain.Backlog
     **/
    Backlog selectFinishedBacklogsByUserId(Integer userId);

    /*
     * @author zws
     * @description 查询未完成的待办事项
     * @create 2020/12/7 11:40
     * @update 2020/12/7 11:40
     * @param [userId]
     * @return project.system.domain.Backlog
     **/
    Backlog selectUnFinishedBacklogsByUserId(Integer userId);
}