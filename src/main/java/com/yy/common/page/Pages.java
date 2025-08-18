package com.yy.common.page;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页工具类
 *
 * @author
 **/
@Data
public final class Pages<T> implements Serializable {

    private static final long serialVersionUID = 6677852537002542077L;

    /**
     * 总行数
     **/
    private long totalNum;

    /**
     * 总页数
     **/
    private int totalPageNum;

    /**
     * 页行
     **/
    private int pageSize;

    /**
     * 页码
     **/
    private int pageNum;

    /**
     * 当前页数据集
     **/
    private List<T> pages;

    /**
     * 额外数据（传Json格式的数据）
     **/
    private String extraData;
}
