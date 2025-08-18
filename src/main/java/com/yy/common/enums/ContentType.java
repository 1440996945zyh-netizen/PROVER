package com.yy.common.enums;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;

/**
 * 请求类型
 * @author Administrator
 *
 */
@Getter
public enum ContentType {

	CONTENT_TYPE_1(1, "application/x-www-form-urlencoded"),
	CONTENT_TYPE_2(2, "application/json"),
	CONTENT_TYPE_3(3, "application/xml");

	ContentType(Integer code, String comment) {
        this.code = code;
        this.comment = comment;
    }

    private final Integer code;

    private final String comment;

    public static String getComment(Integer code) {
        return Arrays.stream(ContentType.values()).filter(val -> val.getCode().equals(code)).map(ContentType::getComment).findFirst().orElse(StringUtils.EMPTY);
    }
}
