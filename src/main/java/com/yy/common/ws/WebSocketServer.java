package com.yy.common.ws;

import com.yy.common.enums.CommonConstants;
import com.yy.common.enums.Response;
import com.yy.common.jwt.Jwt;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.JSONUtils;
import com.yy.common.util.JwtUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.ws.CustomServerEndpointConfigurator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import jakarta.websocket.*;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
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

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) throws IOException {
        final String methodName = "onOpen";
        LOGGER.enter(methodName, "尝试连接websocket");

        HandshakeRequest request = (HandshakeRequest) config.getUserProperties().get("request");

        String token = request.getHeaders().get(CommonConstants.CONTEXT_TOKEN).get(0);

        if (StringUtils.isBlank(token)) {
            LOGGER.warn("token不存在,鉴权失败!");
            String responseJson = JSONUtils.NON_NULL
                    .toJSONString(Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0001).toResult());
            session.getBasicRemote().sendText(responseJson);
            session.close();
            LOGGER.exit(methodName, StringUtils.EMPTY);
            return;
        }

        Jwt.JwtBean bean;
        try {
            // 本地鉴权
            bean = JwtUtils.parseToken(token);

            // 系统日期与令牌时间戳比较
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

        // 心跳机制
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        final ScheduledFuture<?>[] heartbeatFutureRef = new ScheduledFuture[1];
        ScheduledFuture<?> heartbeatFuture = scheduler.scheduleAtFixedRate(() -> {
            try {
                if (session.isOpen()) {
                    // 获取基本远程端点并发送Ping
                    RemoteEndpoint.Basic basicRemote = session.getBasicRemote();
                    basicRemote.sendPing(ByteBuffer.wrap("HEARTBEAT".getBytes()));
                    LOGGER.enter("向账号[" + bean.getAccount() + "]发送Ping帧");
                }
            } catch (IOException | IllegalArgumentException e) {
                // IOException: 连接已失效
                // IllegalArgumentException: session已关闭
                LOGGER.error("发送Ping帧失败，准备关闭连接：" + e.getMessage());
                cancelHeartbeatFuture(heartbeatFutureRef[0], scheduler); // 使用数组引用
                closeSessionSilently(session);
            }
        }, 30, 30, TimeUnit.SECONDS); // 初始延迟30秒，之后每30秒执行一次

        heartbeatFutureRef[0] = heartbeatFuture;
        // 将定时任务Future与Session关联，以便在@OnClose时取消它
        session.getUserProperties().put("HEARTBEAT_FUTURE", heartbeatFuture);
        session.getUserProperties().put("HEARTBEAT_SCHEDULER", scheduler);

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

        String accNo = WebSocketSessionContext.getAccNo(session);
        LOGGER.info("账号：" + accNo + "，接收文本消息：" + message);

        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    @OnMessage
    public void onMessage(Session session, PongMessage message) {
        final String methodName = "onMessage";
        LOGGER.enter(methodName, "接收Pong消息");

        String accNo = WebSocketSessionContext.getAccNo(session);
        LOGGER.info("账号：" + accNo + "，接收Pong消息：" + message);

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

        cancelHeartbeatTask(session); // 新增：取消心跳任务

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
}
