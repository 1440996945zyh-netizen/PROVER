package com.yy.ppm.common.enums;

import lombok.Getter;

public interface MySpecialInfoEnum {

    /**
     * 类型
     */
    @Getter
    public static enum myProjectEnum {

        PROJECT("NOTICE", "项目"),
        PAGE_NUM("PAGE_COUNT","页数");


        private String code;
        private String comment;

        private myProjectEnum(String code, String comment) {
            this.code = code;
            this.comment = comment;
        }

    }

    /**
     * 个人代办
     */
    @Getter
    public static enum scheduleSelfStatus {

        TODO("0", "待办"),
        MANAGED("10","已完成");

        private String code;
        private String comment;

        private scheduleSelfStatus(String code, String comment) {
            this.code = code;
            this.comment = comment;
        }

    }
}
