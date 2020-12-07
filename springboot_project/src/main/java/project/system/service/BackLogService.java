package project.system.service;

import project.system.domain.Backlog;

import java.util.Date;
import java.util.List;

public interface BackLogService {

    /*
     * @author zws
     * @description 创建一个待办事项
     * @create 2020/12/5 11:55
     * @update 2020/12/5 11:55
     * @param [userId, endTime]
     * @return void
     **/
    void createBackLog(Integer userId,String title,String description,Boolean isFinished,Boolean isOverTime, String endTime);

    /*
     * @author zws
     * @description 获取未完成的待办事项
     * @create 2020/12/5 12:06
     * @update 2020/12/5 12:06
     * @param []
     * @return java.util.List<project.system.domain.Backlog>
     **/
    List<Backlog> getNotFinishedBackLogs(Integer userId);

    /*
     * @author zws
     * @description 获取已完成的待办事项
     * @create 2020/12/5 12:07
     * @update 2020/12/5 12:07
     * @param []
     * @return java.util.List<project.system.domain.Backlog>
     **/
    List<Backlog> getFinishedBackLogs(Integer userId);

    /*
     * @author zws
     * @description 根据待办事项id获取截至时间
     * @create 2020/12/5 12:09
     * @update 2020/12/5 12:09
     * @param [backLogId]
     * @return java.util.Date
     **/
    Date getBackLogEndTime(Integer backLogId);

    /*
     * @author zws
     * @description 完成一个待办事项
     * @create 2020/12/5 12:10
     * @update 2020/12/5 12:10
     * @param [backLogId]
     * @return void
     **/
    void finishBackLog(Integer backLogId);

    /*
     * @author zws
     * @description 将一个待办事项设为超时
     * @create 2020/12/5 12:11
     * @update 2020/12/5 12:11
     * @param [backLogId]
     * @return void
     **/
    void overBackLog(Integer backLogId);
}
