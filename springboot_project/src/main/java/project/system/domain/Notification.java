package com.project.domain;

import java.util.Date;

public class Notification {
    private Integer notificationId;

    private Integer notificationReceiverId;

    private Integer notificationSenderId;

    private String notificationType;

    private String notificationChecked;

    private String notificationBody;

    private Date notificationTime;

    public Integer getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Integer notificationId) {
        this.notificationId = notificationId;
    }

    public Integer getNotificationReceiverId() {
        return notificationReceiverId;
    }

    public void setNotificationReceiverId(Integer notificationReceiverId) {
        this.notificationReceiverId = notificationReceiverId;
    }

    public Integer getNotificationSenderId() {
        return notificationSenderId;
    }

    public void setNotificationSenderId(Integer notificationSenderId) {
        this.notificationSenderId = notificationSenderId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType == null ? null : notificationType.trim();
    }

    public String getNotificationChecked() {
        return notificationChecked;
    }

    public void setNotificationChecked(String notificationChecked) {
        this.notificationChecked = notificationChecked == null ? null : notificationChecked.trim();
    }

    public String getNotificationBody() {
        return notificationBody;
    }

    public void setNotificationBody(String notificationBody) {
        this.notificationBody = notificationBody == null ? null : notificationBody.trim();
    }

    public Date getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(Date notificationTime) {
        this.notificationTime = notificationTime;
    }
}