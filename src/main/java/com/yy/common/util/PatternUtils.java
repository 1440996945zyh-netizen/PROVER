package com.yy.common.util;

import java.util.regex.Pattern;

/**
 * 正则表达式工具类,预编译了一批可复用表达式
 *
 * @author
 **/
public final class PatternUtils {

    /**
     * 正则表达式,支持整数/小数(精度到后三位)
     **/
    public static final Pattern NUMERIC_REGEX = Pattern.compile("^([1-9]\\d*(\\.\\d{1,3})?|0\\.\\d{1,3}|0)$");
}
