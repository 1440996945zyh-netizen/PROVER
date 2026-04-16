package com.yy.ppm.equipment.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 物资出库主表PO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EMaterialWarehouseOutPO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 出库单号
     */
    private String warehouseOutNo;

    /**
     * 出库主题
     */
    private String warehouseOutTitle;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 仓库名称
     */
    private String warehouseName;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 领料人ID
     */
    private Long receiverId;

    /**
     * 领料人名称
     */
    private String receiverName;

    /**
     * 状态：0-待确认，1-已确认
     */
    private Integer status;

    /**
     * 确认人ID
     */
    private Long confirmBy;

    /**
     * 确认人名称
     */
    private String confirmByName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date confirmTime;

    /**
     * 盘点单ID（关联E_MATERIAL_STOCK_CHECK表）
     */
    private Long checkId;

    private String warehouseOutTypeCode;

    private String warehouseOutTypeName;

    private Long sourceBizId;

    private String sourceBizNo;
}

