package com.yy.ppm.common.enums;

import lombok.Getter;

/**
 * @Author linqi
 * @Description
 * @Date 2023-07-04 14:38
 */
@Getter
public enum ContractStatusEnum {

    签订("10", "签订"),
    生效("20", "生效"),
    ;

    private final String code;

    private final String name;

    ContractStatusEnum(String code, String comment) {
        this.code = code;
        this.name = comment;
    }
}
