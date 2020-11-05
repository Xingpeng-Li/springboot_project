package project.system.ws.send;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;

@Component
@ServerEndpoint("/video/{groupUniqueId}")
public class VideoWebsocketServer {

    // 记录空闲Session集合
    private static CopyOnWriteArraySet<Session> idle = new CopyOnWriteArraySet<>();
    // 记录正在使用中Session集合，value为Future，表示使用情况
    private static final ConcurrentHashMap<Session, Future<Void>> busy = new ConcurrentHashMap<>();

    //存放会议号与对应的Session集合
    private static Map<String, Set<Session>> clientSession=new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session,@PathParam("groupUniqueId")String groupUniqueId){
        System.out.println("有客户端连接");
        if (!clientSession.containsKey(groupUniqueId)) {
            clientSession.put(groupUniqueId, new HashSet<>());
        }
        clientSession.get(groupUniqueId).add(session);
        open(session);
    }

    @OnMessage
    public void onMessage(Session session, @PathParam("groupUniqueId")String groupUniqueId,String message){
        System.out.println(message);
        Set<Session> sessions = clientSession.get(groupUniqueId);
        sendInfo(session,message,sessions);
    }

    private void sendInfo(Session session,String message,Set<Session> sessions){
        for (Session session1:sessions){
            if(!session1.getId().equals(session.getId()))
            try {
                send(session1,message,3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // 使用session发送消息
    public static void send(Session session, String message, Integer timeout) throws InterruptedException {
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

    @OnClose
    public void onClose(Session session){
        clientSession.remove(session.getId());
        close(session);
    }

    //发生错误
    @OnError
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
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

}
