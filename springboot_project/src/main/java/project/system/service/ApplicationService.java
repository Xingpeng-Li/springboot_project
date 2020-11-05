package com.project.service;

import java.util.Map;

public interface ApplicationService {
    void sendApplication(String type, String startTime, String endTime, String reason, Integer sender, String approver, String secondApprover);

    Map<String,Object> getApplication(Integer notificationId);

    void disposeApplication(Integer notificationId, String isAgree, Integer userId);
}
