package project.system.service.impl;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;
import project.system.mapper.BacklogMapper;
import project.system.service.BackLogService;
import project.system.domain.Backlog;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
@Component
public class BackLogServiceImpl implements BackLogService {

    @Resource
    BacklogMapper backlogMapper;


    @Override
    public void createBackLog(Integer userId, String title, String description, Boolean isFinished, Boolean isOverTime, String endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date endDate;
        try {
            endDate = sdf.parse(endTime);
        } catch (ParseException e) {
            //日期格式不正确
            throw new BusinessException(EmBusinessError.DATE_FORMAT_ERROR);
        }
        Backlog backlog=new Backlog();
        backlog.setUserId(userId);
        backlog.setTitle(title);
        backlog.setDescription(description);
        backlog.setIsFinished(isFinished);
        backlog.setIsOvertime(isOverTime);
        backlog.setEndTime(endDate);
        backlogMapper.insert(backlog);
    }

    @Override
    public List<Backlog> getNotFinishedBackLogs(Integer userId) {

        List<Backlog> backlogs= Arrays.asList(backlogMapper.selectFinishedBacklogsByUserId(userId));
        return backlogs;
    }

    @Override
    public List<Backlog> getFinishedBackLogs(Integer userId) {
        List<Backlog> backlogs= Arrays.asList(backlogMapper.selectUnFinishedBacklogsByUserId(userId));
        return backlogs;
    }

    @Scheduled(cron="*/5 * * * * ?")
    @Override
    public Date getBackLogEndTime(Integer backLogId) {
        backLogId=1;
        Backlog backlog=backlogMapper.selectByPrimaryKey(backLogId);
        backlog.setUserId(backlog.getUserId()+1);
        backlogMapper.updateByPrimaryKey(backlog);
        return backlog.getEndTime();
    }

    @Override
    public void finishBackLog(Integer backLogId) {
        Backlog backlog=backlogMapper.selectByPrimaryKey(backLogId);
        backlog.setIsFinished(true);
        backlogMapper.updateByPrimaryKey(backlog);
    }

    @Override
    public void overBackLog(Integer backLogId) {
        Backlog backlog=backlogMapper.selectByPrimaryKey(backLogId);
        backlog.setIsOvertime(true);
        backlogMapper.updateByPrimaryKey(backlog);
    }


}