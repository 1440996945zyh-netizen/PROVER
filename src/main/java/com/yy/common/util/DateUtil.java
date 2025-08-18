package com.yy.common.util;

import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-12-15 10:58
 */
public class DateUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date localDate2Date(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDateTime date2LocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDate date2LocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static String localDateTime2Str(LocalDateTime localDateTime) {
        return localDateTime.format(DATETIME_FORMATTER);
    }

    public static String localDate2Str(LocalDate localDate) {
        return localDate.format(DATE_FORMATTER);
    }

    public static LocalDateTime str2LocalDateTime(String str) {
        return LocalDateTime.parse(str, DATETIME_FORMATTER);
    }

    public static LocalDate str2LocalDate(String str) {
        return LocalDate.parse(str, DATE_FORMATTER);
    }

    public static LocalDateTime max(LocalDateTime... localDateTimes) {
        return Arrays.stream(localDateTimes).max(Comparator.comparing(v1 -> v1)).orElse(null);
    }

    public static LocalDate max(LocalDate... localDates) {
        return Arrays.stream(localDates).max(Comparator.comparing(v1 -> v1)).orElse(null);
    }

    public static LocalDateTime min(LocalDateTime... localDateTimes) {
        return Arrays.stream(localDateTimes).min(Comparator.comparing(v1 -> v1)).orElse(null);
    }

    public static LocalDate min(LocalDate... localDates) {
        return Arrays.stream(localDates).min(Comparator.comparing(v1 -> v1)).orElse(null);
    }

    public static long getDateDifference(LocalDate beginDate, LocalDate endDate, ChronoUnit unit) {
        return unit.between(beginDate, endDate);
    }

    public static long getDateDifference(LocalDate beginDate, LocalDate endDate) {
        return getDateDifference(beginDate, endDate, ChronoUnit.DAYS);
    }

    /**
     * 检查日期数组是否连续
     *
     * @param dates
     * @return
     */
    public static boolean isContinuous(List<LocalDate> dates) {
        if (CollectionUtils.isEmpty(dates) || dates.size() == 1) {
            return true;
        }

        LocalDate prev = dates.get(0);
        for (int i = 1; i < dates.size(); i++) {
            LocalDate curr = dates.get(i);
            if (!curr.equals(prev.plusDays(1))) {
                return false;
            }
            prev = curr;
        }

        return true;
    }

    /**
     * 查找两个日期数组的重叠部分
     *
     * @param range0
     * @param range1
     * @return
     */
    public static List<LocalDate> getOverlap(List<LocalDate> range0, List<LocalDate> range1) {
        if (CollectionUtils.isEmpty(range0) || CollectionUtils.isEmpty(range1)) return Collections.emptyList();

        if (!isContinuous(range0) || !isContinuous(range1)) throw new IllegalArgumentException("错误的参数：日期数组不连续");

        if (range0.get(range0.size() - 1).isBefore(range1.get(0)) || range1.get(range1.size() - 1).isBefore(range0.get(0))) {
            return Collections.emptyList();
        }

        return range0.stream()
                .flatMap(v1 -> range1.stream().filter(v1::equals))
                .collect(Collectors.toList());
    }
}
