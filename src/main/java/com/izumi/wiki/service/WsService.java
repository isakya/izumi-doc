package com.izumi.wiki.service;

import com.izumi.wiki.websocket.WebSocketServer;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class WsService {

    @Resource
    private WebSocketServer webSocketServer;

    // 异步执行
    @Async
    public void sendInfo(String message, String logId) {
        MDC.put("LOG_ID", logId);
        // 推送消息
        webSocketServer.sendInfo(message);
    }
}
