package com.yy.ppm.common.enums;

import lombok.Getter;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-18 16:19
 */
@Getter
public enum InoutStorageEnum {

    _10("10", "理货"),

    _20("20", "作业票"),

    _30("30", "补录"),

    _40("40", "调账"),

    _50("50", "清场"),

    _60("60", "货转"),

    _70("70", "混配"),

    _80("80", "撤销清场");

    private final String code;

    private final String label;

    InoutStorageEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
