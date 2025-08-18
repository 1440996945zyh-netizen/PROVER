package com.yy.ppm.common.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * @Author linqi
 * @Description
 * @Date 2023-07-05 11:40
 */
@Getter
public enum InOutPortEnum {

    IN("IN", "进口"),

    OUT("OUT", "出口"),

    INOUT("INOUT", "进出口");

    private final String code;

    private final String name;

    InOutPortEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static boolean isContains(String code) {
        return Arrays.stream(InOutPortEnum.values()).anyMatch(v1 -> v1.getCode().equals(code));
    }
}
