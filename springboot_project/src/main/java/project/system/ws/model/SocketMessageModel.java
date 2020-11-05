package project.system.ws.model;

import java.util.Date;

/*
@author WL
@CreateDate 2020-7-15
@update 2020-7-16
@description 是负责传给前端的消息的类，比SocketMessage类多了两个字段，分别是发送者名字和接收者名字
*/
public class SocketMessageModel {
    private Integer id;

    private String launchId;

    private String launchUserName;

    private String receiveId;

    private String receiveUserName;

    private String content;

    private Integer type;

    private Date createTime;

    private Integer deptId;

    private Integer companyId;

    public SocketMessageModel(Integer id, String launchId, String launchUserName, String receiveId, String receiveUserName, String content, Integer type, Date createTime, Integer deptId, Integer companyId) {
        this.id = id;
        this.launchId = launchId;
        this.launchUserName = launchUserName;
        this.receiveId = receiveId;
        this.receiveUserName = receiveUserName;
        this.content = content;
        this.type = type;
        this.createTime = createTime;
        this.deptId = deptId;
        this.companyId = companyId;
    }

    public SocketMessageModel(Integer id, String launchId, String launchUserName, String receiveId, String receiveUserName, String content, Integer type, Date createTime) {
        this.id = id;
        this.launchId = launchId;
        this.launchUserName = launchUserName;
        this.receiveId = receiveId;
        this.receiveUserName = receiveUserName;
        this.content = content;
        this.type = type;
        this.createTime = createTime;
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

    public SocketMessageModel() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLaunchId() {
        return launchId;
    }

    public void setLaunchId(String launchId) {
        this.launchId = launchId == null ? null : launchId.trim();
    }

    public String getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(String receiveId) {
        this.receiveId = receiveId == null ? null : receiveId.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getLaunchUserName() {
        return launchUserName;
    }

    public void setLaunchUserName(String launchUserName) {
        this.launchUserName = launchUserName;
    }

    public String getReceiveUserName() {
        return receiveUserName;
    }

    public void setReceiveUserName(String receiveUserName) {
        this.receiveUserName = receiveUserName;
    }
}
