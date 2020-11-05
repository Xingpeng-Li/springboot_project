package project.system.ws.utils;


import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import project.system.domain.SocketMessage;
import project.system.domain.User;
import project.system.mapper.UserMapper;
import project.system.ws.model.SocketMessageModel;

import javax.annotation.Resource;

/*
@author WL
@CreateDate 2020-7-17
@update
@description 封装了实现SocketMessage与SocketMessageModel互相转换的方法
*/
@Component
public class SocketMessageUtil {

    @Resource
    private UserMapper socketUserMapper;

    public SocketMessageModel convertFromSocketMessage(SocketMessage socketMessage) {
        if (socketMessage == null) {
            return null;
        }
        SocketMessageModel socketMessageModel = new SocketMessageModel();
        BeanUtils.copyProperties(socketMessage, socketMessageModel);
        if (!"0".equals(socketMessage.getLaunchId())) {
            User user = socketUserMapper.selectByPrimaryKey(Integer.parseInt(socketMessage.getLaunchId()));
            socketMessageModel.setLaunchUserName(user.getUserName());
        }
        if (!"0".equals(socketMessage.getReceiveId())) {
            User user = socketUserMapper.selectByPrimaryKey(Integer.parseInt(socketMessage.getReceiveId()));
            socketMessageModel.setReceiveUserName(user.getUserName());
        }
        return socketMessageModel;
    }

    public SocketMessage convertFromSocketMessageModel(SocketMessageModel socketMessageModel) {
        if (socketMessageModel == null) {
            return null;
        }
        SocketMessage socketMessage = new SocketMessage();
        BeanUtils.copyProperties(socketMessageModel, socketMessage);
        return socketMessage;
    }
}
