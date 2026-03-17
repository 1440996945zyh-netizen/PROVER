package com.yy.ppm.equipment.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;

/**
 * 物资类别PO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialCategoryPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 父级ID（顶级分类为NULL或0）
     */
    private Long parentId;

    /**
     * 类别编码
     */
    private String categoryCode;
    private String categoryPath;

    /**
     * 类别名称
     */
    private String categoryName;

    /**
     * 顺序号
     */
    private Integer sortOrder;

    /**
     * 是否纳入劳保管理（0-否，1-是）
     */
    private Integer isLaborProtection;

    /**
     * 等级（1-一级，2-二级，3-三级）
     */
    private Integer categoryLevel;

    /**
     * 编码最大值（用于生成子级编码）
     */
    private Integer codeCount;

    /**
     * 删除标志（0-未删除，1-已删除）
     */
    private Integer delFlag;

    /**
     * 删除人
     */
    private String deleteBy;

    /**
     * 删除人姓名
     */
    private String deleteByName;

    /**
     * 删除时间
     */
    private java.util.Date deleteTime;
}

