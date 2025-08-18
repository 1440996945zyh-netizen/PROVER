package com.yy.ppm.common.enums;

import lombok.Getter;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-11-11 17:02
 */
@Getter
public enum TrateStatusEnum {

    待发布("10", "待发布"),

    已发布("20", "已发布");

    private final String code;

    private final String comment;

    TrateStatusEnum(String code, String comment) {
        this.code = code;
        this.comment = comment;
    }
}
