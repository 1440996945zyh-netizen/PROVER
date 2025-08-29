package com.yy.ppm.common.enums;

import lombok.Getter;

/**
 * @Auther chenfs
 * @Description 计件工资审核状态
 * @Date 2023-08-18 15:33
 */
@Getter
public enum SalaryStatusEnum {

    _10("10", "待审核"),

    _20("20", "生产已审核"),

    _30("30", "HR审核");

    private final String code;

    private final String label;

    SalaryStatusEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
