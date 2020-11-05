package project.system.ws.send;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.springframework.stereotype.Component;
import project.system.domain.SocketMessage;
import project.system.domain.User;
import project.system.mapper.UserMapper;
import project.system.service.SocketMessageService;
import project.system.ws.model.SocketMessageModel;
import project.system.ws.utils.SocketMessageUtil;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;


/*
@author WL
@CreateDate 2020-7-15
@update 2020-7-16 2020-7-17 2020-7-18
@description 与前端websocket交互
*/
@ServerEndpoint("/chat/room/group/{param}")
@Component
public class MyWebsocketServer {

    public static SocketMessageUtil socketMessageUtil;

    public static SocketMessageService socketMessageService;

    public static UserMapper userMapper;

    // 记录空闲Session集合
    private static CopyOnWriteArraySet<Session> idle = new CopyOnWriteArraySet<Session>();
    // 记录正在使用中Session集合，value为Future，表示使用情况
    private static final ConcurrentHashMap<Session, Future<Void>> busy = new ConcurrentHashMap<Session, Future<Void>>();

    //存放所有在线的客户端以及客户端信息
    private static Map<String, Session> clientSession = new ConcurrentHashMap<>();
    private static Map<String, User> clientUser = new ConcurrentHashMap<>();

    //与客户端建立连接
    @OnOpen
    public void onOpen(Session session, @PathParam("param") String socketUserId) {
        System.out.println(socketUserId);
        System.out.println("有新的客户端连接了: " + session.getId());

        System.out.println(Integer.parseInt(socketUserId));
        User user = userMapper.selectByPrimaryKey(Integer.parseInt(socketUserId));
        user.setSocketId(session.getId());
        userMapper.updateByPrimaryKeySelective(user);

        //将新用户存入在线的组
        clientUser.put(session.getId(), user);
        //将客户端存入
        clientSession.put(session.getId(), session);

        //判断是否在线
        for (Map.Entry<String, User> user1 : clientUser.entrySet()) {
            User socketUser = user1.getValue();
            System.out.println("在线--" + socketUser);
            if (Objects.equals(socketUser.getUserId(), user.getUserId())) {
                SocketMessage socketMessage = new SocketMessage(null, "0", "0", "已在其他设备登陆，强制下线", 5, new Date());
                SocketMessageModel socketMessageModel = socketMessageUtil.convertFromSocketMessage(socketMessage);
                Session session1 = clientSession.get(socketUser.getSocketId());
                sendTo(socketMessageModel, session1);
                break;
            }
        }
        open(session);
        SocketMessage socketMessage = new SocketMessage(null, String.valueOf(user.getUserId()), "0", user.getUserName() + "进入部门群聊-" + session.getId(), 0, new Date(), user.getDeptId(), user.getCompanyId());
        SocketMessageModel socketMessageModel = socketMessageUtil.convertFromSocketMessage(socketMessage);
        System.out.println("发送登陆消息");
        this.sendAll(socketMessageModel, user, true);
        this.userAll(user);
        this.inLine(user);
    }

    //客户端关闭
    @OnClose
    public void onClose(Session session) {
        System.out.println("用户" + session.getId() + "离线");
        User socketUser = clientUser.get(session.getId());

        //将掉线的用户移除在线的组里
        clientSession.remove(session.getId());
        clientUser.remove(session.getId());
        close(session);
        SocketMessage socketMessage = new SocketMessage(null, "0", "0", socketUser.getUserName() + "离开部门群聊", 0, new Date(), socketUser.getDeptId(), socketUser.getCompanyId());
        SocketMessageModel socketMessageModel = socketMessageUtil.convertFromSocketMessage(socketMessage);
        this.sendAll(socketMessageModel, socketUser, true);
        this.inLine(socketUser);
    }

    //发生错误
    @OnError
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    //收到客户端发来消息
    @OnMessage
    public void onMessage(String message) {
        System.out.println("收到消息：" + message);
        JSONObject jsStr = JSONObject.parseObject(message);
        SocketMessage socketMessage = JSONObject.toJavaObject(jsStr, SocketMessage.class);
        socketMessage.setCreateTime(new Date());

        User socketUser = clientUser.get(socketMessage.getLaunchId());
        socketMessage.setLaunchId(String.valueOf(socketUser.getUserId()));

        if (!"-1".equals(socketMessage.getReceiveId())) {
            //离线消息
            if (socketMessage.getType() == 30 || socketMessage.getType() == 40) {
                //将客户端提交的type设置为服务端的type
                if (socketMessage.getType() == 40)
                    socketMessage.setType(4);
                if (socketMessage.getType() == 30)
                    socketMessage.setType(3);
                //保存到数据库
                saveMessage(socketMessage);
            } else {
                User receiveUser = clientUser.get(socketMessage.getReceiveId());
                Session receiveSession = clientSession.get(socketMessage.getReceiveId());//接收人
                socketMessage.setReceiveId(String.valueOf(receiveUser.getUserId()));
                SocketMessageModel socketMessageModel = socketMessageUtil.convertFromSocketMessage(socketMessage);
                this.sendTo(socketMessageModel, receiveSession);
            }
        } else {
            socketMessage.setReceiveId("0");
            SocketMessageModel socketMessageModel = socketMessageUtil.convertFromSocketMessage(socketMessage);
            this.sendAll(socketMessageModel, socketUser, true);
        }
    }

    /*
     * 群发消息,将消息发送给与user所在同一个部门的用户
     * @param socketMessageModel 消息内容
     * @param 当前指定用户
     * @param flag 是否需要判断部门
     * */
    private void sendAll(SocketMessageModel socketMessageModel, User user, boolean flag) {
        if (!flag) {//不需要判断部门是否相同，只需要判断公司是否相同
            for (Map.Entry<String, Session> sessionEntry : clientSession.entrySet()) {
                String sessionId = sessionEntry.getKey();
                User inlineUser = clientUser.get(sessionId);
                if (inlineUser.getCompanyId().equals(user.getCompanyId())) {
                    try {
                        send(sessionEntry.getValue(), socketMessageModel, 3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            for (Map.Entry<String, Session> sessionEntry : clientSession.entrySet()) {
                String sessionId = sessionEntry.getKey();
                User inlineUser = clientUser.get(sessionId);
                if (inlineUser.getCompanyId().equals(user.getCompanyId()) && inlineUser.getDeptId().equals(user.getDeptId())) {
                    try {
                        send(sessionEntry.getValue(), socketMessageModel, 3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        saveMessage(socketMessageUtil.convertFromSocketMessageModel(socketMessageModel));
    }

    //发送消息给指定用户
    private void sendTo(SocketMessageModel socketMessageModel, Session receiveSession) {
        saveMessage(socketMessageUtil.convertFromSocketMessageModel(socketMessageModel));
        if (receiveSession != null) {
            try {
                send(receiveSession, socketMessageModel, 3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    //将消息保存在数据库
    private void saveMessage(SocketMessage socketMessage) {
        if (socketMessage.getType() != 0 && socketMessage.getType() != 1 && socketMessage.getType() != 2 && socketMessage.getType() != 5) {
            SocketMessage socketMessage1 = new SocketMessage(null, socketMessage.getLaunchId(), socketMessage.getReceiveId(), socketMessage.getContent(), socketMessage.getType(), socketMessage.getCreateTime(), socketMessage.getDeptId(), socketMessage.getCompanyId());
            String content = socketMessage1.getContent();
            int index = content.indexOf("进入部门群聊-");
            if (socketMessage1.getType().equals(0) && index != -1) {
                socketMessage1.setContent(content.substring(0, index + 6));
            }
            socketMessageService.save(socketMessage1);
        }
    }

    // 新增Session
    public static void open(Session session) {
        idle.add(session);
    }

    // 关闭Session
    public static void close(Session session) {
        idle.remove(session);
        busy.remove(session);
    }

    // 使用session发送消息
    public static void send(Session session, SocketMessageModel message, Integer timeout) throws InterruptedException {
        if (timeout < 0) { // timeout后放弃本次发送
            return;
        }
        if (idle.remove(session)) { // 判断session是否空闲，抢占式
            busy.put(session, session.getAsyncRemote().sendText(JSON.toJSONString(message)));
        } else {
            // 若session当前不在idle集合，则去busy集合中查看session上次是否已经发送完毕，即采用惰性判断
            synchronized (busy) {
                if (busy.containsKey(session) && busy.get(session).isDone()) {
                    busy.remove(session);
                    idle.add(session);
                }
            }
            // 重试
            Thread.sleep(100);
            send(session, message, timeout - 100);
        }
    }

    //获取用户公司当前的在线人数
    private void inLine(User user) {
        SocketMessage socketMessage = new SocketMessage(null, "0", "0", JSON.toJSONString(clientUser), 1, new Date());
        this.sendAll(socketMessageUtil.convertFromSocketMessage(socketMessage), user, false);
    }

    //获取用户公司的所有员工
    private void userAll(User user) {
        List<User> users = userMapper.selectByCompanyId(user.getCompanyId());
        SocketMessage socketMessage = new SocketMessage(null, "0", "0", JSON.toJSONString(users), 2, new Date());
        this.sendAll(socketMessageUtil.convertFromSocketMessage(socketMessage), user, false);
    }
}
