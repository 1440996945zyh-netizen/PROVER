package com.yy.ppm.common.enums;

import lombok.Getter;

/**
 * @Auther wangxd
 * @Description
 * @Date 2024-01-03 09:42
 */
@Getter
public enum PrepaymentTypeEnum {

    _10("10", "货方"),
    _30("30", "船方"),
    _40("40", "杂项");

    private final String code;

    private final String comment;

    PrepaymentTypeEnum(String code, String comment) {
        this.code = code;
        this.comment = comment;
    }
}
