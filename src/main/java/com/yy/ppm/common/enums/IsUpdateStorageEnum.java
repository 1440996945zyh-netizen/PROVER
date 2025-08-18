package com.yy.ppm.common.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * @Auther linqi
 * @Description 是否更新港存
 * @Date 2023-08-22 15:23
 */
@Getter
public enum IsUpdateStorageEnum {

    _0("0", "否"),

    _1("1", "是");

    private final String code;

    private final String name;

    IsUpdateStorageEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static boolean isContains(String code) {
        return Arrays.stream(IsUpdateStorageEnum.values()).anyMatch(v1 -> v1.getCode().equals(code));
    }
}
