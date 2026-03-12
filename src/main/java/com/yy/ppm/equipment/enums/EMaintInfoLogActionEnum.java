package com.yy.ppm.equipment.enums;

import lombok.Getter;

/**
 * 维修工单操作日志动作枚举
 */
@Getter
public enum EMaintInfoLogActionEnum {

    UPDATE("UPDATE", "修改"),
    REPORT("REPORT", "提报"),
    DISPATCH("DISPATCH", "派工"),
    START_MAINT("START_MAINT", "开始维修"),
    END_MAINT("END_MAINT", "结束维修"),
    ACCEPT_PASS("ACCEPT_PASS", "验收通过"),
    ACCEPT_REJECT("ACCEPT_REJECT", "验收不通过"),
    CANCEL("CANCEL", "作废");

    private final String code;

    private final String name;

    EMaintInfoLogActionEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
