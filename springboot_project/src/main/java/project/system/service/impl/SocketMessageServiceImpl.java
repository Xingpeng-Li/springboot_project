package com.project.service.impl;

import com.project.domain.SocketMessage;
import com.project.mapper.SocketMessageMapper;
import com.project.service.SocketMessageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/*
@author WL
@CreateDate 2020-7-15
@update 2020-7-16
@description 查找私聊和群聊聊天记录的具体实现
*/
@Service
public class SocketMessageServiceImpl implements SocketMessageService {
    @Resource
    private SocketMessageMapper socketMessageMapper;

    @Override
    public void save(SocketMessage socketMessage) {
        socketMessageMapper.insertSelective(socketMessage);
    }

    @Override
    public List<SocketMessage> getSocketMessageByGroup(Date date,Integer deptId,Integer companyId) {
        return socketMessageMapper.getSocketMessageByGroup(date,deptId,companyId);
    }

    @Override
    public List<SocketMessage> getSocketMessageByUser(String launchId, String receiveId, Date date) {
        return socketMessageMapper.getSocketMessageByUser(launchId,receiveId,date);
    }
}
