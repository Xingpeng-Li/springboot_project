package project.system.websocket;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author ASUS
 * @Date 2020/12/17
 * @Description TODO
 **/
@ServerEndpoint("/ws/{userId}")
@Component
public class WebSocketServer {
    private static ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();

    //与某个客户端的连接会话
    private Session session;

    private String userId = "";

    //用户连接
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        if(webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
        }
        webSocketMap.put(userId, this);
        System.out.println(userId+"已连接");
    }

    //用户退出
    @OnClose
    public void onClose() {
        if(webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
        }
        System.out.println(userId+"退出");
    }

    //收到用户消息（不会用到）
//    @OnMessage
//    public void onMessage(String message, Session session) {
//        System.out.println("收到用户消息："+userId+", 报文："+message);
//        if(message != null && !message.equals("")) {
//            try {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("content", message);
//                jsonObject.put("fromUserId", this.userId);
//                String toUserId = jsonObject.getString("toUserId");
//                if(toUserId != null && !toUserId.equals("") && webSocketMap.containsKey(toUserId)) {
//                    webSocketMap.get(toUserId).sendMessage(JSONObject.toJSONString(jsonObject));
//                }
//                sendAll(JSONObject.toJSONString(jsonObject));
//            }
//            catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("用户错误："+this.userId);
        error.printStackTrace();
    }

    public void sendMessage(String userId, String message) throws IOException {
        WebSocketServer server = webSocketMap.get(userId);
        if(server == null) {
            //不在线
            return;
        }
        Session session = server.session;
        session.getAsyncRemote().sendText(message);
    }

    public void sendAll(String message) throws IOException {
        for(WebSocketServer server : webSocketMap.values()) {
            server.session.getAsyncRemote().sendText(message);
        }
    }
}
