package com.yy.ppm.common.enums;

import lombok.Getter;

/**
 * @Author yy
 * @Description 班次
 * @Date 2023-07-04 14:38
 */
@Getter
public enum ClassCodeEnum {

    DAY("01", "白班"),
    NIGHT("02", "夜班");

    private final String code;

    private final String name;

    ClassCodeEnum(String code, String comment) {
        this.code = code;
        this.name = comment;
    }
}
