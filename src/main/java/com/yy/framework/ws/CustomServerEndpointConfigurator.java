package com.yy.framework.ws;

import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;

/**
 * @Author linqi
 * @Description
 * @Date 2023-05-18 15:38
 */
public class CustomServerEndpointConfigurator extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
        config.getUserProperties().put("request", request);
        config.getUserProperties().put("response", response);
    }
}
