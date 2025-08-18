package com.yy.common.excel.export.enums;

import lombok.Getter;

/**
 * @Author linqi
 * @Description
 * @Date 2023-05-27 20:33
 */
@Getter
public enum Regex {

    UNSIGNED_INTEGER_STRICT("(0|[1-9]\\d*)", "匹配任意非负整数，0开头时仅匹配”0“"),

    NOT_INTEGER("\\D", "匹配任意非整数"),

    PLACEHOLDER("(?<!\\\\)\\{([^{}]*)(?<!\\\\)\\}", "匹配占位符"),

    VERTICAL_ARRAY("[vV](;([0-9]*)?)?", "匹配垂直数组标记符"),

    HORIZONTAL_ARRAY("[hH](;([0-9]*)?)?", "匹配水平数组标记符"),

    VERTICAL_HORIZONTAL_ARRAY("[vVhH](;([0-9]*)?)?", "匹配数组标记符");

    private final String code;

    private final String comment;

    Regex(String code, String comment) {
        this.code = code;
        this.comment = comment;
    }
}
