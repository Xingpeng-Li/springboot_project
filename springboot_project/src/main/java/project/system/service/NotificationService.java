package project.system.service;

import java.util.List;
import java.util.Map;

public interface NotificationService {
    List<Map<String,Object>> getNotification(Integer userId);

    void deleteNotification(Integer notificationId);
}
