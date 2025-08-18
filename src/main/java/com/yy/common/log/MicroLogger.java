package com.yy.common.log;

import com.yy.framework.annotation.ThreadSafe;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * 分布式日志框架
 *
 * @author
 **/
@ThreadSafe
public final class MicroLogger {

    enum LOGGER {
        // 开始
        enter,

        // 退出
        exit,

        // 普通
        info,

        // 警告
        warn,

        // 异常
        error
    }

    private static final String MDC_STATE = "state";

    private final Logger logger;

    @SuppressWarnings("rawtypes")
    public MicroLogger(Class c) {
        this.logger = LoggerFactory.getLogger(c);
    }

    /**
     * 日志打印
     *
     * @param methodName 方法名
     * @param msg        打印消息body
     * @return void
     * @author
     **/
    public void enter(String methodName, String msg) {
        logger.info("{} ==> {} | {}", methodName, msg, LOGGER.enter);
    }

    /**
     * 日志打印
     *
     * @param
     * @return void
     * @author
     **/
    public void enter(String msg) {
        enter(null, msg);
    }

    /**
     * 日志打印
     *
     * @param methodName 方法名
     * @param msg        打印消息body
     * @return void
     * @author
     **/
    public void exit(String methodName, String msg) {
        logger.info("{} ==> {} | {}", methodName, msg, LOGGER.exit);
    }

    /**
     * 日志打印
     *
     * @param msg 打印消息body
     * @return void
     * @author
     **/
    public void exit(String msg) {
        exit(null, msg);
    }

    /**
     * 日志打印
     *
     * @param methodName 方法名
     * @param msg        打印消息body
     * @return void
     * @author
     **/
    public void info(String methodName, String msg) {
        logger.info("{} ==> {}", methodName, msg);
    }

    /**
     * 日志打印
     *
     * @param msg 打印消息body
     * @return void
     * @author
     **/
    public void info(String msg) {
        info(null, msg);
    }

    /**
     * 日志打印
     *
     * @param methodName 方法名
     * @param msg        打印消息body
     * @return void
     * @author
     **/
    public void warn(String methodName, String msg) {
        logger.warn("{} ==> {}", methodName, msg);
    }

    /**
     * 日志打印
     *
     * @param msg 打印消息body
     * @return void
     * @author
     **/
    public void warn(String msg) {
        warn(null, msg);
    }

    /**
     * 日志打印
     *
     * @param methodName 方法名
     * @param msg        打印消息body
     * @return void
     * @author
     **/
    public void error(String methodName, String msg) {
        logger.error("{} ==> {}", methodName, msg);
    }

    /**
     * 日志打印
     *
     * @param msg 打印消息body
     * @return void
     * @author
     **/
    public void error(String msg) {
        error(null, msg);
    }
}
