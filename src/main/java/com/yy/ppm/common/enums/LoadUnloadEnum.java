package com.yy.ppm.common.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * @Author linqi
 * @Description
 * @Date 2023-07-12 16:13
 */
@Getter
public enum LoadUnloadEnum {

    UNLOAD("卸"),

    LOAD("装"),

    LOAD_UNLOAD("装卸");

    private final String name;

    LoadUnloadEnum(String name) {
        this.name = name;
    }


    public static boolean isContains(String name) {
        return Arrays.stream(LoadUnloadEnum.values()).anyMatch(v1 -> v1.getName().equals(name));
    }
}
