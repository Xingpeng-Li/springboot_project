package com.project.service;

import com.project.domain.SocketMessage;

import java.util.Date;
import java.util.List;

/*
@author WL
@CreateDate 2020-7-15
@update 2020-7-16
@description 查找私聊和群聊聊天记录
*/
public interface SocketMessageService {
    void save(SocketMessage socketMessage);

    List<SocketMessage> getSocketMessageByGroup(Date date, Integer deptId, Integer companyId);

    List<SocketMessage> getSocketMessageByUser(String launchId, String receiveId, Date date);

}
