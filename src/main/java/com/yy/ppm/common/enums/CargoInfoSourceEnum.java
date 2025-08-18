package com.yy.ppm.common.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * @Auther linqi
 * @Description 场存来源
 * @Date 2023-11-24 10:43
 */
@Getter
public enum CargoInfoSourceEnum {

    卸船,

    集港,

    货转,

    混配;

    public static CargoInfoSourceEnum match(String code) {
        return Arrays.stream(CargoInfoSourceEnum.values()).filter(v1 -> v1.name().equals(code)).findFirst().orElse(null);
    }
}
