package com.yy.ppm.equipment.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 物资库存盘点主表PO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EMaterialStockCheckPO extends BasePO {

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
     * 盘点状态：0-待盘点，1-盘点中，2-已完成，3-已调整
     */
    private Integer checkStatus;

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
     * 删除标记：0-未删除，1-已删除
     */
    private Integer delFlag;
}

