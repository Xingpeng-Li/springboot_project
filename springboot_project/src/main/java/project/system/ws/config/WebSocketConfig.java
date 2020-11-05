package com.project.ws.config;


import com.project.mapper.UserMapper;
import com.project.service.SocketMessageService;
import com.project.service.manager.SimpleCoreManager;
import com.project.ws.send.MyWebsocketServer;
import com.project.ws.send.VideoWebsocketServer;
import com.project.ws.utils.SocketMessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/*
@author WL
@CreateDate 2020-7-14
@update 2020-7-15
@description WebWebSocketConfig配置类，主要功能是注入对象ServerEndpointExporter，
                       这个bean会自动注册使用了@ServerEndpoint注解声明的Websocket endpoint
*/
@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Autowired
    public void UserMapper(UserMapper userMapper){
        MyWebsocketServer.userMapper =userMapper;
    }

    @Autowired
    public void socketMessageService(SocketMessageService socketMessageService){
        MyWebsocketServer.socketMessageService = socketMessageService;
    }

    @Autowired
    public void setSocketMessageUtil (SocketMessageUtil socketMessageUtil){
        MyWebsocketServer.socketMessageUtil = socketMessageUtil;
    }
}
