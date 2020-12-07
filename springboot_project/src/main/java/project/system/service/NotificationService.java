package project.system.service;

import project.system.domain.Notification;

import java.util.List;
import java.util.Map;

public interface NotificationService {
    List<Map<String,Object>> getNotification(Integer userId);

    void deleteNotification(Integer notificationId);

    Notification selectByPrimaryKey(Integer notificationId);

    int updateByPrimaryKeySelective(Notification record);

    int getUncheckedCount(Integer userId);
}
