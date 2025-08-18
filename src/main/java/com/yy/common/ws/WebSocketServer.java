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

/**
 * @Author linqi
 * @Description
 * @Date 2023-05-18 15:46
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
        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        final String methodName = "onClose";
        LOGGER.enter(methodName, "客户端连接断开回调");

        LOGGER.warn("客户端连接断开：" + reason.getReasonPhrase());

        WebSocketSessionContext.remove(session);

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

        LOGGER.error("账号：" + accNo + "，websocket连接错误");
    }
}
