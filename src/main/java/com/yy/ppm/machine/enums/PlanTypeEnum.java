package com.yy.ppm.machine.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * 计划类型
 * @author zcc
 */
@Getter
public enum PlanTypeEnum {

	PLAN_TYPE_1("1", "船舶计划"),
	PLAN_TYPE_2("2", "集疏港计划"),
	PLAN_TYPE_3("3", "转运计划"),
	PLAN_TYPE_4("4", "零工计划");

	PlanTypeEnum(String code, String comment) {
        this.code = code;
        this.comment = comment;
    }

    private final String code;

    private final String comment;

    public static String getComment(String code) {
        return Arrays.stream(PlanTypeEnum.values()).filter(val -> val.getCode().equals(code)).map(PlanTypeEnum::getComment).findFirst().orElse(StringUtils.EMPTY);
    }

    public static boolean isContains(String code) {
        return Arrays.stream(PlanTypeEnum.values()).anyMatch(v1 -> v1.getCode().equals(code));
    }
}
