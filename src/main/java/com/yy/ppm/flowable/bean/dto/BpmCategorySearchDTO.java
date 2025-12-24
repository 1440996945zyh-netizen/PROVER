package com.yy.ppm.flowable.bean.dto;

import com.yy.common.page.PageParameter;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;

/**
 * 流程分类
 */
@Data
public class BpmCategorySearchDTO extends PageParameter implements Serializable {
    /**
     * 分类编号
     */
    private Long id;
    /**
     * 分类名
     */
    private String name;
    /**
     * 分类标志
     */
    private String code;
    /**
     * 分类描述
     */
    private String description;
    /**
     * 分类状态
     */
    private Integer status;
    /**
     * 分类排序
     */
    private Integer sort;
}
