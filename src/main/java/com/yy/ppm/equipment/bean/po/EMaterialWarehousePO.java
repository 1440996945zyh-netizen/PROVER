package com.yy.ppm.equipment.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;

/**
 * 物资仓库PO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialWarehousePO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 仓库编号
     */
    private String warehouseCode;

    /**
     * 仓库名称
     */
    private String warehouseName;

    /**
     * 公司ID
     */
    private Long companyId;

    /**
     * 公司名称
     */
    private String companyName;

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

