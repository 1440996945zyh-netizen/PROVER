package com.yy.ppm.common.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * 源-目标
 *
 * @author yy
 */
@Getter
public enum DirectionEnum {

    SOURCE("1", "源"),
    TARGET("2", "目标");

    private final String code;

    private final String name;

    DirectionEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static boolean isContains(String code) {
        return Arrays.stream(DirectionEnum.values()).anyMatch(v1 -> v1.getCode().equals(code));
    }
}
