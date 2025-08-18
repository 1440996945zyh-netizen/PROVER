package com.yy.common.enums;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;

/**
 * 大屏展示折线图数据类型信息
 * @author zcc
 */
@Getter
public enum ScreenListType {

	SCREEN_LIST_10("10", "月份"),
	SCREEN_LIST_20("20", "日期");

	ScreenListType(String code, String comment) {
        this.code = code;
        this.comment = comment;
    }

    private final String code;

    private final String comment;

    public static String getComment(String code) {
        return Arrays.stream(ScreenListType.values()).filter(val -> val.getCode().equals(code)).map(ScreenListType::getComment).findFirst().orElse(StringUtils.EMPTY);
    }
}
