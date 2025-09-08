package com.yy.common.ws;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.enums.Response;
import com.yy.common.enums.WebsocketEnum;
import com.yy.common.util.JSONUtils;
import com.yy.ppm.midCore.bean.po.WsOfflineMessagePO;
import com.yy.ppm.midCore.service.WsMessageService;
import jakarta.websocket.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author linqi
 * @Description
 * @Date 2023-05-18 18:27
 */
@Component
public class WebSocketUtils {
    private static Snowflake snowflake;
    private static WsMessageService offlineMessageService;


    @Autowired
    public void setOfflineMessageService(WsMessageService offlineMessageService) {
        WebSocketUtils.offlineMessageService = offlineMessageService;
    }

    @Autowired
    public void setSnowflake(Snowflake snowflake) {
        WebSocketUtils.snowflake = snowflake;
    }

    public static void sendMessage(String sender, Map<String, Object> messageMap) {
        Session session = WebSocketSessionContext.getSession(messageMap.get("receiverAccount").toString());
        Map<String, Object> result = new HashMap<>();
        result.put("sender", sender);
        result.put("mesType", WebsocketEnum.SERVER_MSG.getCode());
        result.put("contentType", messageMap.get("contentType").toString());
        result.put("content", messageMap.get("content").toString());
        result.put("timestamp", System.currentTimeMillis());
        String text = JSONUtils.NON_NULL.toJSONString(Response.SUCCESS.newBuilder().toResult(result));

        WsOfflineMessagePO wsOfflineMessagePO = new WsOfflineMessagePO();
        wsOfflineMessagePO.setId(snowflake.nextId());
        wsOfflineMessagePO.setContent(messageMap.get("content").toString());
        wsOfflineMessagePO.setIsSent("0");
        wsOfflineMessagePO.setMesType(WebsocketEnum.SERVER_MSG.getCode());
        wsOfflineMessagePO.setContentType(messageMap.get("contentType").toString());
        wsOfflineMessagePO.setReceiverAccount(messageMap.get("receiverAccount").toString());
        wsOfflineMessagePO.setSenderAccount(messageMap.get("senderAccount").toString());
        wsOfflineMessagePO.setCreateTime(new Date());

        try {
            if (session != null && session.isOpen()) {
                // 接收者在线，直接发送
                session.getBasicRemote().sendText(text);
            } else {
                // 接收者不在线，存储为离线消息
                offlineMessageService.add(wsOfflineMessagePO);
            }
        } catch (IOException e) {
            // 发送失败，也存储为离线消息
            offlineMessageService.add(wsOfflineMessagePO);
        }
    }

    /**
     * 发送待办通知（系统消息）
     */
    public static void sendNotification(String sender, String receiver, String notificationContent) {
        Session session = WebSocketSessionContext.getSession(receiver);
        Map<String, Object> result = new HashMap<>();
        result.put("sender", sender);
        result.put("notification", notificationContent);
        result.put("timestamp", System.currentTimeMillis());
        result.put("type", "NOTIFICATION");
        String text = JSONUtils.NON_NULL.toJSONString(Response.SUCCESS.newBuilder().toResult(result));

        WsOfflineMessagePO wsOfflineMessagePO = new WsOfflineMessagePO();
        wsOfflineMessagePO.setId(snowflake.nextId());
        try {
            if (session != null && session.isOpen()) {
                session.getBasicRemote().sendText(text);
            } else {
                // 接收者不在线，存储为待办通知
                offlineMessageService.add(wsOfflineMessagePO);
            }
        } catch (IOException e) {
            offlineMessageService.add(wsOfflineMessagePO);
        }
    }
}
