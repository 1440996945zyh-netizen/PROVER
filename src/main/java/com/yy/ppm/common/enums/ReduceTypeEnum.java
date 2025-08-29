package com.yy.ppm.common.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ReduceTypeEnum {

    /**
     * 免堆存费
     */
    _0("0", "FREE"),

    /**
     * 减免指定天数
     */
    _1("1", "DAYS"),

    /**
     * 减免至指定日期
     */
    _2("2", "DATE");

    private final String code;

    private final String comment;

    ReduceTypeEnum(String code, String comment) {
        this.code = code;
        this.comment = comment;
    }

    public static boolean isContains(String code) {
        return Arrays.stream(ReduceTypeEnum.values()).anyMatch(v1 -> v1.getCode().equals(code));
    }
}
