package com.yy.common.util;

import com.yy.common.page.Pages;

import java.util.List;

/**
 * 通用分页转换工具类
 * 用于将List转换为Pages格式
 */
public class PageConverterUtils {

    /**
     * 将List转换为Pages格式（最简单方式）
     *
     * @param <T>        数据类型
     * @param list       当前页数据列表
     * @param pageNum    当前页码
     * @param pageSize   每页大小
     * @param totalNum   总记录数
     * @return 分页对象
     */
    public static <T> Pages<T> convert(List<T> list, int pageNum, int pageSize, long totalNum) {
        Pages<T> pages = new Pages<>();
        pages.setPages(list);
        pages.setPageNum(pageNum);
        pages.setPageSize(pageSize);
        pages.setTotalNum(totalNum);
        pages.setTotalPageNum(calculateTotalPages(totalNum, pageSize));
        return pages;
    }

    /**
     * 将List转换为Pages格式（带额外数据）
     *
     * @param <T>        数据类型
     * @param list       当前页数据列表
     * @param pageNum    当前页码
     * @param pageSize   每页大小
     * @param totalNum   总记录数
     * @param extraData  额外数据（JSON格式）
     * @return 分页对象
     */
    public static <T> Pages<T> convert(List<T> list, int pageNum, int pageSize, long totalNum, String extraData) {
        Pages<T> pages = convert(list, pageNum, pageSize, totalNum);
        pages.setExtraData(extraData);
        return pages;
    }

    /**
     * 计算总页数
     *
     * @param totalNum  总记录数
     * @param pageSize  每页大小
     * @return 总页数
     */
    private static int calculateTotalPages(long totalNum, int pageSize) {
        if (pageSize <= 0) return 0;
        return (int) Math.ceil((double) totalNum / pageSize);
    }
}
