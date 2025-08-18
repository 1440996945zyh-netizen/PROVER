package com.yy.ppm.statement.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 堆存费历史结算表PO
 * @author yangcl
 * */
@Data
public class TStorageFeeHisPO extends BasePO implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 合同id
     */
    private Long contractId;

    /**
     * 票货id
     */
    private Long cargoInfoId;

    /**
     * 结算开始日期
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    /**
     * 计算结束日期
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    /**
     * 费率
     */
    private BigDecimal rate;

    /**
     * 结算金额
     */
    private BigDecimal amount;

    /**
     * 结余量 场地剩余量
     */
    private BigDecimal balancesAmount;

    /**
     * 总进场量
     */
    private BigDecimal totalEntry;

    /**
     * 总出厂量
     */
    private BigDecimal totalAppearance;

    /**
     * 结算天数
     */
    private Integer settlementDays;

    /**
     * 免堆存天数
     */
    private Integer freeStorageDays;

    /**
     * 税率
     */
    private BigDecimal taxRate;

    /**
     * 税额
     */
    private BigDecimal taxCast;

    /**
     * 税后金额
     */
    private BigDecimal afterTax;

    /**
     * 计算状态 0未生成 1已生成
     */
    private int status;

    private static final long serialVersionUID = 1L;

    /**
     * 是否最终结算 0否/1是
     */
    @NotBlank(message = "是否最终结算不能为空")
    private String isFinal;

    /**
     * 结算状态
     */
    private String statementStatus;

    /**
     * 费率ID
     */
    private Long rateId;
}

