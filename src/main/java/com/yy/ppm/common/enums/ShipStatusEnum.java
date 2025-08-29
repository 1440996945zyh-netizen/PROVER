package com.yy.ppm.common.enums;

import lombok.Getter;

/**
 * @Author linqi
 * @Description 船舶状态枚举
 * @Date 2023-07-04 14:38
 */
@Getter
public enum ShipStatusEnum {

    ZUOFEI("00", "作废"),

    YUBAO("10", "预报"),

    JVSHOU("11", "拒收"),

    JIESHOU("20", "接收"),

    DIMAO("30", "抵锚"),

    QIMAO("40", "起锚"),

    KAOBO("50", "靠泊"),

    YIBO("60", "移泊"),

    KAIGONG("70", "开工"),

    TINGGONG("80", "停工"),

    FUGONG("90", "复工"),

    WANGONG("100", "完工"),

    LIBO("110", "离泊"),

    LIGANG("120", "离港"),

    TE_SHU_TING_BO_FEI("130", "特殊停泊费");

    private final String code;

    private final String name;

    ShipStatusEnum(String code, String comment) {
        this.code = code;
        this.name = comment;
    }
}
