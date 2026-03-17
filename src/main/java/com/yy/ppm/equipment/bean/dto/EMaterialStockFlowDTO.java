package com.yy.ppm.equipment.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 物资库存流水DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialStockFlowDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关系ID
     */
    private Long id;

    /**
     * 入库明细ID
     */
    private Long warehouseInDetailId;

    /**
     * 出库明细ID
     */
    private Long warehouseOutDetailId;

    /**
     * 数量
     */
    private BigDecimal quantity;

    /**
     * 物资代码
     */
    private String materialCode;

    /**
     * 物资名称
     */
    private String materialName;

    /**
     * 入库单ID
     */
    private Long warehouseInId;

    /**
     * 入库单号
     */
    private String warehouseInNo;

    /**
     * 入库日期
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date warehouseInDate;

    /**
     * 入库单主题
     */
    private String warehouseInTitle;

    /**
     * 出库单ID
     */
    private Long warehouseOutId;

    /**
     * 出库单号
     */
    private String warehouseOutNo;

    /**
     * 出库日期（出库单创建时间）
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date warehouseOutDate;

    /**
     * 出库单主题
     */
    private String warehouseOutTitle;

    /**
     * 操作类型：1-入库，2-出库
     */
    private Integer operationType;

    /**
     * 操作时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operationTime;

    /**
     * 创建时间（关系创建时间）
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}

