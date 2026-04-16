package com.yy.ppm.equipment.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 物资调拨主表 PO。
 * 对应物资调拨主单基础信息及执行结果信息。
 *
 * @author system
 */
@Getter
@Setter
@ToString
public class EMaterialAllocatePO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID。
     */
    private Long id;

    /**
     * 调拨编号。
     */
    private String allocateCode;

    /**
     * 调拨标题。
     */
    private String title;

    /**
     * 调出单位ID。
     */
    private Long fromCompanyId;

    /**
     * 调出单位名称。
     */
    private String fromCompanyName;

    /**
     * 调出仓库ID。
     */
    private Long fromWarehouseId;

    /**
     * 调出仓库名称。
     */
    private String fromWarehouseName;

    /**
     * 调入单位ID。
     */
    private Long toCompanyId;

    /**
     * 调入单位名称。
     */
    private String toCompanyName;

    /**
     * 调入仓库ID。
     */
    private Long toWarehouseId;

    /**
     * 调入仓库名称。
     */
    private String toWarehouseName;

    /**
     * 调拨时间。
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date allocateTime;

    /**
     * 申请人ID。
     */
    private Long applyUser;

    /**
     * 申请人姓名。
     */
    private String applyUserName;

    /**
     * 申请原因。
     */
    private String applyReason;

    /**
     * 执行状态：0未执行，1执行成功，2执行失败。
     */
    private Integer executeStatus;

    /**
     * 执行结果信息。
     */
    private String executeMsg;

    /**
     * 执行时间。
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date executeTime;

    /**
     * 执行人ID。
     */
    private Long executeUser;

    /**
     * 执行人姓名。
     */
    private String executeUserName;

    /**
     * 关联调出库单ID。
     */
    private Long outWarehouseId;

    /**
     * 关联调出库单号。
     */
    private String outWarehouseNo;

    /**
     * 关联调入库单ID。
     */
    private Long inWarehouseId;

    /**
     * 关联调入库单号。
     */
    private String inWarehouseNo;

    /**
     * 备注。
     */
    private String remarks;
}
