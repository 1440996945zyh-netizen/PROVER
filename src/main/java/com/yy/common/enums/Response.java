package com.yy.common.enums;

import com.github.pagehelper.Page;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 网关响应设计
 **/
public enum Response {

    // 响应成功
    SUCCESS(true, "SUCCESS"),

    // 响应失败
    FAIL(false, "FAIL");

    /**
     * 网关响应码枚举
     **/
    @Getter
    public enum GateWayCode {

        /**
         * 网关默认响应,成功
         **/
        S0000("0000", "SUCCESS"),

        /**
         * 未登录
         **/
        E0001("0001", "未登录"),

        /**
         * 登录超时
         **/
        E0002("0002", "登录超时"),

        /**
         * 账号不存在
         **/
        E0003("0003", "账号不存在"),

        /**
         * 账号重复登录
         **/
        E0004("0004", "您的账号已在另一台设备登录"),

        /**
         * 鉴权失败
         **/
        E0100("0100", "鉴权失败"),

        /**
         * 权限不足
         **/
        E0101("0101", "权限不足"),

        /**
         * 通用数据校验失败
         **/
        E0200("0200", "通用数据校验失败"),

        /**
         * 缺失必填参数
         **/
        E0201("0201", "缺失必填参数"),

        /**
         * 数值格式不正确
         **/
        E0202("0202", "数值格式不正确"),

        /**
         * 入参枚举不匹配
         **/
        E0205("0205", "入参枚举不匹配"),

        /**
         * SQL异常
         **/
        E0300("0300", "数据操作异常"),

        /**
         * 文件上传失败
         **/
        E0400("0400", "文件上传失败"),

        /**
         * 文件上传容量过大
         **/
        E0401("0401", "文件上传大小超过限制"),

        /**
         * 文件上传格式有误
         **/
        E0402("0402", "不支持的文件上传格式"),

        /**
         * 文件上传格式有误
         **/
        E0403("0403", "文件名过长"),

        /**
         * excel导出失败
         **/
        E0501("0501", "Excel导出失败"),

        /**
         * pdf转换失败
         **/
        E0502("0502", "PDF转换失败"),

        /**
         * 业务异常
         **/
        E9996("9996", "业务异常"),

        /**
         * 运行时异常
         **/
        E9997("9997", "运行时异常"),

        /**
         * 并发异常
         **/
        E9998("9998", "并发异常"),

        /**
         * 系统错误
         **/
        E9999("9999", "系统错误"),

        /**
         * 图形验证码错误
         **/
        E9996001("9996001", "图形验证码错误");

        GateWayCode(String code, String comment) {
            this.code = code;
            this.comment = comment;
        }

        private String code;

        private String comment;
    }

    Response(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    private final boolean success;

    private final String msg;

    public boolean isSuccess() {
        return success;
    }

    public String getMsg() {
        return msg;
    }

    /**
     * 响应消息构建方法
     *
     * @return 响应消息对象
     * @author
     **/
    public ResponseBuild newBuilder() {
        return new ResponseBuild(this);
    }

    /**
     * 内部类,消息构建
     *
     * @author
     **/
    public class ResponseBuild {

        private final boolean success;

        private String code;

        private String msg;

        ResponseBuild(Response response) {
            this.success = response.isSuccess();
            this.msg = response.getMsg();
            this.code = response.isSuccess() ? GateWayCode.S0000.getCode() : GateWayCode.E9999.getCode();
        }

        public ResponseBuild out(String msg) {
            this.msg = msg;
            return this;
        }

        public ResponseBuild addGateWayCode(GateWayCode geteWayCode) {
            this.code = geteWayCode.getCode();
            this.msg = geteWayCode.getComment();
            return this;
        }

        public Map<String, Object> toResult() {
            return buildMap();
        }

        /**
         * 构建消息体
         *
         * @return 消息体
         * @author
         **/
        public <T extends java.io.Serializable> Map<String, Object> toResult(T model) {
            Map<String, Object> map = buildMap();
            map.put("data", model);
            return map;
        }

        /**
         * 构建消息体
         *
         * @return 消息体
         * @author
         **/
        @SuppressWarnings("rawtypes")
        public <T extends java.util.Collection> Map<String, Object> toResult(T model) {
            Map<String, Object> map = buildMap();
            map.put("data", model);
            return map;
        }

        /**
         * 构建消息体
         *
         * @return 消息体
         * @author
         **/
        public <T extends java.io.Serializable> Map<String, Object> toResult(Page<T> model) {
            Map<String, Object> map = buildMap();
            map.put("data", model);
            return map;
        }

        /**
         * 构建消息体
         *
         * @return 消息体
         * @author
         **/
        @SuppressWarnings("rawtypes")
        public <T extends Map> Map<String, Object> toResult(T model) {
            Map<String, Object> map = buildMap();
            map.put("data", model);
            return map;
        }

        /**
         * 构建消息体
         *
         * @return 消息体
         * @author
         **/
        private Map<String, Object> buildMap() {
            Map<String, Object> map = new HashMap<>(6);
            map.put("success", this.success);
            map.put("msg", this.msg);
            map.put("code", this.code);
            return map;
        }
    }
}
