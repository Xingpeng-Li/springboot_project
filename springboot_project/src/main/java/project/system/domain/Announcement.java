package project.system.domain;

import java.util.Date;

public class Announcement {
    private Integer announcementId;

    private String announcementType;

    private String announcementTitle;

    private String announcementBody;

    private String announcementUrl;

    private Integer announcementSender;

    private Date announcementDate;

    public Integer getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(Integer announcementId) {
        this.announcementId = announcementId;
    }

    public String getAnnouncementType() {
        return announcementType;
    }

    public void setAnnouncementType(String announcementType) {
        this.announcementType = announcementType == null ? null : announcementType.trim();
    }

    public String getAnnouncementTitle() {
        return announcementTitle;
    }

    public void setAnnouncementTitle(String announcementTitle) {
        this.announcementTitle = announcementTitle == null ? null : announcementTitle.trim();
    }

    public String getAnnouncementBody() {
        return announcementBody;
    }

    public void setAnnouncementBody(String announcementBody) {
        this.announcementBody = announcementBody == null ? null : announcementBody.trim();
    }

    public String getAnnouncementUrl() {
        return announcementUrl;
    }

    public void setAnnouncementUrl(String announcementUrl) {
        this.announcementUrl = announcementUrl == null ? null : announcementUrl.trim();
    }

    public Integer getAnnouncementSender() {
        return announcementSender;
    }

    public void setAnnouncementSender(Integer announcementSender) {
        this.announcementSender = announcementSender;
    }

    public Date getAnnouncementDate() {
        return announcementDate;
    }

    public void setAnnouncementDate(Date announcementDate) {
        this.announcementDate = announcementDate;
    }
}