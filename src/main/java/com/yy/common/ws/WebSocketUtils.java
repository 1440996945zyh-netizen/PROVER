package com.yy.common.ws;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.enums.CommonEnum;
import com.yy.common.enums.Response;
import com.yy.common.enums.WebsocketEnum;
import com.yy.common.util.JSONUtils;
import com.yy.ppm.midCore.bean.po.WsOfflineMessagePO;
import com.yy.ppm.midCore.service.WsMessageService;
import jakarta.websocket.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

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

    public static void sendMessage(Map<String, Object> messageMap) {
        List<String> accountList = new ArrayList<>();
        String contentType = messageMap.get("contentType").toString();
        if (WebsocketEnum.PERSONAL_TYPE.getCode().equals(contentType)) {
            accountList.add(messageMap.get("receiverAccount").toString());
        }else {
            List<String> userAccounts = offlineMessageService.getUserAccounts(messageMap.get("deptId").toString(), messageMap.get("postCode").toString());
            accountList.addAll(userAccounts);
        }
        for (String account : accountList) {
            Session session = WebSocketSessionContext.getSession(account);
            Map<String, Object> result = new HashMap<>();
            result.put("mesType", WebsocketEnum.SERVER_MSG.getCode());
            result.put("contentType", messageMap.get("contentType").toString());
            result.put("content", messageMap.get("content").toString());
            result.put("mesShowType", messageMap.get("mesShowType").toString());
            result.put("isOffline", CommonEnum.YesNoMode.NO.getCode());
            result.put("timestamp", System.currentTimeMillis());
            String text = JSONUtils.NON_NULL.toJSONString(Response.SUCCESS.newBuilder().toResult(result));

            WsOfflineMessagePO wsOfflineMessagePO = new WsOfflineMessagePO();
            wsOfflineMessagePO.setId(snowflake.nextId());
            wsOfflineMessagePO.setContent(messageMap.get("content").toString());
            wsOfflineMessagePO.setIsSent("0");
            wsOfflineMessagePO.setMesType(WebsocketEnum.SERVER_MSG.getCode());
            wsOfflineMessagePO.setMesShowType(messageMap.get("mesShowType").toString());
            wsOfflineMessagePO.setContentType(messageMap.get("contentType").toString());
            wsOfflineMessagePO.setSenderAccount(messageMap.get("senderAccount").toString());
            wsOfflineMessagePO.setCreateTime(new Date());
            if (WebsocketEnum.PERSONAL_TYPE.getCode().equals(contentType)) {
                wsOfflineMessagePO.setReceiverAccount(account);
                wsOfflineMessagePO.setContentType(WebsocketEnum.PERSONAL_TYPE.getCode());
            }else {
                wsOfflineMessagePO.setReceiverAccount(account);
                wsOfflineMessagePO.setReceiverAccount(messageMap.get("deptId").toString());
                wsOfflineMessagePO.setSenderAccount(messageMap.get("postCode").toString());
                wsOfflineMessagePO.setContentType(WebsocketEnum.GROUP_TYPE.getCode());
            }

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
