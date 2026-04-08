package com.yy.common.util.str;

import com.yy.framework.exception.NumberConvertException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 字符串操作辅助类
 *
 * @author
 **/
public final class StringUtil extends StringUtils {

    /**
     * 获取对象的字符串
     *
     * @param obj 需要转换的对象
     * @return string
     * @author
     **/
    public static String getString(Object obj) {
        if (obj == null) {
            return StringUtils.EMPTY;
        } else {
            return obj.toString();
        }
    }

    /**
     * 获取Int数值
     *
     * @param obj 需要转换的对象
     * @return Integer
     * @throws IllegalAccessException
     * @author
     **/
    public static Integer getInt(Object obj) {
        String val = getString(obj);
        if (NumberUtils.isCreatable(val)) {
            return Integer.parseInt(val);
        } else {
            throw new NumberConvertException("转换数值类型失败~");
        }
    }

    /**
     * 获取Int数值
     *
     * @param obj        需要转换的对象
     * @param defaultInt 当对象为null时的默认值
     * @return Integer
     * @throws IllegalAccessException
     * @author luyy
     **/
    public static Integer getInt(Object obj, Integer defaultInt) {
        if (obj == null) {
            return defaultInt;
        } else {
            String val = getString(obj);
            if (NumberUtils.isCreatable(val)) {
                return Integer.parseInt(val);
            } else {
                throw new NumberConvertException("转换数值类型失败~");
            }
        }
    }

    /**
     * 获取Long数值
     *
     * @param obj 需要转换的对象
     * @return Long
     * @throws IllegalAccessException
     * @author
     **/
    public static Long getLong(Object obj) {
        String val = getString(obj);
        if (NumberUtils.isCreatable(val)) {
            return Long.parseLong(val);
        } else {
            throw new NumberConvertException("转换数值类型失败~");
        }
    }

    /**
     * 获取Long数值
     *
     * @param obj        需要转换的对象
     * @param defaultInt 当对象为null时的默认值
     * @return Long
     * @throws IllegalAccessException
     * @author luyy
     **/
    public static Long getLong(Object obj, Long defaultInt) {
        if (obj == null) {
            return defaultInt;
        } else {
            String val = getString(obj);
            if (NumberUtils.isCreatable(val)) {
                return Long.parseLong(val);
            } else {
                throw new NumberConvertException("转换数值类型失败~");
            }
        }
    }

    /**
     * 获取Double数值
     *
     * @param obj 需要转换的对象
     * @return Long
     * @throws IllegalAccessException
     * @author
     **/
    public static Double getDouble(Object obj) {
        String val = getString(obj);
        if (NumberUtils.isCreatable(val)) {
            return Double.parseDouble(val);
        } else {
            throw new NumberConvertException("转换数值类型失败~");
        }
    }

    /**
     * 获取Double数值
     *
     * @param obj        需要转换的对象
     * @param defaultInt 当对象为null时的默认值
     * @return Long
     * @throws IllegalAccessException
     * @author luyy
     **/
    public static Double getLong(Object obj, Double defaultInt) {
        if (obj == null) {
            return defaultInt;
        } else {
            String val = getString(obj);
            if (NumberUtils.isCreatable(val)) {
                return Double.parseDouble(val);
            } else {
                throw new NumberConvertException("转换数值类型失败~");
            }
        }
    }

    /**
     * 异常栈字符串输出
     *
     * @param throwable 异常对象
     * @return String
     * @author
     **/
    public static String getErrorText(Throwable throwable) {
        if (throwable == null) {
            return "ERROR,throwable is NULL!";
        }

        try (StringWriter strWriter = new StringWriter(512); PrintWriter writer = new PrintWriter(strWriter)) {
            throwable.printStackTrace(writer);
            StringBuffer sb = strWriter.getBuffer();
            return sb.toString();
        } catch (Exception ex) {
            return "ERROR!";
        }
    }

    /**
     * 首字符大写
     *
     * @param val 字符
     * @author
     **/
    public static String firstStrUpperCase(String val) {
        char[] cs = val.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }

    /**
     * 字符串拼接
     *
     * @param stringList 字符串列表
     * @param delimiter  分隔符
     * @return String
     * @author linqi
     **/
    public static String join(List<String> stringList, String delimiter) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String string : stringList) {
            if (StringUtils.isNotBlank(string)) {
                stringBuilder.append(string).append(delimiter);
            }
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.delete(stringBuilder.length() - delimiter.length(), stringBuilder.length());
        }
        return stringBuilder.toString();
    }

    /**
     * 取出当前字符串+1操作，用于自动生成部门公司编码等代码
     *
     * @param testStr
     * @return
     */
    public static String addOne(String testStr) {
        String[] strs = testStr.split("[^0-9]");//根据不是数字的字符拆分字符串
        String numStr = strs[strs.length - 1];//取出最后一组数字
        if (numStr != null && numStr.length() > 0) {//如果最后一组没有数字(也就是不以数字结尾)，抛NumberFormatException异常
            int n = numStr.length();//取出字符串的长度
            long num = Long.parseLong(numStr) + 1;//将该数字加一
            String added = String.valueOf(num);
            n = Math.min(n, added.length());
            //拼接字符串
            return testStr.subSequence(0, testStr.length() - n) + added;
        } else {
            throw new NumberFormatException();
        }
    }

    /**
     * 字符串拼接（去重）
     *
     * @param stringList 字符串列表
     * @param delimiter  分隔符
     * @return String
     * @author zcc
     **/
    public static String deduplicationJoin(List<String> stringList, String delimiter) {
        Set<String> stringSet = new LinkedHashSet<>(stringList);
        stringList = new LinkedList<>(stringSet);
        return join(stringList, delimiter);
    }

    /**
     * 字符串拼接
     *
     * @param stringList 字符串列表
     * @param delimiter  分隔符
     * @return String
     * @author zcc
     **/
    public static String joinLong(List<Long> stringList, String delimiter) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Long string : stringList) {
            if (string != null) {
                stringBuilder.append(string).append(delimiter);
            }
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.delete(stringBuilder.length() - delimiter.length(), stringBuilder.length());
        }
        return stringBuilder.toString();
    }


    public static List<Long> strArrayConvertToLongList(String ids) {
        if (ids == null || ids.length() == 0) return new ArrayList<Long>();

        String[] idArray = StringUtils.split(ids, ",");
        return Arrays.stream(idArray)
                .map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
    }

    public static Boolean checkNumber(String num, Integer scaleMin, Integer scaleMax) {
        if (num == null || num.length() == 0) {
            return false;
        }

        String format = "^(\\d+)$";

        //整数或至少X位
        if (scaleMin != null && scaleMin > 0 && scaleMax == null) {
            format = "^(\\d+)(\\.(\\d){" + scaleMin + ",})?$";
        }
        //整数或至多X位
        else if (scaleMin == null && scaleMax != null && scaleMax > 0) {
            format = "^(\\d+)(\\.(\\d){0," + scaleMax + "})?$";
        }
        //整数或X~Y位
        else if (scaleMin != null && scaleMin > 0 && scaleMax != null && scaleMax > 0) {
            format = "^(\\d+)(\\.(\\d){" + scaleMin + "," + scaleMax + "})?$";
        }
        Pattern emailPattern = Pattern.compile(format);

        if (!emailPattern.matcher(num).matches()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 空判断
     *
     * @param obj
     * @return
     */
    public static Boolean isEmpty(Object obj) {
        if (obj == null || getString(obj).length() == 0) {
            return true;
        }
        return false;
    }
    /**
     * * 判断一个Collection是否非空，包含List，Set，Queue
     *
     * @param coll 要判断的Collection
     * @return true：非空 false：空
     */
    public static boolean isNotEmpty(Collection<?> coll)
    {
        return !isEmpty(coll);
    }
    public static boolean isEmpty(Collection<?> coll)
    {
        return isNull(coll) || coll.isEmpty();
    }


    /**
     * * 判断一个对象是否为空
     *
     * @param object Object
     * @return true：为空 false：非空
     */
    public static boolean isNull(Object object)
    {
        return object == null;
    }

    /**
     * * 判断一个对象是否非空
     *
     * @param object Object
     * @return true：非空 false：空
     */
    public static boolean isNotNull(Object object)
    {
        return !isNull(object);
    }

    /**
     * 随机数字符串生成
     *
     * @param start
     * @param end
     * @return
     */
    public static String getRandomString(int start, int end) {
        SecureRandom random = new SecureRandom();
        int value = random.nextInt(end - start + 1) + start;
        return String.valueOf(value);
    }

    /**
     * 格式化文本, {} 表示占位符<br>
     * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
     * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
     * 例：<br>
     * 通常使用：format("this is {} for {}", "a", "b") -> this is a for b<br>
     * 转义{}： format("this is \\{} for {}", "a", "b") -> this is \{} for a<br>
     * 转义\： format("this is \\\\{} for {}", "a", "b") -> this is \a for b<br>
     *
     * @param template 文本模板，被替换的部分用 {} 表示
     * @param params 参数值
     * @return 格式化后的文本
     */
    public static String format(String template, Object... params)
    {
        if (isEmpty(params) || isEmpty(template))
        {
            return template;
        }
        return StrFormatter.format(template, params);
    }


    /**
     * 判断给定的collection列表中是否包含数组array 判断给定的数组array中是否包含给定的元素value
     *
     * @param collection 给定的集合
     * @param array 给定的数组
     * @return boolean 结果
     */
    public static boolean containsAny(Collection<String> collection, String... array)
    {
        if (isEmpty(collection) || isEmpty(array))
        {
            return false;
        }
        else
        {
            for (String str : array)
            {
                if (collection.contains(str))
                {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * * 判断一个对象数组是否为空
     *
     * @param objects 要判断的对象数组
     ** @return true：为空 false：非空
     */
    public static boolean isEmpty(Object[] objects)
    {
        return isNull(objects) || (objects.length == 0);
    }
}
