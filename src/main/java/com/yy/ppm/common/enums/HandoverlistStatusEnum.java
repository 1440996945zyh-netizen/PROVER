package com.yy.ppm.common.enums;

import lombok.Getter;

/**
 * @Auther linqi
 * @Description 结算单状态
 * @Date 2023-09-08 15:02
 */
@Getter
public enum HandoverlistStatusEnum {

    _10("10", "未结算"),

    _20("20", "已预结"),

    _30("30", "最终结算");

    private final String code;

    private final String name;

    HandoverlistStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
