package com.yy.ppm.finance.bean.dto;

public enum LqSdInvoiceEnum {


    SZHDZ_ZP("081","数字化电子专票"),
    SZHDZ_PP("082","数字化电子普票"),

    INVOICE_TYPE_BULE("0","蓝字发票"),
    INVOICE_TYPE_RED("1","红字发票"),

    SPECIAL_INVOICE_MARK("00","非特定要素"),

    ZS_TYPE_CODE_0("0","普通征税"),
    ZS_TYPE_CODE_2("2","差额征税—差额开票"),
    ZS_TYPE_CODE_3("3","差额征税-全额开票"),

    TAX_MARK_HS("1","含税"),
    TAX_MARK_BHS("0","不含税"),

    JFFS_BJF("0","不交付"),
    JFFS_YX("1","邮箱"),
    JFFS_DX("2","短信"),
    JFFS_WX("3","微信插卡包"),
    JFFS_DZ("5","定制"),

    BUYER_TYPE_QY("1","非自然人-企业"),
    BUYER_TYPE_FQY("2","非企业单位"),
    BUYER_TYPE_ZRR("3","自然人"),

    NATURE_LINES_0("0","正常行"),
    NATURE_LINES_1("1","折扣行"),
    NATURE_LINES_2("2","被折扣行"),

    CHECKER("马彦平","复核人"),
    INVOICE_CLERK("崔波","开票人"),


    REQUEST_TYPE_0("0","pdf短链接"),
    REQUEST_TYPE_1("1","pdf版式文件"),
    REQUEST_TYPE_4("4","xml签名文件短链接"),
    REQUEST_TYPE_5("5","xml签名文件"),

    RED_REASON_1("1","销货退回"),
    RED_REASON_2("2","开票有误"),
    RED_REASON_3("3","服务中止"),
    RED_REASON_4("4","销售折让"),


    RED_APPLY_FOR_REASON_BUYER("1","购方"),
    RED_APPLY_FOR_REASON_SELLER("2","销方"),

    ;

    /**
     * 枚举code
     **/
    private String code;

    /**
     * 枚举注释
     **/
    private String comment;

    LqSdInvoiceEnum(String code, String comment) {
        this.code = code;
        this.comment = comment;
    }


    public String getCode() {
        return code;
    }
}

