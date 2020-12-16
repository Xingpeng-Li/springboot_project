package project.system.service.impl;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import project.system.domain.Notification;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;
import project.system.mapper.BacklogMapper;
import project.system.mapper.NotificationMapper;
import project.system.service.BackLogService;
import project.system.domain.Backlog;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zws
 * @version 1.0
 * @class BackLogServiceImpl
 * @description 待办事项
 * * @date 2020/12/5 11:44
 */
@Service
@Component
@EnableScheduling
public class BackLogServiceImpl implements BackLogService {

    @Resource
    BacklogMapper backlogMapper;
    @Resource
    NotificationMapper notificationMapper;


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
        backlogMapper.insertSelective(backlog);
    }

    @Override
    public List<Backlog> getNotFinishedBackLogs(Integer userId) {

        List<Backlog> backlogs= backlogMapper.selectUnFinishedBacklogsByUserId(userId);
        return backlogs;
    }

    @Override
    public List<Backlog> getFinishedBackLogs(Integer userId) {
        List<Backlog> backlogs= backlogMapper.selectFinishedBacklogsByUserId(userId);
        return backlogs;
    }

    @Scheduled(cron="0 * */1 * * ?")
    @Override
    public void checkOverTimedBacklogs() {

        Date currentTime=new Date();
        backlogMapper.checkOverTimedBacklogs(currentTime);
        List<Backlog> backlogs=backlogMapper.getChangedBacklogs(currentTime);
        for (Backlog backlog:
             backlogs) {
            Notification notification = new Notification();
            notification.setNotificationSenderId(0);
            notification.setNotificationReceiverId(backlog.getUserId());
            notification.setNotificationType("待办事项过期");
            notification.setNotificationChecked("否");
            notification.setNotificationBody(backlog.getTitle());
            notification.setNotificationTime(new Date());
            notificationMapper.insert(notification);
        }
    }

    @Override
    public void finishBackLog(Integer backLogId) {
        Backlog backlog=backlogMapper.selectByPrimaryKey(backLogId);
        backlog.setIsFinished(true);
        backlogMapper.updateByPrimaryKey(backlog);
    }

    @Override
    public void deleteBackLog(Integer backLogId) {
        backlogMapper.deleteByPrimaryKey(backLogId);
    }

    @Override
    public void updateBackLog(Integer userId, String title, String description, Boolean isFinished, Boolean isOverTime, String endTime) {

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
        backlogMapper.updateByPrimaryKey(backlog);
    }

    @Override
    public void overBackLog(Integer backLogId) {
        Backlog backlog=backlogMapper.selectByPrimaryKey(backLogId);
        backlog.setIsOvertime(true);
        backlogMapper.updateByPrimaryKey(backlog);
    }


}