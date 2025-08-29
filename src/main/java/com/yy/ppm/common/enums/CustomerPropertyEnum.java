package com.yy.ppm.common.enums;

/**
 * 客户属性
 *
 * @author yy
 */
public enum CustomerPropertyEnum {

    HD("HD", "1", "货代"),
    HZ("HZ", "2","货主"),
    CD("CD", "3","船代"),
    CZ("CZ",  "4" ,"船主"),
    MTZYGS("MTZYGS", "5","码头作业公司"),
    XZ("XZ", "6","箱主"),
    FFF("FFF", "7","付费方"),
    SHR("SHR", "8","收货人"),
    CYR("CYR", "9","承运人"),
    KH("KH", "10","客户"),
    CGS("CGS", "11","船公司"),
    WZSBTGS("WZSBTGS", "12","外租设备提供商"),
    QYZSDW("QYZSDW", "13","企业直属单位"),
    WTR("WTR", "14","委托人"),
    TYR("TYR", "15","托运人"),
    GYS("GYS", "16","供应商"),
    WLCD("WLCD", "17", "物流车队"),
    STATUS_10("PASS", "", "通过审批的客户");

    private String code;
    private String value;
    private String comment;


    CustomerPropertyEnum(String code, String value, String comment) {
        this.code = code;
        this.value = value;
        this.comment = comment;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public String getComment() {
        return comment;
    }

}
