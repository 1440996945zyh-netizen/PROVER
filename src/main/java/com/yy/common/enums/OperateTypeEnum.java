package com.yy.common.enums;

/**
 * 业务操作类型
 *
 * @author ruoyi
 */
public enum OperateTypeEnum {
    QUERY("QUERY", "查询"),

    OTHER("OTHER", "其他操作"),

    INSERT("INSERT", "新增"),

    UPDATE("UPDATE", "修改"),

    EXPORT("EXPORT", "导出"),

    DELETE("DELETE", "删除");

    private String code;

    private String commont;

    private OperateTypeEnum(String code, String commont) {
        this.code = code;
        this.commont = commont;
    }

    public String getComment() {
        return commont;
    }

    public String getCode() {
        return code;
    }

    public void setValue(String value) {
        this.commont = value;
    }

    public static String getByValue(String code) {
        for (OperateTypeEnum item : values()) {
            if (item.getCode().equals(code)) {
                return item.getComment();
            }
        }
        return "";
    }
}
