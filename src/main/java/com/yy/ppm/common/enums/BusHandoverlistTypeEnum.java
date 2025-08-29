package com.yy.ppm.common.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-07 15:51
 */
@Getter
public enum BusHandoverlistTypeEnum {

    ZHUANGXIECHUAN("1", "装卸船"),

    LUJILUSHU("2", "陆集陆疏");

    private final String code;

    private final String name;

    BusHandoverlistTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static boolean contains(String code) {
        return Arrays.stream(BusHandoverlistTypeEnum.values()).anyMatch(v1 -> v1.getCode().equals(code));
    }
}
