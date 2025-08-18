package com.yy.ppm.common.enums;

import lombok.Getter;

/**
 * @Auther linqi
 * @Description 出入库
 * @Date 2023-08-18 16:15
 */
@Getter
public enum RateCodeEnum {

    STOWAGE("MS00240", "库场使用费");

    private final String code;

    private final String label;

    RateCodeEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
