package com.yy.ppm.equipment.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 物资入库主表PO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EMaterialWarehouseInPO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 入库主题
     */
    private String warehouseInTitle;

    /**
     * 入库单号
     */
    private String warehouseInNo;

    /**
     * 入库类型编码
     */
    private String warehouseInTypeCode;

    /**
     * 入库类型名称
     */
    private String warehouseInTypeName;

    /**
     * 入库日期
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date warehouseInDate;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 所属仓库ID
     */
    private Long warehouseId;

    /**
     * 所属仓库名称
     */
    private String warehouseName;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 交货人ID
     */
    private Long deliveryPersonId;

    /**
     * 交货人名称
     */
    private String deliveryPersonName;

    /**
     * 验收人ID
     */
    private Long acceptancePersonId;

    /**
     * 验收人名称
     */
    private String acceptancePersonName;

    /**
     * 验收状态：0-待验收，1-通过，2-不通过
     */
    private Integer acceptanceStatus;

    /**
     * 验收备注
     */
    private String acceptanceRemarks;

    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 盘点单ID（关联E_MATERIAL_STOCK_CHECK表）
     */
    private Long checkId;

    /**
     * 审批人ID
     */
    private Long approvalBy;

    /**
     * 审批人姓名
     */
    private String approvalByName;

    /**
     * 审批时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date approvalTime;

    private Long sourceBizId;

    private String sourceBizNo;
}

