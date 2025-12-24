package com.yy.ppm.common.enums;

/**
 * 业务操作类型
 *
 * @author yy
 */
public enum SelectEnum {
    /** 用户 */
    USER("USER",  "sys_user","ID", "USER_NAME","用户信息"),
    CUSTOMER_ALL("CUSTOMER_ALL", "", "", "", "全部客户"),
    /** 按级别查询组织架构 */
    DEPT_LEVEL("DEPT_LEVEL","SYS_DEPT","id", "DEPT_NAME","按级别查询组织架构"),
    /** 按级别查询组织架构 */
    DEPT_LEVEL_SALARY("DEPT_LEVEL","SYS_DEPT","id", "DEPT_NAME","按级别查询组织架构"),
    PROVINCE("PROVINCE","M_CITY","PROVINCE_CODE", "NAME","省"),
    CITY("CITY","M_CITY","CITY_CODE", "NAME","市"),
    // 字典
    DICT("DICT","M_DICT_DATA","DICT_VALUE", "DICT_LABEL","字典"),
    /** 表单*/
    BPM_FORM("BPM_FORM","BPM_FORM","ID","NAME","表单信息"),
    /**流程分类*/
    BPM_CATEGORY("BPM_CATEGORY","BPM_CATEGORY","CODE","NAME","流程分类"),
    /** 常量 */
    CONSTANT("CONSTANT","M_CONSTANTS","CD", "NM","常量信息"),

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
