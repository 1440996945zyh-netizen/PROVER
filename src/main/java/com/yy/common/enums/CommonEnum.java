package com.yy.common.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * 全部枚举变量
 */
public interface CommonEnum {

    /**
     * 在用，停用
     */
    @Getter
    public static enum IsUsed  {
        // 在用
        USED("1"),
        // 停用
        UNUSED("0");

        private String code;

        private IsUsed(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
    /**
     * 是否
     */
    public static enum YesNoMode {

        YES("1"),

        NO("0");

        private String code;

        private YesNoMode(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public static YesNoMode getByCode(String code) {
            for (YesNoMode item : values()) {
                if (item.getCode().equals(code)) {
                    return item;
                }
            }
            return null;
        }

        public static YesNoMode fromBoolean(boolean bool) {
            return bool ? YES : NO;
        }

        public static boolean isContains(String code) {
            return Arrays.stream(values()).anyMatch(v1 -> v1.getCode().equals(code));
        }
    }

    /**
     * 真假
     */
    public static enum EnableMode {

        TRUE("TRUE"),

        FALSE("FALSE");

        private String code;

        private EnableMode(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public static EnableMode getByCode(String code) {
            for (EnableMode item : values()) {
                if (item.getCode().equals(code)) {
                    return item;
                }
            }
            return null;
        }

    }

    /**
     * 日期格式
     */
    public static enum DateFormatType {

        E_1("yyyy-MM-dd"),
        E_11("yyyyMMdd"),

        E_2("yyyy-MM"),
        E_21("yyyyMM"),

        E_3("MM-dd"),

        E_4("yyyy"),

        E_5("MM"),

        E_6("yyyy-MM-dd HH:mm:ss"),

        E_7("yyyy-MM-dd HH:mm"),
        E_71("yyyyMMddHHmm"),

        E_8("HH:mm:ss"),

        E_9("HH:mm"),
        E_91("HHmm"),
        E_92("yyMMdd"),
        E_93("yyMM"),
        E_94("yy"),
        E_95("yyyyMMdd HH:mm:ss");


        private String code;

        private DateFormatType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public static String getByCode(String code) {
            for (DateFormatType item : values()) {
                if (item.getCode().toLowerCase().equals(code.toLowerCase())) {
                    return item.getCode();
                }
            }
            return "";
        }
    }


    @Getter
    public enum MillisUnit {

        /**
         * 一小时的毫秒值
         */
        HOUR(3_600_000),

        /**
         * 一天的毫秒值
         */
        DAY(86_400_000);

        private final long millis;

        MillisUnit(int millis) {
            this.millis = millis;
        }

        public long getMillis() {
            return millis;
        }
    }


}
