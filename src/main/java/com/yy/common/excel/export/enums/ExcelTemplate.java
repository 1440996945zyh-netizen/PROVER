package com.yy.common.excel.export.enums;

import lombok.Getter;

/**
 * @Author linqi
 * @Description excel模板枚举
 * @Date 2023-05-18 13:55
 */
@Getter
public enum ExcelTemplate {

    EXAMPLE1("exceltemplates/示例模板1.xlsx", "示例1"),

    EXAMPLE2("exceltemplates/示例模板2.xlsx", "示例2"),

    FEIYONGMINGXI("exceltemplates/港口作业费用明细通知单.xlsx", "港口作业费用明细通知单"),

    FEIYONGMINGXIOTHER("exceltemplates/港口作业费用明细通知单其他.xlsx", "港口作业费用明细通知单其他"),

    FEIYONGMINGXISTACK("exceltemplates/港口作业费用明细通知单堆存费.xlsx", "港口作业费用明细通知单堆存费"),

    DAYNIGHTPLAN("exceltemplates/集疏港昼夜计划散货.xlsx", "集疏港昼夜计划散货"),
    TINGBOFEI("exceltemplates/停泊费账单.xlsx", "停泊费账单"),
    TRUST_FEE_EXPORT_TEMPLATE("exceltemplates/通知单费用导出模板.xlsx", "停泊费账单"),
    STORAGE_FEE_COST_DETAIL_TEMPLATE("exceltemplates/堆存费结算导出模板.xlsx", "堆存费结算导出模板"),
    SHIP_WORK_REPORT("exceltemplates/单船作业报告.xlsx", "单船作业报告"),
    STACK_SIGN_MODEL("exceltemplates/stackSignModel.xlsx", "stackSignModel");



    private final String templatePath;

    private final String comment;

    ExcelTemplate(String templatePath, String comment) {
        this.templatePath = templatePath;
        this.comment = comment;
    }
}
