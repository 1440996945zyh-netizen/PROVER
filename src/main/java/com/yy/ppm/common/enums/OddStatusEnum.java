package com.yy.ppm.common.enums;

import lombok.Getter;

/**
 * @Auther wangxd
 * @Description 零工申请状态
 * @Date 2023-12-12 16:15
 */
@Getter
public enum OddStatusEnum {

    APPLY("10", "已申请"),
    REPORT("20", "已填报"),
    CONFIRM("30", "已确认"),
    FIRST_APPROVE("40", "一级审批"),
    SECOND_APPROVE("50", "二级审批"),
    THIRD_APPROVE("60", "三级审批"),
    ABANDONED_APPROVE("99", "作废");

    private final String code;

    private final String label;

    OddStatusEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
