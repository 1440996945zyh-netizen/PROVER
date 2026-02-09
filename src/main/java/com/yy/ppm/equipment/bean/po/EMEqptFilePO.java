package com.yy.ppm.equipment.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 设备资料文件PO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EMEqptFilePO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 设备ID
     */
    private Long equipId;

    /**
     * 资料类型CODE
     */
    private String dataTypeCode;

    /**
     * 资料类型NAME
     */
    private String dataTypeName;

    /**
     * 文件表ID
     */
    private Long fileTableId;

    /**
     * 备注
     */
    private String remark;
}

