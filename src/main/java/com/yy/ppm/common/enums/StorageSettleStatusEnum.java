package com.yy.ppm.common.enums;

import lombok.Getter;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-11-25 15:37
 */
@Getter
public enum StorageSettleStatusEnum {

    _10("10", "未审核"),

    _20("20", "已审核");

    private final String code;

    private final String comment;

    StorageSettleStatusEnum(String code, String comment) {
        this.code = code;
        this.comment = comment;
    }
}
