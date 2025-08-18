package com.yy.ppm.common.enums;

/**
 * 业务操作类型
 *
 * @author yy
 */
public enum SelectEnum {
    /** 用户 */
    USER("USER",  "sys_user","ID", "USER_NAME","用户信息"),
    /** 费目 */
    FEE_ITME("FEE_ITME","M_FEE_ITEM","ITEM_CD", "ITEM_NM","费目"),
    /** SERVICE */
    SERVICE("SERVICE", "T_BUS_SERVICE","ID", "SERVICE_NM","服务"),

    /** 作业过程（主） */
    MAIN_WORK_PROCESS("MAIN_PROCESS", "M_WORK_PROCESS","PROCESS_CD", "PROCESS_NM","作业过程"),
    /** 作业过程（子） */
    SUB_WORK_PROCESS("SUB_WORK_PROCESS", "M_WORK_PROCESS","PROCESS_CD", "PROCESS_NM","子作业过程"),
    /** 货物 */
    CARGO_INFO("CARGO_INFO","M_CARGO","CARGO_CODE", "CARGO_NAME","货物信息"),
    /** 货种 */
    CARGO_CATEGORY("CARGO_CATEGORY","M_CARGO_CATEGORY","CARGO_CATEGORY_CODE", "CARGO_CATEGORY_NAME","货种信息"),
    /** 票货 */
    BUS_CARGO_INFO("BUS_CARGO_INFO","","", "","票货信息"),
    /** 标准化货种 */
    STD_CARGO_CATEGORY("STD_CARGO_CATEGORY","M_CARGO_CATEGORY","CARGO_CATEGORY_CODE", "CARGO_CATEGORY_NAME","标准化货种信息"),


    CUSTOMER_ALL("CUSTOMER_ALL", "", "", "", "全部客户"),
    CUSTOMER_CARGO_OWNER("CUSTOMER_CARGO_OWNER", "", "", "", "货主"),
    CUSTOMER_CARGO_AGENT("CUSTOMER_CARGO_AGENT", "", "", "", "货代"),
    CUSTOMER_FLEET("CUSTOMER_FLEET", "", "", "", "物流公司"),
    CUSTOMER_SHIP_OWNER("CUSTOMER_SHIP_OWNER", "", "", "", "船主"),
    CUSTOMER_SHIP_AGENT("CUSTOMER_SHIP_AGENT", "", "", "", "船代"),

    /** 机械队 */
    DEPT_MACHINE("DEPT_MACHINE","SYS_DEPT","id", "DEPT_NAME","机械队"),
    /** 劳务队 */
    DEPT_LABOR("DEPT_LABOR","SYS_DEPT","id", "DEPT_NAME","劳务队"),
    /** 按级别查询组织架构 */
    DEPT_LEVEL("DEPT_LEVEL","SYS_DEPT","id", "DEPT_NAME","按级别查询组织架构"),
    /** 按级别查询组织架构 */
    DEPT_LEVEL_SALARY("DEPT_LEVEL","SYS_DEPT","id", "DEPT_NAME","按级别查询组织架构"),

    /** 作业公司 */
    DEPT_WORK_COMPANY("DEPT_WORK_COMPANY","SYS_DEPT","id", "DEPT_NAME","作业公司"),
    /** 内部部门 */
    DEPT_INNER_ID("DEPT_INNER_ID","SYS_DEPT","id", "DEPT_NAME","内部部门"),
    /** 作业公司 */
    DEPT_WORK_COMPANY_CODE("DEPT_WORK_COMPANY_CODE","SYS_DEPT","DEPT_CODE", "DEPT_NAME","作业公司"),

    /** 船舶 */
    SHIP("SHIP","","", "","船舶"),
    /** 航次 */
    VOYAGE("VOYAGE","T_DIS_SHIPVOYAGE_ITEM","", "","航次"),
    /** 航次 */
    SCN("SCN","T_DIS_SHIPVOYAGE","", "","航次主表"),

    /** 拖轮 */
    TUG("TUG","M_TUG","ID", "TUG_NAME","拖轮表"),

    /** 泊位 */
    BERTH("BERTH","M_BERTH","ID", "BERTH_NAME","泊位"),
    /** 泊位缆庄 */
    BERTH_BOLLARD("BERTH","M_BERTH_BOLLARD","ID", "BOLLARD_NAME","泊位缆庄"),

    PROVINCE("PROVINCE","M_CITY","PROVINCE_CODE", "NAME","省"),
    CITY("CITY","M_CITY","CITY_CODE", "NAME","市"),

    MAC_TYPE("MAC_TYPE","","", "","设备类型"),
    MAC_MODEL("MAC_MODEL","","", "","设备型号"),
    MAC("MAC","M_MACHINE","ID", "MAC_NAME","设备"),

    ALL_STORAGE_MASS("ALL_STORAGE_MASS", "", "", "", "场/区"),
    ALL_STORAGE_MASS_NEW("ALL_STORAGE_MASS", "", "", "", "场/区"),

    // 停工原因
    STOP_REASON("STOP_REASON","M_STOP_REASON","ID", "STOP_REASON_NAME","停工原因"),

    // 字典
    DICT("DICT","M_DICT_DATA","DICT_VALUE", "DICT_LABEL","字典"),

    /** 票货 */
    PORT("PORT","","", "","港口信息"),
    
    /** 常量 */
    CONSTANT("CONSTANT","M_CONSTANTS","CD", "NM","常量信息"),
    
    /** 场、区、货位 */
    STORAGE_YARD_LEVEL("STORAGE_YARD_LEVEL", "M_STORAGE_YARD","ID", "STORAGE_YARD_NM", "场、区、货位"),

    //标准体系
    STANDARD_SYSTEM("STANDARD_SYSTEM","T_STD_PROCESS_STANDARD_SYSTEM","ID", "STANDARD_SYSTEM_NAME","标准体系"),

    //工艺流程
    PROCESS("PROCESS","T_STD_TECHNOLOGICAL_PROCESS","ID", "PROCESS_NAME","工艺流程"),

    /**航名航次*/
    T_DIS_SHIPVOYAGE("T_DIS_SHIPVOYAGE","T_DIS_SHIPVOYAGE","ID", "SHIP_NAME","航名航次信息"),

    WBHTFL("WBHTFL","M_DICT_DATA","DICT_VALUE", "DICT_LABEL","外包合同类型"),

    ;

    private String code;
    private String tableName;
    private String valueName;
    private String labelName;
    private String comment;


    SelectEnum(String code, String tableName, String valueName, String labelName, String comment) {
        this.code = code;
        this.tableName = tableName;
        this.valueName = valueName;
        this.labelName = labelName;
        this.comment = comment;
    }

    public String getCode() {
        return code;
    }

    public String getTableName() {
        return tableName;
    }

    public String getComment() {
        return comment;
    }

    public String getValueName() {
        return valueName;
    }

    public String getLabelName() {
        return labelName;
    }
}
