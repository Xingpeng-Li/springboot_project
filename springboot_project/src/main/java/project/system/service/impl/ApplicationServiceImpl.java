package project.system.service.impl;

import org.springframework.stereotype.Service;
import project.system.domain.Application;
import project.system.domain.Notification;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;
import project.system.mapper.ApplicationMapper;
import project.system.mapper.NotificationMapper;
import project.system.mapper.UserMapper;
import project.system.service.ApplicationService;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
 @author:李星鹏
 @createDate:2020/7/17
 @description:发送请假、外出、加班审批

 @update:2020/7/18
 @description:添加开始时间不能晚于结束时间限制
 */

@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Resource
    UserMapper userMapper;
    @Resource
    ApplicationMapper applicationMapper;
    @Resource
    NotificationMapper notificationMapper;

    @Override
    public void disposeApplication(Integer notificationId, String isAgree, Integer userId) {
        //若该提醒的对象是一个审批，则该审批的id保存在提醒的body中
        Notification notification = notificationMapper.selectByPrimaryKey(notificationId);
        //提醒是由前端传入,不会出现提醒id不存在的现象
        String applicationId = notification.getNotificationBody();
        Application application = applicationMapper.selectByPrimaryKey(Integer.parseInt(applicationId));
        //提醒发送者审批状态
        Notification notification2 = new Notification();
        notification2.setNotificationSenderId(userId);
        notification2.setNotificationReceiverId(notification.getNotificationSenderId());
        notification2.setNotificationType("已处理审批");
        notification2.setNotificationChecked("否");
        String body;
        if("0".equals(isAgree)){
            body = "您的"+application.getApplicationType()+"申请已通过";
        }
        else {
            body = "您的"+application.getApplicationType()+"申请未通过";
        }
        notification2.setNotificationBody(body);
        Timestamp timestamp = new Timestamp(new Date().getTime());
        notification2.setNotificationTime(timestamp);
        notificationMapper.insert(notification2);
    }

    @Override
    public Map<String, Object> getApplication(Integer notificationId) {
        //若该提醒的对象是一个审批，则该审批的id保存在提醒的body中
        Notification notification = notificationMapper.selectByPrimaryKey(notificationId);
        notification.setNotificationChecked("是");
        notificationMapper.updateByPrimaryKey(notification);
        //提醒是由前端传入,不会出现提醒id不存在的现象
        String applicationId = notification.getNotificationBody();
        Application application = applicationMapper.selectByPrimaryKey(Integer.parseInt(applicationId));
        Map<String, Object> map = new HashMap<>();
        map.put("type",application.getApplicationType());
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTime = formatter.format(application.getApplicationStartTime());
        map.put("startTime",startTime);
        String endTime = formatter.format(application.getApplicationEndTime());
        map.put("endTime",endTime);
        map.put("reason",application.getApplicationReason());
        return map;
    }

    //发送审批
    @Override
    public void sendApplication(String type, String startTime,String endTime,String reason,Integer sender,String approverId,String secondApproverId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date startDate, endDate;
        try {
            startDate = sdf.parse(startTime);
            endDate = sdf.parse(endTime);
        } catch (ParseException e) {
            //日期格式不正确
            throw new BusinessException(EmBusinessError.DATE_FORMAT_ERROR);
        }
        if(endDate.before(startDate)){
            throw new BusinessException(EmBusinessError.DATE_ERROR);
        }
        Integer approver = Integer.parseInt(approverId);
        Integer secondApprover = Integer.parseInt(secondApproverId);
        Application application = new Application();
        application.setApplicantId(sender);
        application.setApplicationStartTime(startDate);
        application.setApplicationEndTime(endDate);
        application.setApplicationReason(reason);
        application.setApplicationType(type);
        application.setApproverId(approver);
        application.setSecondApproverId(secondApprover);
        Timestamp timestamp = new Timestamp(new Date().getTime());
        application.setApplicationTime(timestamp);
        applicationMapper.insertSelective(application);

        //提醒批阅人
        Notification notification = new Notification();
        notification.setNotificationSenderId(sender);
        notification.setNotificationReceiverId(approver);
        notification.setNotificationType("待处理审批");
        notification.setNotificationChecked("否");
        notification.setNotificationBody(application.getApplicationId().toString());
        notification.setNotificationTime(timestamp);
        notificationMapper.insert(notification);

        //提醒抄送人
        Notification notification2 = new Notification();
        notification2.setNotificationSenderId(sender);
        notification2.setNotificationReceiverId(secondApprover);
        notification2.setNotificationType("待查看审批");
        notification2.setNotificationChecked("否");
        notification2.setNotificationBody(application.getApplicationId().toString());
        notification2.setNotificationTime(timestamp);
        notificationMapper.insert(notification2);
    }
}
