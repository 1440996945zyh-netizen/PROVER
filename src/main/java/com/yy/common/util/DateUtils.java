package com.yy.common.util;

import com.yy.common.enums.CommonEnum;
import com.yy.common.util.str.StringUtil;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

/**
 * 日期工具类 基于Joda-time
 *
 * @author
 **/
public final class DateUtils {

    /**
     * 日期格式校验
     *
     * @param strDate   日期字符串
     * @param strFormat 格式化字符
     * @return true-验证通过, false-验证失败
     * @author gewx
     **/
    public static boolean validDate(String strDate, String strFormat) {
        try {
            DateTimeFormatter format = DateTimeFormat.forPattern(strFormat);
            format.parseDateTime(strDate);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    /**
     * 日期解析
     *
     * @param strDate   日期字符串
     * @param strFormat 格式化字符
     * @return 日期对象
     * @author gewx
     **/
    public static Date parseDate(String strDate, String strFormat) {
        DateTimeFormatter format = DateTimeFormat.forPattern(strFormat);
        return format.parseDateTime(strDate).toDate();
    }

    /**
     * 日期格式化
     *
     * @param date    要格式化的日期字段
     * @param hour    时
     * @param minutes 分
     * @param seconds 秒
     * @return 格式化后的日期
     */
    public static Date formatDate(Date date, int hour, int minutes, int seconds) {
        return new DateTime(date.getTime()).withHourOfDay(hour).withMinuteOfHour(minutes).withSecondOfMinute(seconds)
                .toDate();
    }

    /**
     * Date转String
     * @param date
     * @param formater
     * @return
     */
    public static String formatDate(Date date, String formater) {

        if (date == null) {
            return "";
        }

        if (StringUtil.isEmpty(formater)) {
            return new SimpleDateFormat(CommonEnum.DateFormatType.E_1.getCode()).format(date);
        } else{
            return new SimpleDateFormat(formater).format(date);
        }
    }

    /**
     * 计算日期差,毫秒
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 日期差值
     * @author gewx
     **/
    public static long timeDiffMillis(DateTime start, DateTime end) {
        return end.getMillis() - start.getMillis();
    }

    /**
     * 计算日期差,秒
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 日期差值
     * @author gewx
     **/
    public static int timeDiffSeconds(DateTime start, DateTime end) {
        return Seconds.secondsBetween(start, end).getSeconds();
    }

    /**
     * 计算日期差,分钟
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 日期差值
     * @author gewx
     **/
    public static int timeDiffMinutes(DateTime start, DateTime end) {
        return Minutes.minutesBetween(start, end).getMinutes();
    }

    /**
     * 计算日期差,小时
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 日期差值
     * @author gewx
     **/
    public static int timeDiffHours(DateTime start, DateTime end) {
        return Hours.hoursBetween(start, end).getHours();
    }

    /**
     * 计算日期差,天
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 日期差值
     * @author gewx
     **/
    public static int timeDiffDays(DateTime start, DateTime end) {
        return Days.daysBetween(start, end).getDays();
    }

    /**
     * 计算日期差,月
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 日期差值
     * @author gewx
     **/
    public static int timeDiffMonths(DateTime start, DateTime end) {
        return Months.monthsBetween(start, end).getMonths();
    }

    /**
     * 计算日期差,年
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 日期差值
     * @author gewx
     **/
    public static int timeDiffYears(DateTime start, DateTime end) {
        return Years.yearsBetween(start, end).getYears();
    }

    /**
     * 计算日期差,年/月/日/时/分/秒
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 日期差值Period对象
     * @author gewx
     **/
    public static Period timeDiffPeriod(DateTime start, DateTime end) {
        Interval time = new Interval(start, end);
        return time.toPeriod();
    }

    /**
     * 日期格式化
     *
     * @param date   日期
     * @param format 格式化函数
     * @return 格式化字符串
     * @author gewx
     **/
    public static String format(Date date, Supplier<DateTimeFormatter> format) {
        if (date == null) {
            return null;
        } else {
            return new DateTime(date).toString(format.get());
        }
    }

    /**
     * 日期格式化
     *
     * @param date   日期
     * @param format 格式化函数
     * @return 格式化字符串
     * @author gewx
     **/
    public static String format(DateTime date, Supplier<DateTimeFormatter> format) {
        if (date == null) {
            return null;
        } else {
            return date.toString(format.get());
        }
    }

    /**
     * 计算日期差：天 四舍五入
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return
     * @author linqi
     **/
    public static int roundDays(DateTime start, DateTime end) {
        return round(start, end, CommonEnum.MillisUnit.DAY);
    }

    /**
     * 计算日期差 向上取整
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return
     * @author pengqh
     **/
    public static int roundUpDay(DateTime start, DateTime end) {
        long millis = timeDiffMillis(start, end);
        BigDecimal bb = new BigDecimal(millis).multiply(new BigDecimal(1.0)).divide(new BigDecimal(CommonEnum.MillisUnit.DAY.getMillis()), BigDecimal.ROUND_UP);
        return bb.intValue();
    }

    /**
     * 计算日期差 四舍五入
     *
     * @param start          开始时间
     * @param end            结束时间
     * @param millisUnitEnum 毫秒值单位枚举
     * @return
     * @author linqi
     **/
    public static int round(DateTime start, DateTime end, CommonEnum.MillisUnit millisUnitEnum) {
        long millis = timeDiffMillis(start, end);
        return round(millis, millisUnitEnum);
    }

    /**
     * 计算日期差 四舍五入
     *
     * @param millis         毫秒值
     * @param millisUnitEnum 毫秒值单位枚举
     * @return
     * @author linqi
     **/
    public static int round(long millis, CommonEnum.MillisUnit millisUnitEnum) {
        long round = Math.round(millis * 1.0 / (millisUnitEnum.getMillis()));
        return Long.valueOf(round).intValue();
    }

    /**
     * 日期范围比较
     * @param startDate
     * @param endDate
     * @param list
     * @param <T>
     * @return
     */
    public static <T> boolean isInclude(Date startDate, Date endDate, List<T> list)  {

        try {
            Long startTime= startDate.getTime();
            Long endTime= endDate.getTime();
            for (T temp : list) {
                Date startDateTemp = (Date) temp.getClass().getMethod("getStartDate", null).invoke(temp);
                Long startTimeTemp = startDateTemp.getTime();
                Date endDateTemp = (Date) temp.getClass().getMethod("getEndDate", null).invoke(temp);
                Long endTimeTemp = endDateTemp.getTime();
                if((startTime<startTimeTemp && endTime>startTimeTemp) || (startTime<endTimeTemp && startTime>startTimeTemp)){
                    return true;
                }
            }
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    /****
     * 传入具体日期 ，返回具体日期减少一天
     */
    public static String addDay(String date, int days)  {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dt = sdf.parse(date);
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(dt);
            rightNow.add(Calendar.DAY_OF_MONTH, days);
            Date dt1 = rightNow.getTime();
            String reStr = sdf.format(dt1);
            return reStr;
        } catch(ParseException e) {
            return "";
        }
    }
    /****
     * 传入日期 ，返回增减后的日期
     */
    public static Date addDays(Date date, int days)  {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.add(Calendar.DAY_OF_MONTH, days);
        return rightNow.getTime();
    }

    /**
     * 设置小时
     * @param date
     * @param hour
     * @return
     */
    public static Date setHour(Date date, int hour) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.set(Calendar.HOUR_OF_DAY, hour);
        return instance.getTime();
    }
    /**
     * 设置分钟
     * @param date
     * @param minute
     * @return
     */
    public static Date setMinute(Date date, int minute) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.set(Calendar.MINUTE, minute);
        return instance.getTime();
    }
    /**
     * 设置秒
     * @param date
     * @param second
     * @return
     */
    public static Date setSecond(Date date, int second) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.set(Calendar.SECOND, second);
        return instance.getTime();
    }

    /**
     * 合并日期时间（date + time）
     * @param date
     * @param time
     * @return
     */
    public static Date mergeDateTime(Date date, Date time) {
        Calendar dateInstance = Calendar.getInstance();
        Calendar timeInstance = Calendar.getInstance();
        dateInstance.setTime(date);
        timeInstance.setTime(time);
        dateInstance.set(Calendar.HOUR_OF_DAY, timeInstance.get(Calendar.HOUR_OF_DAY));
        dateInstance.set(Calendar.MINUTE, timeInstance.get(Calendar.MINUTE));
        dateInstance.set(Calendar.SECOND, timeInstance.get(Calendar.SECOND));
        return dateInstance.getTime();
    }

    public static java.time.LocalDateTime dateToLocalDateTime(Date date) {
        java.time.Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        java.time.LocalDateTime localDateTime = java.time.LocalDateTime.ofInstant(instant, zone);
        return java.time.LocalDateTime.ofInstant(instant, zone);
    }

    public static void main(String args[]) {
       String a=  addDay("2021-07-12", 2);
       System.out.println(a);
    }
}
