package com.yy.ppm.common.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 自动编号
 */
public interface AutoNumEnum {

    /**
     * Auth服务自动编号
     * 两种编号规则，1、父编号前方一致，2、固定格式自增长
     */
    @Getter
    public static enum BusinessAutoEnum {

        DEPT("DEPT", "SYS_DEPT","DEPT_NO", "", "", 4, "动态传值", "部门"),

        CARGO_CATEGORY("CARGO_CATEGORY", "M_CARGO_CATEGORY", "CARGO_CATEGORY_CODE", "CARGO_TYPE_CODE", "", 4, "", "货类"),
        CARGO("CARGO", "M_CARGO", "CARGO_CODE", "CARGO_CATEGORY_CODE", "", 4, "", "货类"),

        WORK_PROCESS("WORK_PROCESS", "m_work_process", "PROCESS_CD", "", "", 4, "", "作业过程"),
        SUB_WORK_PROCESS("SUB_WORK_PROCESS", "m_work_process", "PROCESS_CD", "PROCESS_CD", "", 4, "动态传值", "子作业过程"),

        MACHINE_TYPE("MACHINE_TYPE", "M_MACHINE_TYPE", "MAC_TYPE_CODE", "", "", 4, "", "机械类型"),
        MACHINE_MODEL("MACHINE_MODEL", "M_MACHINE_TYPE_MODEL", "MODEL_CODE", "MAC_TYPE_CODE", "", 4, "", "机械型号"),

        SCN("SCN", "T_DIS_SHIPVOYAGE", "SCN", "", "yymm", 4, "", "SN号"),

        TRUST_NO("TRUST_NO", "T_BUS_TRUST", "TRUST_NO", "", "yymd", 3, "动态传值", "指令编号"),
        ENTRUST_NO("ENTRUST_NO", "T_BUS_CUSTOMER_ENTRUST", "ENTRUST_NO", "", "yymd", 3, "动态传值", "委托单号"),

        MAIN_CARGO_INFO("MAIN_CARGO_INFO", "T_BUS_CARGO_INFO", "CARGO_INFO_NO", "", "yymd", 3, "PH", "票货"),
        ODD_PLAN("ODD_PLAN", "T_PRD_ODD_WORK_PLAN", "ODD_PLAN_NO", "", "yymd", 3, "LG", "零工"),
        SUB_CARGO_INFO("SUB_CARGO_INFO", "T_BUS_CARGO_INFO", "CARGO_INFO_NO", "", "", 2, "动态传值", "转移后票货"),

        WORK_PLAN_SHIP("WORK_PLAN_SHIP", "T_PRD_WORK_PLAN", "PLAN_NO", "", "ymd", 4, "C", "船舶计划"),
        WORK_PLAN_SUNDRY("WORK_PLAN_SUNDRY", "T_PRD_WORK_PLAN", "PLAN_NO", "", "ymd", 4, "Z", "杂项计划"),
        WORK_PLAN_RESHIPMENT("WORK_PLAN_RESHIPMENT", "T_PRD_WORK_PLAN", "PLAN_NO", "", "ymd", 4, "D", "转运计划"),
        WROK_PLAN_TRANSPORT("WROK_PLAN_TRANSPORT", "T_PRD_WORK_PLAN", "PLAN_NO", "", "ymd", 4, "J", "集疏港计划"),
        BIG_BANK_CUSTOMER_PREPAYMENT("BIG_PREPAYMENT_CODE", "T_FD_BANK_CUSTOMER_PREPAYMENT", "PREPAYMENT_CODE", "", "yy", 5, "DYJ", "大预缴"),
        CARGO_BANK_CUSTOMER_PREPAYMENT("CARGO_PREPAYMENT_CODE", "T_FD_BANK_CUSTOMER_PREPAYMENT", "PREPAYMENT_CODE", "", "yy", 6, "YJ", "货物预缴"),
        SYS_INVOICE_CODE("SYS_INVOICE_CODE", "T_FD_INVOICE", "SYS_INVOICE_CODE", "", "ymd", 4, "INVOICE", "系统发票编号"),
        DEBTORPAYMENT_NO("DEBTORPAYMENT_NO", "T_FD_DEBTORPAYMENT", "DEBTORPAYMENT_NO", "", "y", 5, "SJ", "收据编号"),
        STATEMENT_NO("STATEMENT_NO", "T_COST_STATEMENT", "STATEMENT_NO", "", "ymd", 3, "", "结算单号"),
        BUSINESS_NO("BUSINESS_NO", "T_BUS_TRUST_CARGO", "BUSINESS_NO", "", "", 2, "动态传值", "业务号（指令编号+两位流水）"),
        WEIGHT_PLAN_NO("WRIGHT_PLAN_NO", "T_WEIGHT_PLAN", "PLAN_NO", "", "ymd", 3, "SD", "杂货计划号（SD+年（23）+月+日+3个序号）");

        // 类型
        private String code;
        // 编号表名
        private String tableName;
        // 编号列
        private String colName;
        // 编号父字段名称
        private String parentColName;
        // 编号中年月日格式 （y, ym, ymd)
        private String ymd;
        // 编号长度 例如 0001 4位， 00020001依赖付字段值0002自增长0001，0001为4位
        private int codeLength;
        // 编号固定以XXX开头
        private String startWidth;
        // 备注
        private String comment;

        private BusinessAutoEnum(String code, String tableName, String colName, String parentColName, String ymd,
                             int codeLength, String startWidth, String comment) {
            this.code = code;
            this.tableName = tableName;
            this.colName = colName;
            this.parentColName = parentColName;
            this.ymd = ymd;
            this.codeLength = codeLength;
            this.startWidth = startWidth;
            this.comment = comment;
        }

        public static BusinessAutoEnum getItemByCode(String code) {
            for (AutoNumEnum.BusinessAutoEnum item : values()) {
                if (item.getCode().equals(code)) {
                    return item;
                }
            }
            return null;
        }

        public static Map getMapByCode(String code) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            for (AutoNumEnum.BusinessAutoEnum item : values()) {
                if (item.getCode() == code) {
                    map.put("tableName", item.getTableName());
                    map.put("colName", item.getColName());
                    map.put("parentColName", item.getParentColName());
                    map.put("ymd", item.getYmd());
                    map.put("codeLength", item.getCodeLength());
                    map.put("startWidth", item.getStartWidth());
                }
            }
            return map;
        }

    }

}
