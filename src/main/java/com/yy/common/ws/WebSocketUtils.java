package com.yy.common.ws;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.enums.Response;
import com.yy.common.util.JSONUtils;
import com.yy.ppm.middleware.bean.po.WsOfflineMessagePO;
import com.yy.ppm.middleware.service.WsOfflineMessageService;
import jakarta.annotation.Resource;
import jakarta.websocket.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
    private static WsOfflineMessageService offlineMessageService;

    // 使用setter注入，避免循环依赖
    @Autowired
    public void setOfflineMessageService(WsOfflineMessageService offlineMessageService) {
        WebSocketUtils.offlineMessageService = offlineMessageService;
    }

    public static void sendMessage(String sender, String receiver, String textMsg) {
        Session session = WebSocketSessionContext.getSession(receiver);
        Map<String, Object> result = new HashMap<>();
        result.put("sender", sender);
        result.put("textMsg", textMsg);
        result.put("timestamp", System.currentTimeMillis());
        String text = JSONUtils.NON_NULL.toJSONString(Response.SUCCESS.newBuilder().toResult(result));

        WsOfflineMessagePO wsOfflineMessagePO = new WsOfflineMessagePO();
        wsOfflineMessagePO.setId(snowflake.nextId());
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
