package com.yy.ppm.common.enums;

import lombok.Getter;

/**
 * @Auther linqi
 * @Description 结算单类型
 * @Date 2023-09-15 10:27
 */
@Getter
public enum HandoverlistTypeEnum {

    _10("10", "船舶货方结算单"),

    _20("20", "陆集陆疏货方结算单"),

    _30("30", "船方计费"),

    _40("40", "杂项计费"),

    _50("50", "堆存费");

    private final String code;

    private final String label;

    HandoverlistTypeEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
