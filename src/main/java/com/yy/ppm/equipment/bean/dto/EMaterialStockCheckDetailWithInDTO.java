package com.yy.ppm.equipment.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 物资库存盘点明细表DTO（包含入库明细和入库主表信息）
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialStockCheckDetailWithInDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // ========== 盘点明细表字段 ==========
    /**
     * 盘点明细主键ID
     */
    private Long id;

    /**
     * 盘点主表ID
     */
    private Long checkId;

    /**
     * 物资ID
     */
    private Long materialId;

    /**
     * 物资名称
     */
    private String materialName;

    /**
     * 规格型号
     */
    private String specificationModel;

    /**
     * 计量单位编码
     */
    private String unitCode;

    /**
     * 计量单位名称
     */
    private String unitName;

    /**
     * 账面数量
     */
    private BigDecimal bookQuantity;

    /**
     * 盘点数量
     */
    private BigDecimal checkQuantity;

    /**
     * 差异数量（盘点数量-账面数量）
     */
    private BigDecimal differenceQuantity;

    /**
     * 差异类型：1-盘盈，2-盘亏，0-无差异
     */
    private Integer differenceType;

    /**
     * 差异类型名称
     */
    private String differenceTypeName;

    /**
     * 入库明细ID（关联E_MATERIAL_WAREHOUSE_IN_DETAIL表）
     */
    private Long warehouseInDetailId;

    /**
     * 盘点状态：0-待盘点，1-已盘点
     */
    private Integer checkStatus;

    /**
     * 盘点状态名称
     */
    private String checkStatusName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 审核人ID
     */
    private Long auditBy;

    /**
     * 审核人姓名
     */
    private String auditByName;

    /**
     * 审核时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;

    // ========== 入库明细表字段 ==========
    /**
     * 入库主表ID
     */
    private Long warehouseInId;

    /**
     * 入库数量
     */
    private BigDecimal warehouseInQuantity;

    /**
     * 已出库数量
     */
    private BigDecimal outQuantity;

    /**
     * 剩余数量
     */
    private BigDecimal remainingQuantity;

    /**
     * 含税单价
     */
    private BigDecimal taxIncludedUnitPrice;

    /**
     * 含税金额
     */
    private BigDecimal taxIncludedAmount;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 规格描述
     */
    private String specificationDesc;

    // ========== 入库主表字段 ==========
    /**
     * 入库单号
     */
    private String warehouseInNo;

    /**
     * 入库主题
     */
    private String warehouseInTitle;

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
     * 验收状态：0-待验收，1-通过，2-不通过
     */
    private Integer acceptanceStatus;

    /**
     * 验收状态名称
     */
    private String acceptanceStatusName;

    /**
     * 验收备注
     */
    private String acceptanceRemarks;
}

