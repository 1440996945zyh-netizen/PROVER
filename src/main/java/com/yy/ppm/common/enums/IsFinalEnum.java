package com.yy.ppm.common.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * @Auther linqi
 * @Description 是否最终结算
 * @Date 2023-09-15 11:03
 */
@Getter
public enum IsFinalEnum {

    FALSE("0", "否"),

    TRUE("1", "是");

    private final String code;

    private final String name;

    IsFinalEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static boolean isContains(String code) {
        return Arrays.stream(IsFinalEnum.values()).anyMatch(v1 -> v1.getCode().equals(code));
    }
}
