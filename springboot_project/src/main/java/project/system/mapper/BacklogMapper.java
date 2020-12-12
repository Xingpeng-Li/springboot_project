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

    /*
     * @author zws
     * @description 查询已完成的待办事项
     * @create 2020/12/7 11:40
     * @update 2020/12/7 11:40
     * @param [userId]
     * @return project.system.domain.Backlog
     **/
    List<Backlog> selectFinishedBacklogsByUserId(Integer userId);

    /*
     * @author zws
     * @description 查询未完成的待办事项
     * @create 2020/12/7 11:40
     * @update 2020/12/7 11:40
     * @param [userId]
     * @return project.system.domain.Backlog
     **/
    List<Backlog> selectUnFinishedBacklogsByUserId(Integer userId);

    /*
     * @author zws
     * @description 检测超时的backlog并进行修改
     * @create 2020/12/8 12:24
     * @update 2020/12/8 12:24
     * @param []
     * @return void
     **/
    void checkOverTimedBacklogs(Date currentTime);

    /*
     * @author zws
     * @description 获取刚修改过的backlog
     * @create 2020/12/8 12:25
     * @update 2020/12/8 12:25
     * @param []
     * @return java.util.List<project.system.domain.Backlog>
     **/
    List<Backlog> getChangedBacklogs(Date currentTime);



}