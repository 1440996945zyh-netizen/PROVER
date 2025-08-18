package com.yy.ppm.auth.enums;

public enum LoginTypeEnum {


        APP("APP", "手机端"),

        PC("PC", "电脑端");

        private String code;

        private String comment;

        private LoginTypeEnum(String code, String comment) {

            this.code = code;

            this.comment = comment;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public static com.yy.ppm.auth.enums.LoginTypeEnum getByCode(String code) {
            for (com.yy.ppm.auth.enums.LoginTypeEnum item : values()) {
                if (item.getCode().equals(code)) {
                    return item;
                }
            }
            return null;
        }
}
