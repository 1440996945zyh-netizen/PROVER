package com.yy.ppm.common.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther linqi
 * @Description 字典枚举
 * @Date 2023-10-10 14:14
 */
@Getter
public enum DictTypeEnum {

    SPECIAL_OPERATION("SPECIAL_OPERATION", "特殊作业情况"),
    SOURCE_TARGET_TYPE("SOURCE_TARGET_TYPE", "源 目的"),
    UNIT("UNIT", "计量单位"),
    DNBILLTYPE("DNBILLTYPE", "借方票据类型"),
    CARGO_CATEGORY("CARGO_CATEGORY", "货物类型"),
    DEPT_LEVEL("DEPT_LEVEL", "组织架构级别"),
    DEPOSIT_BASIS("DEPOSIT_BASIS", "预缴依据"),
    JOB_LOCATION("JOB_LOCATION", "作业位置"),
    PIECE_PROJECT("PIECE_PROJECT", "计件工资项目"),
    BANK("BANK", "计量单位银行"),
    ENTERPRISE_PROPERTIES("ENTERPRISE_PROPERTIES", "计量单位企业性质"),
    PORT_TYPE("PORT_TYPE", "港口类型"),
    PROCESS_WORKTOOL("PROCESS_WORKTOOL", "工属具"),
    SHIP_TON("SHIP_TON", "货物吨位"),
    TRUST_STATUS("TRUST_STATUS", "指令状态"),
    HANDOVER_TYPE("HANDOVER_TYPE", "交接方式"),
    PROCESS_PERSONNEL_TYPE("PROCESS_PERSONNEL_TYPE", "人员类型"),
    PAYMENT_TYPE("PAYMENT_TYPE", "账期类型"),
    DIS_CLOSE_SAIL_REASON("DIS_CLOSE_SAIL_REASON", "封航原因"),
    TUG_SERVICE_TYPE("TUG_SERVICE_TYPE", "服务内容"),
    STORAGE_TYPE("STORAGE_TYPE", "库场类型"),
    UNIT_TYPE("UNIT_TYPE", "单位类型"),
    CARGO_CLASS("CARGO_CLASS", "货类"),
    CARGO_MARK("CARGO_MARK", "唛头"),
    STOP_REASON_CLASS("STOP_REASON_CLASS", "停工原因分类"),
    DRINK_TYPE("DRINK_TYPE", "引水类型"),
    MACHINE_LOCATION("MACHINE_LOCATION", "机械位置"),
    BWLX("BWLX", "部位类型"),
    STATEMENT_STATUS("STATEMENT_STATUS", "结算状态"),
    DAMAGED_REASON("DAMAGED_REASON", "残损原因"),
    RATE_TYPE("RATE_TYPE", "费用类型"),
    INNERTRANSPORT_TYPE("INNERTRANSPORT_TYPE", "内倒类型"),
    WORK_TYPE("WORK_TYPE", "服务类型"),
    SALARY_TYPE("SALARY_TYPE", "计件工资类型"),
    SETTLEMENT_BASIS("SETTLEMENT_BASIS", "结算依据"),
    WORK_AREA("WORK_AREA", "工作区域"),
    MACHINE_CLASS_TYPE("MACHINE_CLASS_TYPE", "设备类别"),
    CUSTOMER_PROPERTY("CUSTOMER_PROPERTY", "客户属性"),
    HATCH_COVER_TYPE("HATCH_COVER_TYPE", "舱口盖类型"),
    FEE_ITEM_TYPE("FEE_ITEM_TYPE", "费目类型"),
    PROCESS_TYPE("PROCESS_TYPE", "过程种类"),
    IN_OUT_PORT_TYPE("IN_OUT_PORT_TYPE", "进出港配置"),
    SPECIAL_CASE("SPECIAL_CASE", "特殊情况"),
    INOUT_TYPE("INOUT_TYPE", "出入库"),
    SALARY_DAY("SALARY_DAY", "计件工资天数"),
    COLLECTE_FLAG("COLLECTE_FLAG", "集疏类型"),
    NATION("NATION", "国籍"),
    NORMS_TYPE("NORMS_TYPE", "规格"),
    IN_OUT_STORAGE_TYPE("IN_OUT_STORAGE_TYPE", "入库标识"),
    POST("POST", "岗位"),
    WORK_TICKET_STATUS("WORK_TICKET_STATUS", "作业票状态"),
    VERSION_CONTROL("VERSION_CONTROL", "版本控制"),
    DIS_TUG_REASON("DIS_TUG_REASON", "拖轮服务非标准原因"),
    STORAGE_YARD_TYPE("STORAGE_YARD_TYPE", "库场类型"),
    SHIP_TYPE("SHIP_TYPE", "船型"),
    NAVIGISION_AREA("NAVIGISION_AREA", "航区"),
    ANCHORAGE("ANCHORAGE", "锚地"),
    BERTH_LEVEL("BERTH_LEVEL", "设计船型"),
    TRADE_TYPE("TRADE_TYPE", "内外贸"),
    METERING_UNIT("METERING_UNIT", "周期单位"),
    BONUS_PENALTY_TYPE("BONUS_PENALTY_TYPE", "奖惩类型"),
    INOUT_STORAGE("INOUT_STORAGE", "进出场类型"),
    CNBILLTYPE("CNBILLTYPE", "贷方票据类型"),
    E_ENERGY("E_ENERGY", "能源类型"),
    E_TECH("E_TECH", "设备技术情况"),
    E_UNIT("E_UNIT", "设备计量单位"),
    E_STATUS("E_STATUS", "设备使用状态"),
    E_VATION("E_VATION", "改造类型"),
    EMISSION("EMISSION", "排放标准"),
    STOP_REASON_TYPE("STOP_REASON_TYPE", "停时类型"),
    EFFICIENCY_TYPE("EFFICIENCY_TYPE", "效率类别"),
    SALARY_STATUS("SALARY_STATUS", "计件工资审核状态"),
    DEBTORPAY_PAYMENT_TYPE("DEBTORPAY_PAYMENT_TYPE", "收据付款类型"),
    TAX_TYPE("TAX_TYPE", "纳税类型"),
    IS_USE("IS_USE", "在用状态"),
    TRANSPORT_TYPE("TRANSPORT_TYPE", "运输工具"),
    EQUIP_CONFIG("EQUIP_CONFIG", "载具配置"),
    ROLE_GROUP("ROLE_GROUP", "角色组"),
    COMPARE_FORMULA("COMPARE_FORMULA", "公式"),
    IN_OUT_TYPE("IN_OUT_TYPE", "进出口"),
    IS("IS", "是否状态"),
    COMPANY_USER_TYPE("COMPANY_USER_TYPE", "企业用户类型"),
    EQUP_PART("EQUP_PART", "机构"),
    HANDOVERLIST_STATUS("HANDOVERLIST_STATUS", "结算单状态"),
    DYNAMIC_TYPE("DYNAMIC_TYPE", "动态类型"),
    BANK_PAY_METHOD("BANK_PAY_METHOD", "银行付款方式"),
    BANK_PAYMENT_TYPE("BANK_PAYMENT_TYPE", "付款类型"),
    WEIQIAOYARD("WEIQIAOYARD", "魏桥写卡"),
    WORK_ORIGN("WORK_ORIGN", "操作源"),
    SHIPSTATUS("SHIPSTATUS", "船舶状态"),
    PURPOSE_TYPE("PURPOSE_TYPE", "装卸类型"),
    ROUTE("ROUTE", "航线"),
    CUSTOMER_TYPE("CUSTOMER_TYPE", "客户类型"),
    SHIP_KIND("SHIP_KIND", "船舶类型"),
    BERTH_USE("BERTH_USE", "泊位用途"),
    TAX_RATE("TAX_RATE", "税率"),
    TAX_INVOICE("TAX_INVOICE", "税务服务发票"),
    CURRENCY("CURRENCY", "货币类型"),
    PREPAYMENT_TYPE("PREPAYMENT_TYPE", "预缴类型"),
    CONTRACT_TYPE("CONTRACT_TYPE", "合同类型"),
    PAY_TYPE("PAY_TYPE", "付费方式"),
    WORK_SCHEDULE("WORK_SCHEDULE", "工班设置"),
    MENU_TYPE("MENU_TYPE", "用户类型"),
    PACKAGE_TYPE("PACKAGE_TYPE", "包装"),
    SOLID_FLOW_FLAG("SOLID_FLOW_FLAG", "流固机"),
    SIDE("SIDE", "舷靠"),
    BERTH_NATURE("BERTH_NATURE", "泊位性质"),
    TRUCK_MODEL("TRUCK_MODEL", "汽车车型"),
    HANDOVERLIST_TYPE("HANDOVERLIST_TYPE", "结算单类型"),
    ODD_WORK_TYPE("ODD_WORK_TYPE", "零工作业分类"),
    STANDARDIZED_CARGO("STANDARDIZED_CARGO", "标准化货物"),
    BPM_BUSINESS_TYPE("BPM_BUSINESS_TYPE", "流程业务类型"),
    BPM_LISTENER_TASK_EVENT("BPM_LISTENER_TASK_EVENT", "任务监听器事件"),
    BPM_LISTENER_EXECUTION_EVENT("BPM_LISTENER_EXECUTION_EVENT", "执行监听器事件"),
    COMMON_PHRASES_TYPE("COMMON_PHRASES_TYPE", "常用审批语类型"),
    MODEL_TYPE("MODEL_TYPE", "模板类型"),
    CERTIFICATE_TYPE("CERTIFICATE_TYPE", "证件类型"),
    EXPENSE_TYPE("EXPENSE_TYPE", "维修费用类型"),;

    private final String type;

    private final String name;

    DictTypeEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public static String match(String type) {
        return Arrays.stream(DictTypeEnum.values()).map(DictTypeEnum::getType).filter(v1 -> v1.equals(type)).findFirst().orElse(StringUtils.EMPTY);
    }

    @Getter
    public enum DictDataEnum {

        SOURCE_TARGET_TYPE_01("SOURCE_TARGET_TYPE", "01", "船舶"),
        SOURCE_TARGET_TYPE_02("SOURCE_TARGET_TYPE", "02", "驳船"),
        SOURCE_TARGET_TYPE_03("SOURCE_TARGET_TYPE", "03", "汽车"),
        SOURCE_TARGET_TYPE_04("SOURCE_TARGET_TYPE", "04", "火车"),
        SOURCE_TARGET_TYPE_05("SOURCE_TARGET_TYPE", "05", "场地"),
        UNIT_TON("UNIT", "ton", "吨"),
        UNIT_HOUR("UNIT", "hour", "小时"),
        UNIT_DAY("UNIT", "day", "天"),
        UNIT_DAYNRT("UNIT", "DAYNRT", "天数*净吨"),
        UNIT_VOL("UNIT", "VOL", "体积"),
        UNIT_NRT("UNIT", "NRT", "船舶净吨"),
        UNIT_HEAD("UNIT", "HEAD", "人数"),
        UNIT_MOVE("UNIT", "MOVE", "移动"),
        UNIT_STOW("UNIT", "STOW", "存储(周)"),
        UNIT_FANG("UNIT", "FANG", "方"),
        UNIT_BDAY("UNIT", "BDAY", "靠泊天数"),
        UNIT_STOD("UNIT", "STOD", "存储(天)"),
        UNIT_YEAR("UNIT", "YEAR", "年度"),
        UNIT_TEU("UNIT", "TEU", "TEU"),
        UNIT_SCN("UNIT", "SCN", "航次"),
        UNIT_KWH("UNIT", "KWH", "度"),
        UNIT_WEEK("UNIT", "WEEK", "周期"),
        UNIT_WG("UNIT", "WG", "吨,最大量"),
        UNIT_HHR("UNIT", "HHR", "半小时"),
        UNIT_HP("UNIT", "HP", "马力"),
        UNIT_LOA("UNIT", "LOA", "全长(船)"),
        UNIT_QTY("UNIT", "QTY", "件数"),
        UNIT_UNIT("UNIT", "UNIT", "次"),
        UNIT_ACRE("UNIT", "ACRE", "亩数"),
        UNIT_RMB("UNIT", "RMB", "元"),
        UNIT_ITEM("UNIT", "item", "个"),
        UNIT_DMT("UNIT", "dmt", "吨/天"),
        STOP_REASON_CLASS_1("STOP_REASON_CLASS", "1", "港方原因"),
        STOP_REASON_CLASS_2("STOP_REASON_CLASS", "2", "船方原因"),
        STOP_REASON_CLASS_3("STOP_REASON_CLASS", "3", "货方原因"),
        STOP_REASON_CLASS_4("STOP_REASON_CLASS", "4", "其他原因"),
        STOP_REASON_CLASS_5("STOP_REASON_CLASS", "5", "天气原因"),
        DEPT_LEVEL_0("DEPT_LEVEL", "0", "集团"),
        DEPT_LEVEL_1("DEPT_LEVEL", "1", "公司"),
        DEPT_LEVEL_2("DEPT_LEVEL", "2", "部门"),
        DEPT_LEVEL_3("DEPT_LEVEL", "3", "班组"),
        DEPT_LEVEL_4("DEPT_LEVEL", "4", "作业班组");

        private final String type;

        private final String value;

        private final String label;

        DictDataEnum(String type, String value, String label) {
            this.type = type;
            this.value = value;
            this.label = label;
        }

        public static List<DictDataEnum> listDictData(String type) {
            return Arrays.stream(DictDataEnum.values()).filter(v1 -> v1.getType().equals(type)).collect(Collectors.toList());
        }

        public static String match(DictTypeEnum type, String code) {
            return Arrays.stream(DictDataEnum.values())
                    .filter(v1 -> v1.getType().equals(type.getType()) && v1.getValue().equals(code))
                    .map(DictDataEnum::getValue)
                    .findFirst()
                    .orElse(StringUtils.EMPTY);
        }
    }
}
