package com.yy.ppm.common.enums;

import lombok.Getter;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-15 17:15
 */
@Getter
public enum WorkTicketStatusEnum {

    _10("10", "待审核"),

    _20("20", "已审核");

    private final String code;

    private final String name;

    WorkTicketStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
