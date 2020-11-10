package project.system.service.impl;


import org.springframework.stereotype.Service;
import project.system.domain.Notification;
import project.system.domain.User;
import project.system.mapper.NotificationMapper;
import project.system.mapper.UserMapper;
import project.system.service.NotificationService;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 @author:李星鹏
 @createDate:2020/7/16
 @description:消息提醒功能Service
 */

@Service
public class NotificationServiceImpl implements NotificationService {
    @Resource
    NotificationMapper notificationMapper;
    @Resource
    UserMapper userMapper;

    @Override
    public void deleteNotification(Integer notificationId) {
        notificationMapper.deleteByPrimaryKey(notificationId);
    }

    @Override
    public Notification selectByPrimaryKey(Integer notificationId) {
        return notificationMapper.selectByPrimaryKey(notificationId);
    }

    @Override
    public int updateByPrimaryKeySelective(Notification record) {
        return notificationMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public List<Map<String, Object>> getNotification(Integer userId) {
        //找到并返回所有接收者是该成员的消息
        Notification[] notifications = notificationMapper.selectByReceiverId(userId);
        List<Map<String,Object>> notificationList = new ArrayList<>();
        for (Notification notification:notifications) {
            Map<String,Object> map = new HashMap<>();
            map.put("notificationId",notification.getNotificationId());
            Integer senderId = notification.getNotificationSenderId();
            //如果senderId为0，说明该消息是由系统发出
            if(senderId.equals(0)){
                map.put("senderName","系统");
            }
            else{
                User user = userMapper.selectByPrimaryKey(senderId);
                map.put("senderName",user.getUserName());
            }
            map.put("type",notification.getNotificationType());
            Boolean isChecked = false;
            if("是".equals(notification.getNotificationChecked())) {
                isChecked = true;
            }
            map.put("checked",isChecked);
            map.put("body",notification.getNotificationBody());
            //输出日期
            SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time=formatter.format(notification.getNotificationTime());
            map.put("time",time);
            notificationList.add(map);
        }
        return notificationList;
    }
}
