package com.yy.ppm.common.enums;

import lombok.Getter;

/**
 * @Auther linqi
 * @Description 清场标记
 * @Date 2023-08-21 14:01
 */
@Getter
public enum CleanMassSignEnum {

    _0("0", "否"),

    _1("1", "是");

    private final String code;

    private final String name;

    CleanMassSignEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
