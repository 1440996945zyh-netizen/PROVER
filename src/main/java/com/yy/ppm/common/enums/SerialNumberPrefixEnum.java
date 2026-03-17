package com.yy.ppm.common.enums;

import lombok.Getter;

/**
 * 单号前缀枚举
 * 用于生成序列号时的前缀标识
 */
@Getter
public enum SerialNumberPrefixEnum {

    /**
     * 维修
     */
    REPAIR("RW", "维修"),

    /**
     * 申报
     */
    APPLICATION("PR", "申报"),

    /**
     * 入库
     */
    WAREHOUSE_IN("IP", "入库"),

    /**
     * 采购
     */
    PURCHASE("PO", "采购"),

    /**
     * 出库
     */
    WAREHOUSE_OUT("OP", "出库"),
    WAREHOUSE_OUT_APP("MO", "出库申请"),

    /**
     * 盘点
     */
    STOCK_CHECK("PD", "盘点"),

    /**
     * 项目维修申请
     */
    PROJ_APPLY("XMSQ", "项目维修申请"),

    /**
     * 结算
     */
    SETTLEMENT("JS", "结算");

    /**
     * 前缀代码
     */
    private final String code;

    /**
     * 前缀名称
     */
    private final String name;

    SerialNumberPrefixEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据代码获取枚举
     * @param code 前缀代码
     * @return 对应的枚举，如果不存在则返回null
     */
    public static SerialNumberPrefixEnum getByCode(String code) {
        for (SerialNumberPrefixEnum item : values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }
}

