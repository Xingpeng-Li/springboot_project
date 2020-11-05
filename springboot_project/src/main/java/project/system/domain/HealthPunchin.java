package com.project.domain;

import java.util.Date;

public class HealthPunchin {
    private Integer punchinId;

    private Integer userId;

    private Double userBodyTemp;

    private String city;

    private String province;

    private Date punchinDate;

    private String punchinNote;

    private String contactSuspectedCase;

    private String userHealthStatus;

    public Integer getPunchinId() {
        return punchinId;
    }

    public void setPunchinId(Integer punchinId) {
        this.punchinId = punchinId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Double getUserBodyTemp() {
        return userBodyTemp;
    }

    public void setUserBodyTemp(Double userBodyTemp) {
        this.userBodyTemp = userBodyTemp;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province == null ? null : province.trim();
    }

    public Date getPunchinDate() {
        return punchinDate;
    }

    public void setPunchinDate(Date punchinDate) {
        this.punchinDate = punchinDate;
    }

    public String getPunchinNote() {
        return punchinNote;
    }

    public void setPunchinNote(String punchinNote) {
        this.punchinNote = punchinNote == null ? null : punchinNote.trim();
    }

    public String getContactSuspectedCase() {
        return contactSuspectedCase;
    }

    public void setContactSuspectedCase(String contactSuspectedCase) {
        this.contactSuspectedCase = contactSuspectedCase == null ? null : contactSuspectedCase.trim();
    }

    public String getUserHealthStatus() {
        return userHealthStatus;
    }

    public void setUserHealthStatus(String userHealthStatus) {
        this.userHealthStatus = userHealthStatus == null ? null : userHealthStatus.trim();
    }
}