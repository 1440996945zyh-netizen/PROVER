package com.yy.ppm.equipment.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 物资库存盘点主表DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialStockCheckDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 盘点单号
     */
    private String checkNo;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 仓库名称
     */
    private String warehouseName;

    /**
     * 盘点日期（保留字段，兼容旧数据）
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date checkDate;

    /**
     * 盘点开始日期
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date checkStartDate;

    /**
     * 盘点结束日期
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date checkEndDate;

    /**
     * 盘点主题
     */
    private String checkTitle;

    /**
     * 盘点类型：1-全量盘点，2-部分盘点
     */
    private Integer checkType;

    /**
     * 盘点类型名称
     */
    private String checkTypeName;

    /**
     * 盘点状态：0-待盘点，1-盘点中，2-已完成，3-已调整
     */
    private Integer checkStatus;

    /**
     * 盘点状态名称
     */
    private String checkStatusName;

    /**
     * 盘点人ID
     */
    private Long checkPersonId;

    /**
     * 盘点人姓名
     */
    private String checkPersonName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人ID
     */
    private Long createBy;

    /**
     * 创建人姓名
     */
    private String createByName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新人ID
     */
    private Long updateBy;

    /**
     * 更新人姓名
     */
    private String updateByName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 盘点明细列表
     */
    private List<EMaterialStockCheckDetailDTO> detailList;

    /**
     * 部分盘点时的物资ID列表（用于前端传递）
     */
    private List<Long> materialIds;

    /**
     * 物资名称（从明细表关联获取，用于列表显示）
     */
    private String materialName;

    /**
     * 规格型号（从明细表关联获取，用于列表显示）
     */
    private String specificationModel;

    /**
     * 计量单位（从明细表关联获取，用于列表显示）
     */
    private String unitName;

    /**
     * 账面数量（从明细表关联获取，用于列表显示）
     */
    private java.math.BigDecimal bookQuantity;

    /**
     * 盘点数量（从明细表关联获取，用于列表显示）
     */
    private java.math.BigDecimal checkQuantity;

    /**
     * 差异数量（从明细表关联获取，用于列表显示）
     */
    private java.math.BigDecimal differenceQuantity;

    /**
     * 差异类型（从明细表关联获取，用于列表显示）
     */
    private Integer differenceType;

    /**
     * 差异类型名称（从明细表关联获取，用于列表显示）
     */
    private String differenceTypeName;

    /**
     * 盘点明细总条数
     */
    private Integer totalDetailCount;

    /**
     * 未盘点明细条数
     */
    private Integer uncheckedCount;

    /**
     * 待确认明细条数
     */
    private Integer pendingConfirmCount;

    /**
     * 已盘点明细条数
     */
    private Integer checkedCount;
}

