package com.project.domain;

/*
@author WL
@CreateDate 2020-7-08
@update
@description 部门类
*/
public class Dept {
    private Integer deptId;

    private String deptName;

    private Integer deptMaster;

    private Integer companyId;

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName == null ? null : deptName.trim();
    }

    public Integer getDeptMaster() {
        return deptMaster;
    }

    public void setDeptMaster(Integer deptMaster) {
        this.deptMaster = deptMaster;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }
}
