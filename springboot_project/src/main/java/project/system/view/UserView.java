package com.project.view;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
@ApiModel
public class UserView {
    @ApiModelProperty(value = "用户id",notes = "用户唯一标识")
    private Integer userId;
    @ApiModelProperty("用户名")
    private String userName;
    @ApiModelProperty("用户手机号码")
    private String userPhonenumber;
    @ApiModelProperty("用户的部门名字")
    private String deptName;
    @ApiModelProperty("用户的公司名字")
    private String companyName;
    @ApiModelProperty("用户性别")
    private String userGender;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhonenumber() {
        return userPhonenumber;
    }

    public void setUserPhonenumber(String userPhonenumber) {
        this.userPhonenumber = userPhonenumber;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
