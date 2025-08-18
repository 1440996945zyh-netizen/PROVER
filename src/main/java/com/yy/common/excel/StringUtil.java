package com.yy.common.excel;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.hutool.core.util.ReUtil.RE_KEYS;

/**
 * @Author linqi
 * @Description
 * @Date 2023-05-27 21:08
 */
public class StringUtil {

    /**
     * 查询占位符
     */
    private static final String QUERY_PLACEHOLDER = "{}";

    /**
     * 将正则regex在字符串str中第n次匹配的内容替换为newSubStr
     *
     * @param str
     * @param regex
     * @param n
     * @return
     */
    public static String replaceNthMatch(String str, String regex, int n, String newSubStr) {
        if (n <= 0) {
            throw new IllegalArgumentException("n必须为正整数");
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        int count = 0;
        while (matcher.find()) {
            count++;
            if (count == n) {
                return str.substring(0, matcher.start()) + newSubStr + str.substring(matcher.end());
            }
        }
        return str;
    }

    /**
     * 反转义字符，将正则的关键字反转义
     *
     * @param content
     * @return
     */
    public static String unescape(CharSequence content) {
        if (StrUtil.isBlank(content)) {
            return StrUtil.str(content);
        }

        final StringBuilder builder = new StringBuilder();
        int len = content.length();
        char current;
        for (int i = 0; i < len; i++) {
            current = content.charAt(i);
            if (current == '\\' && i < len - 1 && RE_KEYS.contains(content.charAt(i + 1))) {
                builder.append(content.charAt(++i));
            } else {
                builder.append(current);
            }
        }
        return builder.toString();
    }

    /**
     * 使用占位符查询子串
     *
     * @param str
     * @param template
     * @return
     */
    public static List<String> parseValues(String str, String template) {
        template = ReUtil.escape(template);
        template = template.replace(ReUtil.escape(QUERY_PLACEHOLDER), "(.*)");
        List<String> result = new ArrayList<>();
        for (int i = 1; ; i++) {
            String val;
            try {
                val = ReUtil.get(template, str, i);
            } catch (IndexOutOfBoundsException e) {
                break;
            }
            result.add(val);
        }
        return result;
    }
}
