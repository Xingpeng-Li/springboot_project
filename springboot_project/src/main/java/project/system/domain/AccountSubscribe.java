package com.project.domain;

public class AccountSubscribe {
    private Integer subscribeId;

    private String publicaccountName;

    private Integer userId;

    public Integer getSubscribeId() {
        return subscribeId;
    }

    public void setSubscribeId(Integer subscribeId) {
        this.subscribeId = subscribeId;
    }

    public String getPublicaccountName() {
        return publicaccountName;
    }

    public void setPublicaccountName(String publicaccountName) {
        this.publicaccountName = publicaccountName == null ? null : publicaccountName.trim();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}