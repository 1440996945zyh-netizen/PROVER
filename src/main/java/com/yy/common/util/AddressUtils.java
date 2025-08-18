package com.yy.common.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 获取地址类
 *
 * @author hukang
 */
public class AddressUtils {
    private static final Logger log = LoggerFactory.getLogger(AddressUtils.class);

    // IP地址查询
    public static final String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp";

    /**
     * 检查是否为内部IP地址
     *
     * @param ip 字符串地址
     * @return 结果
     */
    public static boolean internalIp(String ip) {
        try {
            byte[] addr = InetAddress.getByName(ip).getAddress();
            return internalIp(addr) || "127.0.0.1".equals(ip);
        } catch (UnknownHostException e) {
            log.error("解析 IP 地址失败: {}", ip, e);
            return true; // 默认视为内网 IP 避免异常
        }
    }

    private static boolean internalIp(byte[] addr) {
        if (addr == null || addr.length < 2) {
            return true;
        }
        final byte b0 = addr[0];
        final byte b1 = addr[1];
        // 10.x.x.x/8
        if (b0 == (byte) 0x0A) {
            return true;
        }
        // 172.16.0.0/12
        if (b0 == (byte) 0xAC && (b1 & 0xF0) == 0x10) {
            return true;
        }
        // 192.168.x.x/16
        if (b0 == (byte) 0xC0 && b1 == (byte) 0xA8) {
            return true;
        }
        return false;
    }

    // 未知地址
    public static final String UNKNOWN = "XX XX";

    public static String getRealAddressByIP(String ip) {
        // 内网不查询
        if (internalIp(ip)) {
            return "内网IP";
        }

        try {
            String rspStr = HttpUtils.sendGet(IP_URL, "ip=" + ip + "&json=true", "GBK");
            if ("".equals(rspStr)) {
                log.error("获取地理位置异常 {}", ip);
                return UNKNOWN;
            }
            JSONObject obj = JSON.parseObject(rspStr);
            String region = obj.getString("pro");
            String city = obj.getString("city");
            return String.format("%s %s", region, city);
        } catch (Exception e) {
            log.error("获取地理位置异常 {}", e);
        }
        return UNKNOWN;
    }
}
