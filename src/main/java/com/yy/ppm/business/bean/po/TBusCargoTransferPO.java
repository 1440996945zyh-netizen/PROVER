package com.yy.ppm.business.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName 货权转移记录表(TBusCargoTransfer)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月03日 19:37:00
 */
@Data
public class TBusCargoTransferPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -93483842381619720L;

    /** 主键ID */
    private Long id;
    /** 货转日期 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date transferDate;
    /** 原票货ID */
    private Long cargoInfoIdSource;
    /** 目标票货ID */
    private Long cargoInfoIdTarget;
    /** 货转件数 */
    private Long quantity;
    /** 货转重量 */
    private BigDecimal ton;
    /** 目标货主ID */
    private Long cargoOwnerId;
    /** 目标货主名称 */
    private String cargoOwnerName;
    /** 目标货代ID */
    private Long cargoAgentId;
    /** 目标货代名称 */
    private String cargoAgentName;
    /** 堆存费起算日期 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date storageDate;
    /** 状态：1:待审核，10:已审核 */
    private String status;
    /** 备注 */
    private String remark;
    /** 审核者-ID */
    private Long approvalBy;
    /** 审核者-姓名 */
    private String approvalByName;
    /** 审核时间 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date approvalTime;

    /** 付款人ID */
    private Long paymentCustomerId;
    /** 付款人名称 */
    private String paymentCustomerName;
    /** 是否付费 */
    private String isBilling;
    /**
     * 票货来源（卸船、集港）
     */
    private String cargoSource;
    /** 原票货免堆存费起算日期*/
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date sourceStorageDate;

    /** 库场审核者-ID */
    private Long yardApprovalBy;
    /** 库场审核者-姓名 */
    private String yardApprovalByName;
    /** 库场审核时间 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date yardApprovalTime;

}

