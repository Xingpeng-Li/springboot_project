package project.system.ws.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import project.system.mapper.UserMapper;
import project.system.service.SocketMessageService;
import project.system.ws.send.MyWebsocketServer;
import project.system.ws.utils.SocketMessageUtil;

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
