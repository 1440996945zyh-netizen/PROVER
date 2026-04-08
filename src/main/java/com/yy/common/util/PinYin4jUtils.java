package com.yy.common.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 拼音转换工具类
 *
 * @author
 **/
public final class PinYin4jUtils {
    private static final Logger log = LoggerFactory.getLogger(PinYin4jUtils.class);
    public static final String SPLIT_SYMBOL = " / ";

    private static final Pattern PATTERN_CN_ZH = Pattern.compile("[\\u4E00-\\u9FA5]+");

    private static final Pattern PATTERN_NUMBER = Pattern.compile("\\d");

    /**
     * 中文转拼音数组
     *
     * @param hyVal 中文
     * @return 拼音数值数组
     * @author
     **/
    public static String[] convertCnzhToPinYinArray(String hyVal) {
        if (StringUtils.isBlank(hyVal)) {
            return Collections.emptyList().toArray(new String[]{});
        }

        List<String> dataList = new ArrayList<>(16);
        char[] charArray = hyVal.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            String tempVal = Character.toString(charArray[i]);
            if (PATTERN_CN_ZH.matcher(tempVal).matches()) {
                String val = PinyinHelper.toHanyuPinyinStringArray(charArray[i])[0];
                val = PATTERN_NUMBER.matcher(val).replaceAll(StringUtils.EMPTY);
                dataList.add(val);
            } else {
                dataList.add(tempVal);
            }
        }
        return dataList.toArray(new String[]{});
    }

    /**
     * 中文转拼音首字符大写
     *
     * @param zh 中文
     * @return 拼音数值
     * @author
     **/
    public static String convertCnzhToPinYinVal(String zh) {
        String[] valArray = convertCnzhToPinYinArray(zh);
        return Stream.of(valArray).map(val -> {
            return Character.toString(val.charAt(0));
        }).collect(Collectors.joining());
    }

    /**
     * 包装字符
     *
     * @param zh 中文数据, splitSymbol 分隔符
     * @return 包装后数据
     **/
    public static String wrap(String zh, String splitSymbol) {
        String pyVal = convertCnzhToPinYinVal(zh);
        if (StringUtils.isNotBlank(pyVal)) {
            StringBuilder sb = new StringBuilder(32);
            sb.append(zh + splitSymbol + pyVal.toUpperCase());
            return sb.toString();
        } else {
            return zh;
        }
    }
    /**
     * 将汉字转换为全拼 (字母==>zimu)
     */
    public static String getPingYin(String src) {

        char[] t1 = null;
        t1 = src.toCharArray();
        String[] t2 = new String[t1.length];
        HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();

        t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        t3.setVCharType(HanyuPinyinVCharType.WITH_V);
        String t4 = "";
        int t0 = t1.length;
        try {
            for (int i = 0; i < t0; i++) {
                // 判断是否为汉字字符
                if (Character.toString(t1[i]).matches(
                        "[\\u4E00-\\u9FA5]+")) {
                    t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
                    t4 += t2[0];
                } else {
                    t4 += Character.toString(t1[i]);
                }
            }
            // System.out.println(t4);
            return t4;
        } catch (BadHanyuPinyinOutputFormatCombination e1) {
            log.warn("BadHanyuPinyinOutputFormatCombination",e1);
        }
        return t4;
    }

    /**
     * 返回中文的首字母 (字母==>zm)
     */
    public static String getPinYinHeadChar(String str, int length) {

        if (str == null || "".equals(str)) {
            return "";
        }

        String convert = "";
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert += pinyinArray[0].charAt(0);
            } else {
                convert += word;
            }
        }

        if ("".equals(convert)) {
            return "";
        }

        if (convert.length() > length) {
            return convert.substring(0, length);
        }

        return convert.toUpperCase();
    }

    /**
     * 返回中文的首字母 (字母==>z)
     */
    public static String getPinYinHeadCharFirst(String str) {

        String convert = "";

        if (StringUtils.isEmpty(str)) {
            return "";
        } else {
            char word = str.charAt(0);
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert += pinyinArray[0].charAt(0);
            } else {
                convert += word;
            }
        }

        return convert;
    }
}
