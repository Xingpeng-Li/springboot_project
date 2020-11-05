package project.system.domain;

import project.system.common.utils.Md5Utils;

public class User {

    private Integer userId;

    private String userName;

    private String userPhonenumber;

    private String userPassword;

    private Integer deptId;

    private Integer companyId;

    private String userGender;

    private String userPosition;

    private String socketId;

    private String userEmail;

    private String emailAuthorizationCode;
    public User(){}
    public User (Integer userId,String userName,String password,String phoneNumber){
        this.setUserId(userId);
        if(userName!=null)
            this.setUserName(userName);
        if(password!=null)
        {
            this.setUserPassword(Md5Utils.inputPassToDBPass(password,phoneNumber+"miaowhu"));
        }
        if(phoneNumber!=null)
            this.setUserPhonenumber(phoneNumber);
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getUserPhonenumber() {
        return userPhonenumber;
    }

    public void setUserPhonenumber(String userPhonenumber) {
        this.userPhonenumber = userPhonenumber == null ? null : userPhonenumber.trim();
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword == null ? null : userPassword.trim();
    }

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender == null ? null : userGender.trim();
    }

    public String getUserPosition() {
        return userPosition;
    }

    public void setUserPosition(String userPosition) {
        this.userPosition = userPosition == null ? null : userPosition.trim();
    }

    public String getSocketId() {
        return socketId;
    }

    public void setSocketId(String socketId) {
        this.socketId = socketId == null ? null : socketId.trim();
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail == null ? null : userEmail.trim();
    }

    public String getEmailAuthorizationCode() {
        return emailAuthorizationCode;
    }

    public void setEmailAuthorizationCode(String emailAuthorizationCode) {
        this.emailAuthorizationCode = emailAuthorizationCode == null ? null : emailAuthorizationCode.trim();
    }
}
