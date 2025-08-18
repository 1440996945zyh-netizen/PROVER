package com.yy.ppm.system.enums;

import lombok.Getter;

/**
 * 系统管理类枚举
 */
public interface SysEnum {

    /**
     * 菜单类型
     */
    public static enum MenuType {

        MENU("1", "带URL菜单"),

        CATALOG("2", "目录"),

        BUTTON("3", "按钮");

        private String code;

        private String value;

        private MenuType(String code, String value) {
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public String getCode() {
            return code;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public static String getByValue(String code) {
            for (MenuType item : values()) {
                if (item.getCode() == code) {
                    return item.getValue();
                }
            }
            return "";
        }
    }

    /**
     * MinIO桶名称
     */
    @Getter
    public static enum SysParamEnum {

        /** 桶名称 */
        FILE_BUCKET("FILE_BUCKET", "ppm", "MIMIO桶名称");

        private String code;
        // 默认值
        private String defValue;
        private String comment;

        private SysParamEnum(String code, String defValue, String comment) {
            this.code = code;
            this.comment = comment;
            this.defValue = defValue;
        }

        public static String getByComment(String code) {
            for (SysParamEnum item : values()) {
                if (item.getCode().equals(code)) {
                    return item.getComment();
                }
            }
            return null;
        }
    }


}
