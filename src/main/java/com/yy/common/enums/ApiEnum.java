package com.yy.common.enums;

/**
 * @author FanQi
 * @version 1.0
 * @date 2023/5/12 9:51
 */
public enum ApiEnum {

    /**
     * 传输协议
     */
    HTTP("http://", "请求前缀"),

    HTTPS("https://", "请求前缀"),

    ;

    ApiEnum(String code, String comment) {
        this.code = code;
        this.comment = comment;
    }

    public String code() {
        return code;
    }

    /**
     * 枚举code
     **/
    private String code;

    /**
     * 枚举注释
     **/
    private String comment;
}
