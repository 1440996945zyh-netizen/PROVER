package com.yy.common.excel.export.enums;

import cn.hutool.core.util.ReUtil;
import lombok.Getter;

/**
 * @Author linqi
 * @Description
 * @Date 2023-05-27 18:28
 */
@Getter
public enum Keyword {

    PROPERTY_START(".", "取属性起始符"),

    PROPERTY_END("", "取属性结束符"),

    ELEMENT_START("[", "取元素起始符"),

    ELEMENT_END("]", "取元素结束符"),

    INDEX_WILDCARD("*", "索引通配符"),

    PLACEHOLDER_START("{", "占位符起始符"),

    PLACEHOLDER_END("}", "占位符结束符");

    private final String code;

    private final String comment;

    Keyword(String code, String comment) {
        this.code = code;
        this.comment = comment;
    }

    public String getCodeAfterEscapeRegexCharacters() {
        return ReUtil.escape(code);
    }
}
