package com.yy.ppm.statement.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther linqi
 * @Description 堆存费结算表
 * @java.util.Date 2023-11-24 9:54
 */
@Setter
@Getter
public class TCostStorageSettlePO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 作业公司ID
     */
    @NotNull(message = "作业公司ID不能为空")
    private Long companyId;

    /**
     * 作业公司名称
     */
    @NotBlank(message = "作业公司名称不能为空")
    private String companyName;

    /**
     * 船舶预报ID
     */
    private Long shipvoyageId;

    /**
     * 船舶航次ID
     */
    private Long shipvoyageItemId;

    /**
     * 货主ID
     */
    @NotNull(message = "货主ID不能为空")
    private Long cargoOwnerId;

    /**
     * 货主名称
     */
    @NotBlank(message = "货主名称不能为空")
    private String cargoOwnerName;

    /**
     * 票货ID
     */
    @NotNull(message = "票货ID不能为空")
    private Long cargoInfoId;

    /**
     * 交接清单ID
     */
    private Long handoverlistId;

    /**
     * 合同ID
     */
    @NotNull(message = "合同ID不能为空")
    private Long contractId;

    /**
     * 合同费率ID
     */
    @NotNull(message = "合同费率ID不能为空")
    private Long contractRateId;

    /**
     * 费率
     */
    @NotNull(message = "费率不能为空")
    private BigDecimal rate;

    /**
     * 税率
     */
    @NotNull(message = "税率不能为空")
    private BigDecimal tax;

    /**
     * 免堆存期
     */
    @NotNull(message = "免堆存期不能为空")
    private Integer freeStorageDays;

    /**
     * 本次结算金额
     */
    private BigDecimal amount;

    /**
     * 本次结算税额
     */
    private BigDecimal taxAmount;

    /**
     * 开始结算日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date startDate;

    /**
     * 截止结算日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date endDate;

    /**
     * 状态 10未审核/20已审核
     */
    private String status;

    /**
     * 审核人
     */
    private Long reviewBy;

    /**
     * 审核人姓名
     */
    private String reviewByName;

    /**
     * 审核时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reviewByTime;

    /**
     * 是否最终结算 0否/1是
     */
    @NotBlank(message = "是否最终结算编码不能为空")
    private String isFinal;

    /**
     * 是否使用减免编码
     */
    @NotBlank(message = "是否使用减免编码不能为空")
    private String isUseReduce;

    /**
     * 减免金额
     */
    private BigDecimal reduceAmount;
    /**
     * 回执驳回
     */
    private Long rejectBy;
    private String rejectByName;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date rejectTime;
    private String rejectReason;

    private String reduceType;
}
