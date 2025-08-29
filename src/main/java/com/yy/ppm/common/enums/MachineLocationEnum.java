package com.yy.ppm.common.enums;

import lombok.Data;
import lombok.Getter;

import java.util.Arrays;

/**
 * 源-目标
 *
 * @author yy
 */
@Getter
public enum MachineLocationEnum {

    FRONT("01", "前沿"),
    BACK("02", "后场"),
    RESHIPMENT("03", "水平"),
    ASSIST("04", "辅助");

    private final String code;

    private final String name;

    MachineLocationEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static boolean isContains(String code) {
        return Arrays.stream(MachineLocationEnum.values()).anyMatch(v1 -> v1.getCode().equals(code));
    }
}
