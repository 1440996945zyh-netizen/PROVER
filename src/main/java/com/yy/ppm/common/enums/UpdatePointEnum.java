package com.yy.ppm.common.enums;

import lombok.Getter;

/**
 * @Auther linqi
 * @Description 场存节点
 * @Date 2023-08-22 16:45
 */
@Getter
public enum UpdatePointEnum {

    _1(1, "理货"),

    _2(2, "签票");

    private final Integer code;

    private final String name;

    UpdatePointEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
