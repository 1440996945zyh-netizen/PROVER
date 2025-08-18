package com.yy.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

/**
 * 消息取得
 */
@Component
public class MessageUtils {

    @Autowired
    private Environment env;

    /**
     * 根据Key获得资源文件内容
     *
     * @param key
     * @return
     */
    public String getMessage(String key) {
        try {
            return env.getProperty(key);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 根据key获得资源文件内容，可替换参数
     *
     * @param key
     * @param args
     * @return
     */
    public String getMessage(String key, Object[] args) {
        try {
            return MessageFormat.format(env.getProperty(key), args);
        } catch (Exception e) {
            return "";
        }
    }
}
