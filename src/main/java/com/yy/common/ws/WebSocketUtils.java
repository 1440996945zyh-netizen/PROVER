package com.yy.common.ws;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.StrUtil;
import com.yy.common.enums.CommonEnum;
import com.yy.common.enums.Response;
import com.yy.common.enums.SysParameterEnum;
import com.yy.common.enums.WebsocketEnum;
import com.yy.common.util.JSONUtils;
import com.yy.ppm.flowable.bean.dto.BpmMessageTemplateDTO;
import com.yy.ppm.middleware.bean.po.WsOfflineMessagePO;
import com.yy.ppm.middleware.service.WsMessageService;
import com.yy.ppm.system.bean.dto.SysParameterDTO;
import com.yy.ppm.system.service.SysParameterService;
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
    private static SysParameterService sysParameterService;

    @Autowired
    public void setOfflineMessageService(WsMessageService offlineMessageService) {
        WebSocketUtils.offlineMessageService = offlineMessageService;
    }

    @Autowired
    public void setSnowflake(Snowflake snowflake) {
        WebSocketUtils.snowflake = snowflake;
    }

    @Autowired
    public void setSysParameterService(SysParameterService sysParameterService) {
        WebSocketUtils.sysParameterService = sysParameterService;
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
                wsOfflineMessagePO.setDeptId(Long.valueOf(messageMap.get("deptId").toString()));
                wsOfflineMessagePO.setPostCode(messageMap.get("postCode").toString());
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

    /**
     * 专门针对 BpmMessageTemplateDTO 的推送方法
     *
     * @param receiverAccount 接收人
     * @param template 模板常量
     * @param forcePush 是否强制推送 (如果为true，将无视系统全局开关，强行发送)
     * @param args 替换 {} 的参数列表
     */
    public static void sendTemplateNotification(String receiverAccount, BpmMessageTemplateDTO template, boolean forcePush, Object... args) {

        // 只有在 不是强制推送 且 全局开关关闭 的情况下，才拦截
        if (!forcePush && !isWebSocketPushEnabled()) {
            return; // 开关为 N，且未声明强制发送，中止
        }

        // 格式化标题和内容
        String finalTitle = StrUtil.format(template.getTitle(), args);
        String finalContent = StrUtil.format(template.getContent(), args);

        // 构造messageMap
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("contentType", WebsocketEnum.PERSONAL_TYPE.getCode());
        messageMap.put("receiverAccount", receiverAccount);
        messageMap.put("senderAccount", "SYSTEM");
        messageMap.put("content", finalContent);
        messageMap.put("mesShowType", template.getMesShowType());

        sendMessage(messageMap);
    }

    /**
     * 重载方法
     */
    public static void sendTemplateNotification(String receiverAccount, BpmMessageTemplateDTO template, Object... args) {
        sendTemplateNotification(receiverAccount, template, false, args);
    }

    /**
     * 校验 WebSocket 消息推送总开关
     * @return true: 允许推送, false: 拒绝推送
     */
    private static boolean isWebSocketPushEnabled() {
        try {
            if (sysParameterService != null) {
                SysParameterDTO config = sysParameterService.getConfig(SysParameterEnum.WEBSOCKET_MESSAGE_SWITCH.getCode());

                String paramVal = config != null ? config.getParamVal() : SysParameterEnum.WEBSOCKET_MESSAGE_SWITCH.getDefaultValue();

                // 如果值为 N，则拦截推送
                if ("N".equalsIgnoreCase(paramVal)) {
                    return false;
                }
            }
        } catch (Exception ignored) {}
        return true;
    }
}
