package com.yy.common.flowable.enums;

/**
 * @author FanQi
 * @version 1.0
 * @date 2026/3/12 16:04
 */
public enum ApprovalStatusEnum {


    /**
     * 通用审批状态
     */
    ZERO("0", "未发起"),
    ONE("1", "审批中"),
    TWO("2", "审批通过"),
    THREE("3", "审批不通过"),
    FOUR("4", "已办结"),

    ;

    ApprovalStatusEnum(String code, String comment) {
        this.code = code;
        this.comment = comment;
    }

    public String code() {
        return code;
    }

    /**
     * 枚举code
     **/
    private String code;

    /**
     * 枚举注释
     **/
    private String comment;
}
