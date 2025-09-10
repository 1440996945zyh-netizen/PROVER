package com.yy.common.ws;

import com.yy.common.enums.CommonEnum;
import com.yy.common.enums.Response;
import com.yy.common.enums.WebsocketEnum;
import com.yy.common.jwt.Jwt;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.JSONUtils;
import com.yy.common.util.JwtUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.ws.CustomServerEndpointConfigurator;
import com.yy.ppm.midCore.bean.po.WsOfflineMessagePO;
import com.yy.ppm.midCore.service.WsMessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @Author hukang
 * @Description
 * @Date 2025-09-04 15:46
 */
@Component
@ServerEndpoint(value = "/api/websocket", configurator = CustomServerEndpointConfigurator.class)
public class WebSocketServer {

    private static final MicroLogger LOGGER = new MicroLogger(WebSocketServer.class);

    private static WsMessageService offlineMessageService;

    @Autowired
    public void setOfflineMessageService(WsMessageService offlineMessageService) {
        WebSocketServer.offlineMessageService = offlineMessageService;
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) throws IOException {
        final String methodName = "onOpen";
        LOGGER.enter(methodName, "尝试连接websocket");

        // token校验
        Map<String, List<String>> parameterMap = session.getRequestParameterMap();
        List<String> tokenList = parameterMap.get("token");
        String token = null;

        if (tokenList != null && !tokenList.isEmpty()) {
            token = tokenList.get(0);
        }

        if (StringUtils.isBlank(token)) {
            LOGGER.warn("token不存在,鉴权失败!");
            String responseJson = JSONUtils.NON_NULL
                    .toJSONString(Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0001).toResult());
            session.getBasicRemote().sendText(responseJson);
            session.close(); // 主动关闭，状态码1000
            LOGGER.exit(methodName, StringUtils.EMPTY);
            return;
        }

        Jwt.JwtBean bean;
        try {
            bean = JwtUtils.parseToken(token);
            boolean bool = JwtUtils.verifyToken(bean);
            if (!bool) {
                LOGGER.warn("token已过期,鉴权失败!");
                String responseJson = JSONUtils.NON_NULL
                        .toJSONString(Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0002).toResult());
                session.getBasicRemote().sendText(responseJson);
                session.close();
                LOGGER.exit(methodName, StringUtils.EMPTY);
                return;
            }
        } catch (Exception ex) {
            // 原异常处理逻辑（不变）
            String msg = StringUtil.getErrorText(ex);
            String responseJson = JSONUtils.NON_NULL
                    .toJSONString(Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0100).toResult());
            LOGGER.error("token鉴权异常, ex: " + msg);
            session.getBasicRemote().sendText(responseJson);
            session.close();
            LOGGER.exit(methodName, StringUtils.EMPTY);
            return;
        }

        Session oldSession = WebSocketSessionContext.getSession(bean.getAccount());
        if (oldSession != null) {
            String responseJson = JSONUtils.NON_NULL
                    .toJSONString(Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0004).toResult());
            oldSession.getBasicRemote().sendText(responseJson);
            oldSession.close();
        }

        String responseJson = JSONUtils.NON_NULL
                .toJSONString(Response.SUCCESS.newBuilder().addGateWayCode(Response.GateWayCode.S0000).out("连接成功").toResult());
        session.getBasicRemote().sendText(responseJson);

        WebSocketSessionContext.put(bean.getAccount(), session);

        // 检测心跳机制
        ScheduledExecutorService heartbeatCheckScheduler = Executors.newSingleThreadScheduledExecutor();
        final ScheduledFuture<?>[] heartbeatCheckFutureRef = new ScheduledFuture[1];

        // 初始化最后心跳时间
        session.getUserProperties().put("LAST_CLIENT_HEARTBEAT", System.currentTimeMillis());
        ScheduledFuture<?> heartbeatCheckFuture = heartbeatCheckScheduler.scheduleAtFixedRate(() -> {
            try {
                if (session.isOpen()) {
                    // 检查上次收到客户端心跳的时间
                    Long lastClientHeartbeat = (Long) session.getUserProperties().get("LAST_CLIENT_HEARTBEAT");
                    long currentTime = System.currentTimeMillis();

                    // 如果超过100秒没收到客户端心跳，认为连接已死亡
                    if (lastClientHeartbeat != null && (currentTime - lastClientHeartbeat) > 100000) {
                        LOGGER.warn("账号[" + bean.getAccount() + "]客户端心跳超时，强制关闭连接");
                        cancelHeartbeatFuture(heartbeatCheckFutureRef[0], heartbeatCheckScheduler);
                        closeSessionSilently(session);
                        return;
                    }

                    LOGGER.enter("账号[" + bean.getAccount() + "]心跳检测正常");
                }
            } catch (Exception e) {
                LOGGER.error("心跳检测异常，准备关闭连接：" + e.getMessage());
                cancelHeartbeatFuture(heartbeatCheckFutureRef[0], heartbeatCheckScheduler);
                closeSessionSilently(session);
            }
        }, 45, 45, TimeUnit.SECONDS); // 每45秒检查一次
        heartbeatCheckFutureRef[0] = heartbeatCheckFuture;
        // 将定时任务Future与Session关联
        session.getUserProperties().put("HEARTBEAT_CHECK_FUTURE", heartbeatCheckFuture);
        session.getUserProperties().put("HEARTBEAT_CHECK_SCHEDULER", heartbeatCheckScheduler);

        // 查询是否有离线消息，有的话则推送-
        sendOfflineMessages(bean.getAccount(), session);
        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        final String methodName = "onClose";
        LOGGER.enter(methodName, "客户端连接断开回调");

        LOGGER.warn("客户端连接断开：" + reason.getReasonPhrase());

        WebSocketSessionContext.remove(session);
        // 取消心跳任务
        cancelHeartbeatTask(session);

        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        final String methodName = "onMessage";
        LOGGER.enter(methodName, "接收文本消息");

        String senderAccNo = WebSocketSessionContext.getAccNo(session);
        LOGGER.info("账号：" + senderAccNo + "，接收文本消息：" + message);

        try {
            // 解析消息
            Map<String, Object> messageMap = JSONUtils.NON_NULL.parseObject(message, Map.class);
            String receiverAccount = (String) messageMap.get("receiverAccount");
            String content = (String) messageMap.get("content");

            // 检查是否是心跳消息
            if (WebsocketEnum.HEART_MSG.getCode().equals((String) messageMap.get("mesType"))) {
                // 更新最后收到客户端心跳的时间
                session.getUserProperties().put("LAST_CLIENT_HEARTBEAT", System.currentTimeMillis());
                // 可以选择发送一个响应，但不是必须的
                Map<String, Object> result = new HashMap<>();
                result.put("mesType", WebsocketEnum.HEART_MSG.getCode());
                result.put("content", "Pong");
                result.put("timestamp", System.currentTimeMillis());
                String text = JSONUtils.NON_NULL.toJSONString(Response.SUCCESS.newBuilder().toResult(result));
                session.getBasicRemote().sendText(text);
                return;
            }
            // 其他消息类型
            if ((StringUtils.isNotBlank(receiverAccount)||StringUtils.isNotBlank((String) messageMap.get("deptId"))) && StringUtils.isNotBlank(content)) {
                // 使用工具类发送消息（会自动处理在线/离线状态）
                WebSocketUtils.sendMessage(messageMap);
            } else {
                LOGGER.warn("消息格式错误，缺少接收者或内容");
            }
        } catch (Exception e) {
            LOGGER.error("处理消息时发生异常");
        }

        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    @OnMessage
    public void onMessage(Session session, PongMessage message) {
        final String methodName = "onMessage";
        LOGGER.enter(methodName, "接收Pong消息");
        String accNo = WebSocketSessionContext.getAccNo(session);
        LOGGER.info("账号：" + accNo + "，接收Pong消息：" + message);
        session.getUserProperties().put("LAST_PONG_TIME", System.currentTimeMillis());
        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    @OnMessage
    public void onMessage(Session session, byte[] bytes) {
        final String methodName = "onMessage";
        LOGGER.enter(methodName, "接收byteArray");

        String accNo = WebSocketSessionContext.getAccNo(session);
        LOGGER.info("账号：" + accNo + "，接收byteArray");

        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    @OnError
    public void onError(Session session, Throwable e) {
        final String methodName = "onError";

        String accNo = WebSocketSessionContext.getAccNo(session);

        cancelHeartbeatTask(session);

        LOGGER.error("账号：" + accNo + "，websocket连接错误");
    }

    // 新增一个方法来取消心跳任务
    private void cancelHeartbeatTask(Session session) {
        ScheduledFuture<?> future = (ScheduledFuture<?>) session.getUserProperties().get("HEARTBEAT_FUTURE");
        ScheduledExecutorService scheduler = (ScheduledExecutorService) session.getUserProperties().get("HEARTBEAT_SCHEDULER");
        cancelHeartbeatFuture(future, scheduler);
    }

    private void cancelHeartbeatFuture(ScheduledFuture<?> future, ScheduledExecutorService scheduler) {
        if (future != null) {
            future.cancel(true);
        }
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }
    // 工具方法：安静地关闭Session
    private void closeSessionSilently(Session session) {
        try {
            session.close();
        } catch (IOException e) {
            // 忽略关闭时的异常
        }
    }

    // 发送用户的离线消息
    private void sendOfflineMessages(String account, Session session) {
        try {
            List<WsOfflineMessagePO> offlineMessages =
                    offlineMessageService.getMessageByReceiver(account);
            for (WsOfflineMessagePO offlineMessage : offlineMessages) {
                try {
                    Map<String, Object> result = new HashMap<>();
                    result.put("id", String.valueOf(offlineMessage.getId()));
                    result.put("mesType", WebsocketEnum.SERVER_MSG.getCode());
                    result.put("contentType",offlineMessage.getContentType());
                    result.put("content", offlineMessage.getContent());
                    result.put("mesShowType", offlineMessage.getMesShowType());
                    result.put("isOffline", CommonEnum.YesNoMode.YES.getCode());
                    result.put("timestamp", System.currentTimeMillis());
                    String text = JSONUtils.NON_NULL.toJSONString(Response.SUCCESS.newBuilder().toResult(result));
                    // 直接发送原始消息内容（已经是JSON格式）
                    session.getBasicRemote().sendText(text);
                    // 通知类型直接标记为已发送
                    if (WebsocketEnum.NOTICE_TYPE.getCode().equals(offlineMessage.getMesShowType())) {
                        offlineMessageService.updateIsSent(offlineMessage);
                    }
                } catch (IOException e) {
                    LOGGER.error("发送离线消息失败，消息ID: " + offlineMessage.getId());
                    // 发送失败，保持未发送状态，下次重试
                }
            }

        } catch (Exception e) {
            LOGGER.error("获取或发送离线消息时发生异常");
        }
    }
}
