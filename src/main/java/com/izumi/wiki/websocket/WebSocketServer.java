package com.izumi.wiki.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;

// 开放websocket连接
@Component
@ServerEndpoint("/ws/{token}")
public class WebSocketServer {

    private static final Logger LOG = LoggerFactory.getLogger(WebSocketServer.class);

    /**
     * 每个客户端一个token
     */
    private String token = "";

    private static HashMap<String, Session> map = new HashMap<>();

    /**
     * 连接成功
     *
     * @param session
     * @param token
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        map.put(token, session);
        this.token = token;
        LOG.info("有新连接: token: {}, session id : {}, 当前连接数: {}", token, session.getId(), map.size());
    }

    /**
     * 连接关闭
     *
     * @param session
     */
    @OnClose
    public void onClose(Session session) {
        map.remove(this.token);
        LOG.info("连接关闭， token: {}, session id: {}, 当前连接数: {}", this.token, session.getId(), map.size());
    }

    /**
     * 收到消息
     *
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        LOG.info("收到消息: {}, 内容: {}", token, message);
    }

    /**
     * 发生错误
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        LOG.error("发生错误", error);
    }

    /**
     * 群发消息
     *
     * @param message
     */
    public void sendInfo(String message) {
        for (String token : map.keySet()) {
            Session session = map.get(token);
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                LOG.error("推送消息失败: {}, 内容: {}", token, message);
            }
            LOG.info("推送消息: {}, 内容: {}", token, message);
        }
    }
}
