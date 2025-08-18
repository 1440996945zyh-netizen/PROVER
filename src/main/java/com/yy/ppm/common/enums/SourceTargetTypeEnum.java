package com.yy.ppm.common.enums;

import lombok.Getter;

/**
 * @Auther linqi
 * @Description 操作过程源和目标
 * @Date 2023-08-18 15:33
 */
@Getter
public enum SourceTargetTypeEnum {

    _01("01", "船舶"),

    _02("02", "驳船"),

    _03("03", "汽车"),

    _04("04", "火车"),

    _05("05", "场地"),

    _06("06", "岸");

    private final String code;

    private final String label;

    SourceTargetTypeEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
