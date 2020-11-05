package com.project.domain;

public class Company {
    private Integer companyId;

    private String companyName;

    private Integer companyAdmin;

    private String companyInviteCode;

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName == null ? null : companyName.trim();
    }

    public Integer getCompanyAdmin() {
        return companyAdmin;
    }

    public void setCompanyAdmin(Integer companyAdmin) {
        this.companyAdmin = companyAdmin;
    }

    public String getCompanyInviteCode() {
        return companyInviteCode;
    }

    public void setCompanyInviteCode(String companyInviteCode) {
        this.companyInviteCode = companyInviteCode == null ? null : companyInviteCode.trim();
    }
}