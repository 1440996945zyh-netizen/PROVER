package com.yy.ppm.common.enums;

import lombok.Getter;

/**
 * @Auther linqi
 * @Description 出入库
 * @Date 2023-08-18 16:15
 */
@Getter
public enum InoutTypeEnum {

    _1("1", "出库"),

    _2("2", "入库");

    private final String code;

    private final String label;

    InoutTypeEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
