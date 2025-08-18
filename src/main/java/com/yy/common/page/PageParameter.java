package com.yy.common.page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yy.common.enums.CommonConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 分页参数
 **/
public class PageParameter implements Serializable {

    private static final long serialVersionUID = -7696440725170038746L;

    /**
     * 页码
     **/
    private Integer startPage;

    /**
     * 页行
     **/
    private Integer pageSize;

    /**
     * 排序列
     **/
    private String sortName;

    /**
     * 排序方式false/ASC, true/DESC
     **/
    private boolean symbol;

    /**
     * 支持多字段查询，优先级高于sortname
     * <p>
     * 1、例如：type ASC,id DESC
     * 2、例如：type,id DESC
     * 3、例如：type,id ASC
     */
    private String order;

    /** 请求参数 */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;

    public Map<String, Object> getParams()
    {
        if (params == null)
        {
            params = new HashMap<>();
        }
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Integer getStartPage() {

        return startPage == null ? 1:startPage;
    }

    public void setStartPage(Integer startPage) {
        this.startPage = startPage;
    }

    public Integer getPageSize() {

        return pageSize == null ? CommonConstants.PAGE_SIZE :pageSize;
    }

    public void setPageSize(Integer pageSize) {

        if (pageSize == null) {
            this.pageSize = CommonConstants.PAGE_SIZE;
        }
        this.pageSize = pageSize;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public boolean isSymbol() {
        return symbol;
    }

    public void setSymbol(boolean symbol) {
        this.symbol = symbol;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
