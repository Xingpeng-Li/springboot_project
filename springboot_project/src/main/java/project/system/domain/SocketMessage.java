package project.system.domain;

import java.util.Date;

/*
@author WL
@CreateDate 2020-7-15
@update 2020-7-16
@description 聊天消息记录
*/
public class SocketMessage {
    private Integer id;

    //消息发送人id，客户端提交为sessionId(socketId),服务器端如果为0表示为系统消息，否则为发送者id
    private String launchId;

    //消息接收者id,客户端提交分为两种情况，若对方在线则提交socketId,否则提交userId,客户端如果是群聊则为-1，
    // 服务器端如果为群聊则为0，否则为接收者id
    private String receiveId;

    //消息内容
    private String content;

    //消息类型 0系统消息(登录登出)、1系统消息(当前在线的列表)、2公司全部员工、3文本消息、4图片消息、5强制下线、30不在线的文本消息(仅为客户端提交)、40不在线的图片消息(仅为客户端提交)
    private Integer type;

    private Date createTime;

    private Integer deptId;

    private Integer companyId;

    public SocketMessage(Integer id, String launchId, String receiveId, String content, Integer type, Date createTime, Integer deptId, Integer companyId) {
        this.id = id;
        this.launchId = launchId;
        this.receiveId = receiveId;
        this.content = content;
        this.type = type;
        this.createTime = createTime;
        this.deptId = deptId;
        this.companyId = companyId;
    }

    public SocketMessage(Integer id, String launchId, String receiveId, String content, Integer type, Date createTime) {
        this.id = id;
        this.launchId = launchId;
        this.receiveId = receiveId;
        this.content = content;
        this.type = type;
        this.createTime = createTime;
    }

    public SocketMessage() {
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
}
