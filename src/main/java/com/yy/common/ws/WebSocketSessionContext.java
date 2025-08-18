package com.yy.common.ws;

import jakarta.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * @Author linqi
 * @Description
 * @Date 2023-05-18 15:56
 */
public class WebSocketSessionContext {

    private static final ConcurrentMap<String, Session> SESSION_MAP = new ConcurrentHashMap<>();

    public static void put(String accNo, Session session) {
        SESSION_MAP.put(accNo, session);
    }

    public static Session getSession(String accNo) {
        return SESSION_MAP.entrySet().stream().filter(v1 -> v1.getKey().equals(accNo)).map(Map.Entry::getValue).findFirst().orElse(null);
    }

    public static String getAccNo(Session session) {
        return SESSION_MAP.entrySet().stream().filter(v1 -> v1.getValue().equals(session)).map(Map.Entry::getKey).findFirst().orElse(EMPTY);
    }

    public static Session remove(String accNo) {
        return SESSION_MAP.remove(accNo);
    }

    public static void remove(Session session) {
        SESSION_MAP.entrySet().removeIf(v1 -> v1.getValue().equals(session));
    }
}
