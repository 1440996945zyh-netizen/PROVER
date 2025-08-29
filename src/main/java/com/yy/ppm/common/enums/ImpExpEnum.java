package com.yy.ppm.common.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-10-10 10:52
 */
@Getter
public enum ImpExpEnum {

    IN("进口"),
    OUT("出口");

    private final String name;

    ImpExpEnum(String name) {
        this.name = name;
    }

    public static boolean isContains(String name) {
        return Arrays.stream(InOutPortEnum.values()).anyMatch(v1 -> v1.getName().equals(name));
    }
}
