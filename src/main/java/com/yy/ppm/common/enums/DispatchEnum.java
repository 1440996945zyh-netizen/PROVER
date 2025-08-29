package com.yy.ppm.common.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * 一次配工类型
 * @Author yy
 * @Description
 * @Date 2023-07-05 11:40
 */
public interface DispatchEnum {

    @Getter
    public static enum DispatchTypeEnum {

        EQUIP("1", "机械"),
        LABOR("2", "劳务");

        private final String code;

        private final String name;

        DispatchTypeEnum(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public static boolean isContains(String code) {
            return Arrays.stream(DispatchTypeEnum.values()).anyMatch(v1 -> v1.getCode().equals(code));
        }
    }

    @Getter
    public static enum WorkPlanStatusEnum {

        TODO_REVIEW("10", "未审核"),
        REVIEW("20", "已审核"),
        WORK("30", "作业中"),
        STOP("40", "停工"),
        COMPLETE("50", "完工");

        private final String code;

        private final String name;

        WorkPlanStatusEnum(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public static boolean isContains(String code) {
            return Arrays.stream(WorkPlanStatusEnum.values()).anyMatch(v1 -> v1.getCode().equals(code));
        }
    }

    @Getter
    public static enum WorkPlanTypeEnum {

        SHIP("1", "船舶计划"),
        TRANSPORT("2", "集疏港计划"),
        RESHIPMENT("3", "转运计划"),
        SUNDRY("4", "杂项计划"),
        LING_GONG("5", "零工计划");

        private String code;

        private String name;

        WorkPlanTypeEnum(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public static boolean isContains(String code) {
            return Arrays.stream(WorkPlanTypeEnum.values()).anyMatch(v1 -> v1.getCode().equals(code));
        }
    }
}
