package com.yy.ppm.equipment.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备变更记录子表PO
 * @author system
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MEquipmentChangeLogDetailPO extends BasePO {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 变更记录主表ID
     */
    private Long changeLogId;

    /**
     * 变更字段
     */
    private String changeField;

    /**
     * 变更字段名称
     */
    private String changeFieldName;

    /**
     * 变更前值
     */
    private String oldValue;

    /**
     * 变更后值
     */
    private String newValue;
}

