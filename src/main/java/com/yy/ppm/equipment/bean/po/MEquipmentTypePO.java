package com.yy.ppm.equipment.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;

/**
 * 设备类型分类PO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class MEquipmentTypePO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 设备类型名称
     */
    private String typeName;

    /**
     * 父级ID（顶级分类为NULL或0）
     */
    private Long parentId;

    /**
     * 分类级别：1-设备大类，2-设备中类，3-设备小类
     */
    private Integer categoryLevel;

    /**
     * 排序字段
     */
    private Integer sortOrder;

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

