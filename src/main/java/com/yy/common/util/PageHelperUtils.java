package com.yy.common.util;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.framework.exception.BusinessRuntimeException;
import org.apache.commons.collections4.CollectionUtils;

import java.util.function.Supplier;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * 分页工具类,基于PageHelper
 *
 * @author
 **/
public final class PageHelperUtils {

    private static int ZERO = 0;

    /**
     * 通用分页方法
     *
     * @param parameter  分页参数
     * @param pageResult 分页数据集
     * @return void
     * @author
     **/
    public static <T> Pages<T> limit(PageParameter parameter, Supplier<Page<T>> pageResult) {
        if (parameter.getStartPage() == null) {
            throw new BusinessRuntimeException("页码必填");
        }

        if (parameter.getPageSize() == null) {
            throw new BusinessRuntimeException("页行必填");
        }

        if (parameter.getPageSize().intValue() == ZERO) {
            throw new BusinessRuntimeException("页行必须大于0");
        }

        PageHelper.startPage(parameter.getStartPage(), parameter.getPageSize());
        if (isNotBlank(parameter.getSortName())) {
            PageHelper.orderBy(parameter.getSortName());
            if (parameter.isSymbol()) {
                PageHelper.orderBy(parameter.getSortName() + " DESC");
            }
        }

        Page<T> page = pageResult.get();
        Pages<T> pages = new Pages<T>();
        pages.setTotalPageNum(page.getPages());
        pages.setTotalNum(page.getTotal());
        pages.setPageNum(page.getPageNum());
        pages.setPageSize(page.getPageSize());
        pages.setPages(page.getResult());
        return pages;
    }

    /**
     * 判断查询数据是否存在
     *
     * @param pageResult 分页数据集
     * @return boolean
     * @author
     **/
    public static <T> boolean isExists(Supplier<Page<T>> pageResult) {
        PageParameter parameter = new PageParameter();
        parameter.setStartPage(1);
        parameter.setPageSize(1);

        PageHelper.startPage(parameter.getStartPage(), parameter.getPageSize(), false);

        return CollectionUtils.isNotEmpty(pageResult.get());
    }
}
