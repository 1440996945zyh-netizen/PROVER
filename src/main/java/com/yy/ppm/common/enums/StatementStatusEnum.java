package com.yy.ppm.common.enums;

import lombok.Getter;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-14 16:42
 */
@Getter
public enum StatementStatusEnum {

    _10("10", "生产结算"),
    _20("20", "商务结算"),
    _30("30", "计费审核"),
    _31("31", "商务确认"),
    _40("40", "部分开票"),
    _50("50", "已开票");

    private final String code;

    private final String comment;

    StatementStatusEnum(String code, String comment) {
        this.code = code;
        this.comment = comment;
    }
}
