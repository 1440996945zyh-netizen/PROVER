package com.yy.ppm.common.enums;

import lombok.Getter;

/**
 * @Author yy
 * @Description 货权转移状态
 * @Date 2023-07-04 14:38
 */
@Getter
public enum CargoTransferEnum {

    TODO("1", "待审核"),
    BUSINESS_APPROVE("10", "商务审核"),
    YARD_APPROVE("20", "库场审核");

    private final String code;

    private final String name;

    CargoTransferEnum(String code, String comment) {
        this.code = code;
        this.name = comment;
    }
}
