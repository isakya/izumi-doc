// package com.izumi.wiki.rocketmq;
//
// import com.izumi.wiki.websocket.WebSocketServer;
// import org.apache.rocketmq.common.message.MessageExt;
// import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
// import org.apache.rocketmq.spring.core.RocketMQListener;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.stereotype.Service;
//
// import javax.annotation.Resource;
//
// @Service
// @RocketMQMessageListener(consumerGroup = "default", topic = "VOTE_TOPIC")
// public class VoteTopicConsumer implements RocketMQListener<MessageExt> {
//
//     private static final Logger LOG = LoggerFactory.getLogger(VoteTopicConsumer.class);
//
//     @Resource
//     public WebSocketServer webSocketServer;
//
//     @Override
//     public void onMessage(MessageExt messageExt) {
//         byte[] body = messageExt.getBody();
//         LOG.info("ROCKETMQ收到消息: {}", new String(body));
//         // 调用websocketServer 发送消息
//         webSocketServer.sendInfo(new String(body));
//     }
// }
