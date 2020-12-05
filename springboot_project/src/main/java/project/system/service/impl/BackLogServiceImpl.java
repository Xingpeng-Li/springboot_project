package project.system.service.impl;

import org.springframework.stereotype.Service;
import project.system.mapper.BacklogMapper;
import project.system.service.BackLogService;
import project.system.domain.Backlog;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author zws
 * @version 1.0
 * @class BackLogServiceImpl
 * @description 待办事项
 * * @date 2020/12/5 11:44
 */
@Service
public class BackLogServiceImpl implements BackLogService {

    @Resource
    BacklogMapper backlogMapper;

    @Override
    public void createBackLog(Integer userId, Date endTime) {

    }

    @Override
    public List<Backlog> getNotFinishedBackLogs() {
        return null;
    }

    @Override
    public List<Backlog> getFinishedBackLogs() {
        return null;
    }

    @Override
    public Date getBackLogEndTime(Integer backLogId) {
        Backlog backlog=backlogMapper.selectByPrimaryKey(backLogId);
        return backlog.getEndTime();
    }

    @Override
    public void finishBackLog(Integer backLogId) {
        Backlog backlog=backlogMapper.selectByPrimaryKey(backLogId);
        backlog.setIsFinished(true);
    }

    @Override
    public void overBackLog(Integer backLogId) {
        Backlog backlog=backlogMapper.selectByPrimaryKey(backLogId);
        backlog.setIsOvertime(true);
    }
}