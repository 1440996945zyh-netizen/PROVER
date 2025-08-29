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

        DEPT("DEPT", "SYS_DEPT","DEPT_NO", "", "", 4, "动态传值", "部门");

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
