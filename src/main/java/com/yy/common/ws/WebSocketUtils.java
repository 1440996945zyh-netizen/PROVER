package com.yy.common.ws;

import com.yy.common.enums.Response;
import com.yy.common.util.JSONUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author linqi
 * @Description
 * @Date 2023-05-18 18:27
 */
public class WebSocketUtils {

    public static void sendMessage(String sender, String receiver, String textMsg) {
        Map<String, Object> result = new HashMap<>();
        result.put("sender", sender);
        result.put("textMsg", textMsg);
        String text = JSONUtils.NON_NULL.toJSONString(Response.SUCCESS.newBuilder().toResult(result));
        try {
            WebSocketSessionContext.getSession(receiver).getBasicRemote().sendText(text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendMessage(String sender, String receiver, byte[] byteArrayMsg) {
        Map<String, Object> result = new HashMap<>();
        result.put("sender", sender);
        result.put("byteArrayMsg", byteArrayMsg);
        String text = JSONUtils.NON_NULL.toJSONString(Response.SUCCESS.newBuilder().toResult(result));
        try {
            WebSocketSessionContext.getSession(receiver).getBasicRemote().sendText(text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
