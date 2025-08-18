package com.yy.common.util;

import org.springframework.validation.FieldError;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 前置校验工具类
 *
 * @author gewx
 **/
public final class FieldErrorUtils {

    /**
     * 注解校验工具方法
     *
     * @param list 字段注解校验异常集合
     * @param bool 过滤条件反转:true|过滤传递字段, false|过滤未传递字段
     * @param args 过滤字段
     * @return 输出异常消息
     * @author gewx
     **/
    public static FieldBean getDefaultMessage(List<FieldError> list, boolean bool, String... args) {
        List<FieldError> returnList = list.stream().filter(val -> bool ? !Arrays.asList(args).contains(val.getField())
                : Arrays.asList(args).contains(val.getField())).collect(Collectors.toList());
        if (!returnList.isEmpty()) {
            String msg = returnList.get(0).getDefaultMessage();
            FieldBean bean = new FieldBean(msg, Boolean.TRUE);
            return bean;
        } else {
            FieldBean bean = new FieldBean(null, Boolean.FALSE);
            return bean;
        }
    }

    /**
     * 注解校验工具方法
     *
     * @param list 字段注解校验异常集合
     * @param args 过滤字段
     * @return 输出异常消息
     * @author gewx
     **/
    public static FieldBean getDefaultMessage(List<FieldError> list, String... args) {
        return getDefaultMessage(list, true, args);
    }

    /**
     * 数据验证结果载体类
     **/
    public static class FieldBean {

        private final String msg;

        private final Boolean success;

        public FieldBean(String msg, Boolean success) {
            this.msg = msg;
            this.success = success;
        }

        public Boolean isSuccess() {
            return this.success;
        }

        public String getMsg() {
            return this.msg;
        }
    }
}
