package com.yy.ppm.statement.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 结算单(TCostStatement)PO
 *
 * @author linqi
 * @since 2023-09-07 10:41:07
 */
@Setter
@Getter
public class TCostStatementPO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 作业公司id
     */
    @NotNull(message = "作业公司ID不能为空")
    private Long companyId;

    /**
     * 作业公司NAME
     */
    @NotBlank(message = "作业公司名称不能为空")
    private String companyName;

    /**
     * 结算单编号
     */
    private String statementNo;

    //是否订单录入(0功能生成1订单录入)
    private String isEnter;

    /**
     * 客户ID
     */
    @NotNull(message = "客户（结算单位）ID不能为空")
    private Long customerId;

    /**
     * 客户名称
     */
    @NotBlank(message = "客户（结算单位）名称不能为空")
    private String customerName;
    private String customerNameYs;

    /**
     * 结算单类型（10.船舶货方结算单、20.陆集陆疏货方结算单 ）30.船方计费 40.杂项计费
     */
    private String type;
    private String typeCode;

    /**
     * 指令ID
     */
    private Long trustId;

    /**
     * 指令票货id
     */
    private Long trustCargoId;
    private Long cargoInfoId;
    private String cargoInfoNo;

    /**
     * 货物清单ID
     */
    @NotNull(message = "交接清单ID不能为空")
    private Long handoverlistId;

    /**
     * 航次ID
     */
    @NotNull(message = "船舶预报ID不能为空")
    private Long shipvoyageId;

    /**
     * 航次子表ID
     */
    @NotNull(message = "船舶航次ID不能为空")
    private Long shipvoyageItemId;

    /**
     * 货物代码
     */
    @NotBlank(message = "货物编码不能为空")
    private String cargoCode;

    /**
     * 货名
     */
    @NotBlank(message = "货名不能为空")
    private String cargoName;

    /**
     * 贸别，内贸、外贸
     */
    @NotBlank(message = "贸别编码不能为空")
    private String tradeType;

    /**
     * 结算日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date settlementDate;

    /**
     * 状态（1.生产结算 2.商务结算 3.计费审核 4.开票）
     */
    private String status;
    private String statusLabel;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否最终结算 0否/1是
     */
    @NotBlank(message = "是否最终结算不能为空")
    private String isFinal;

    /**
     * 状态CODE，预报、确报、抵锚..,（字典SHIP_STATUS）
     */
    private String shipStatusCode;

    /**
     * 离泊时间起始
     */
    @DateTimeFormat(pattern = "yy-MM-dd")
    private Date beginLeaveBerthTime;

    /**
     * 离泊时间截止
     */
    @DateTimeFormat(pattern = "yy-MM-dd")
    private Date endLeaveBerthTime;

    /**
     * 离港时间起始
     */
    @DateTimeFormat(pattern = "yy-MM-dd")
    private Date beginLeavePortTime;

    /**
     * 离港时间截止
     */
    @DateTimeFormat(pattern = "yy-MM-dd")
    private Date endLeavePortTime;

/**
 *      STATEMENT_BY   计费人
 *      REVIEW_BY      审核人
 *      CONFIRM_BY     商务确认人
 */

    /***
     * 计费人相关
     */
    private Long statementBy;
    private String statementByName;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date statementTime;

    /***
     * 审核人相关
     */
    private Long reviewBy;
    private String reviewByName;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date reviewTime;

    /**
     * 商务确认人相关信息
     */
    private Long confirmBy;
    private String confirmByName;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date confirmTime;

    /** 结算依据名称（1.货物交接清单数、2.疏港过磅数、3.海关报关单数、4.集港过磅数5.水尺数） */
    private String settlementBasisName;
    private String settlementBasisCode;



    /**
     * 回执备注
     */
    private String receiptRemark;
    /**
     * 开具类型
     */
    private String taxInvoiceCode;
    private String taxInvoiceName;
    /**
     * 回执驳回
     */
    private Long rejectBy;
    private String rejectByName;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date rejectTime;
    private String rejectReason;


    private BigDecimal amount;
    private BigDecimal amountjf;
    private BigDecimal amountzk;

    private String rateItemCode;
    private String rateItemName;

    /**
     * 进保税区货量
     */
    private BigDecimal bondedAreaTon;

    /**
     * 拖轮计费ID，当type为60：拖轮计费时非空
     */
    private Long tugFeeId;
    private String shipNameVoyage;
    private String financeStatus;
    private String financeStatusLabel;
    private String financeByName;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date financeTime;

    private Long mainStatementId;

    private String isGenerateGWF;
    private Long recheckBy;
    private String recheckByName;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date recheckTime;

    private Long applyInvoiceBy;
    private String applyInvoiceByName;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date applyInvoiceTime;
    private BigDecimal invoiceAmount;
    private BigDecimal invoiceAmountsy;
    //收款金额
    private BigDecimal utilizedAmountSum;
    private BigDecimal utilizedAmount;
    private BigDecimal utilizedAmountsy;
    private String payTypeCode;
    private String payTypeName;
    private String isShip;
    private String isRedRush;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long relationId;

    /**
     * 服务内容id
     */
    private Long serviceContentId;

    /**
     * 服务内容名称
     */
    private String serviceContentName;

    /**
     * 作业过程代码
     */
    private String processCode;

    /**
     * 作业过程名称
     */
    private String processName;

    /**
     * 费率值
     */
    private BigDecimal rate;

    /**
     * 计费单位代码(字典：UNIT)
     */
    private String unitCode;

    /**
     * 计费单位名称
     */
    private String unitName;

    /**
     * 计费数量
     */
//    @NotNull(message = "计费数量不能为空")
    private BigDecimal number;


    /**
     * 税率值
     */
    private BigDecimal tax;

    /**
     * 税额
     */
    private BigDecimal taxAmount;

    /**
     * 已开票数量
     */
    private BigDecimal invoiceNumber;

    /**
     * 结算单类型为30船方计费/40杂项计费/50堆存费时非空
     */
    private Long businessId;


    /**
     * 费率ID
     */
    private Long rateId;

    /**
     * 计费数量2（船舶净吨）
     */
    private BigDecimal number2;

    private String impExp;

    /**
     * 靠泊时间 1108 回显预结算主列表的靠泊时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date berthTime;
    /**
     * 靠泊时间 1108 回显预结算主列表的靠泊时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date leaveBerthTime;
    private String businessNo;
    private String workContent;
    private String wtdw;
    private String workLocation;
    private String isPrint;
}

