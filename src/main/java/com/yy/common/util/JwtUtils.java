package com.yy.common.util;

import cn.hutool.core.text.CharSequenceUtil;
import com.yy.common.jwt.Jwt;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

/**
 * Jwt工具类
 *
 * @author
 **/
public final class JwtUtils {

    /**
     * 解析token
     *
     * @param token
     * @return jwtBean
     * @author
     **/
    public static Jwt.JwtBean parseToken(String token) {
        String strJsonToken = JasyptUtils.decrypt(token);
        return JSONUtils.NON_NULL.toJavaObject(strJsonToken, Jwt.JwtBean.class);
    }

    /**
     * 本地token鉴权
     *
     * @param token
     * @return true 验证通过, false 验证不通过,token已过期
     * @author
     **/
    public static boolean verifyToken(String token) {
        Jwt.JwtBean bean = parseToken(token);
        return bean.getExpiresDate() > System.currentTimeMillis();
    }

    /**
     * 本地token鉴权
     *
     * @param bean token对象
     * @return true 验证通过, false 验证不通过,token已过期
     * @author
     **/
    public static boolean verifyToken(Jwt.JwtBean bean) {
        return bean.getExpiresDate() > System.currentTimeMillis();
    }

    /**
     * 远程token鉴权
     *
     * @param token
     * @return true 验证通过, false 验证不通过
     * @author
     **/
    public static boolean remoteVerifyToken(String token) {
        if (CharSequenceUtil.isBlank(token)) {
            return false;
        }
        Jwt.JwtBean bean = parseToken(token);
        if (bean == null || bean.getExpiresDate() <= System.currentTimeMillis()) {
            return false;
        }
        RedisTemplate<String, String> redisTemplate = SpringUtils.getBean("redisTemplate");

        return redisTemplate.opsForValue().setIfAbsent(token, "true",
                (bean.getExpiresDate() - System.currentTimeMillis()) / 1000 + 60, TimeUnit.SECONDS);
    }
}
