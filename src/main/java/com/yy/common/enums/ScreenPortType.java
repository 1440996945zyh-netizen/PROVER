package com.yy.common.enums;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;

/**
 * 大屏展示港口信息
 * @author zcc
 */
@Getter
public enum ScreenPortType {

	SCREEN_PORT_10("10", "潍坊港"),
	SCREEN_PORT_20("20", "寿光港"),
	SCREEN_PORT_30("30", "东营港"),
	SCREEN_PORT_40("40", "滨州港");

	ScreenPortType(String code, String comment) {
        this.code = code;
        this.comment = comment;
    }

    private final String code;

    private final String comment;

    public static String getComment(String code) {
        return Arrays.stream(ScreenPortType.values()).filter(val -> val.getCode().equals(code)).map(ScreenPortType::getComment).findFirst().orElse(StringUtils.EMPTY);
    }
}
