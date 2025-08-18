package com.yy.ppm.statement.bean.dto.bizCostStatement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-14 14:36
 */
@Setter
@Getter
public class TCostStatementDTO extends BasePO {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空")
    private Long id;

    /**
     * 作业公司id
     */
    private Long companyId;

    /**
     * 作业公司NAME
     */
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
    private Long customerId;
    private String nextPortName;
    private String prePortName;

    /**
     * 客户名称
     */
    private String customerName;
    private String financeStatus;
    private String cargoInfoNo;

    /**
     * 结算单类型（10.船舶货方结算单、20.陆集陆疏货方结算单 ）30.船方计费 40.杂项计费
     */
    private String type;
    private String typeCode;
    private String rateItemCode;
    private String rateItemName;

    /**
     * 指令ID
     */
    private Long trustId;

    /**
     * 指令票货id
     */
    private Long trustCargoId;

    /**
     * 货物清单ID
     */
    private Long handoverlistId;
    private Long tugFeeId;

    /**
     * 航次ID
     */
    private Long shipvoyageId;

    /**
     * 航次子表ID
     */
    private Long shipvoyageItemId;

    /**
     * 货物代码
     */
    private String cargoCode;

    /**
     * 货名
     */
    private String cargoName;

    /**
     * 贸别，内贸、外贸
     */
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

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否最终结算 0否/1是
     */
    private String isFinal;

    /**
     * 是否最终结算
     */
    private String isFinalLabel;

    /**
     * 结算单类型
     */
    private String typeLabel;

    /**
     * 贸别Label
     */
    private String tradeTypeLabel;

    /**
     * 结算状态
     */
    private String statusLabel;

    /**
     * 货主名称
     */
    private String cargoOwnerName;

    /**
     * 货代名称
     */
    private String cargoAgentName;

    /**
     * 船名航次
     */
    private String shipNameVoyage;

    /**
     * 进出口Label
     */
    private String impExpLabel;

    /**
     * 交接清单量
     */
    private BigDecimal ton;

    /**
     * 结算单明细列表
     */
    @NotNull(message = "结算单明细不能为空")
    private List<TCostStatementDetailDTO> details;



    /**
     * scn 1108 回显预结算主列表的scn
     */
    private String scn;

    /**
     * 靠泊时间 1108 回显预结算主列表的靠泊时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date berthTime;

    /**
     * 离泊时间 1108 回显预结算主列表的离泊时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date leavePortTime;

    private BigDecimal number;

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

    /** 外贸结算依据名称（1.货物交接清单数、2.疏港过磅数、3.海关报关单数、4.集港过磅数5.水尺数） */
    private String outerSettlementBasisName;

    /**
     * 件杂 WORK_TYPE
     */
    private String workType;

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
     * 发票类型
     */
    private String taxationInvoiceCode;
    /**
     * 驳回原因
     */
    private String rejectReason;
    /**
     * 驳回相关信息
     */
    private Long rejectBy;
    private String rejectByName;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date rejectTime;
    /**
     * 商务审核相关信息
     */
    private Long recheckBy;
    private String recheckByName;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date recheckTime;


    /**
     * 批量处理用
     */
    private List<Long> ids;
    /**
     * 文件集合
     */
    private List<Long> fileIds;

    private List<Long> statementIds;

    private String routeType;

    private String inTime;

    private String contactNo;

    private BigDecimal Amount;
    private BigDecimal rate;

    private Long tbhId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long cargoInfoId;

    //指令编号回显
    private String trustNo;

    private String isClear;

    private String berthName;

    private String month;
    private String months;
    private String monthe;
    private String statementDate;
    private String shipName;
    private String voyage;
    private String flag;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date invoiceDate;
    private String contractNo;

    private Long mainStatementId;

    private String isGenerateGWF;
    /**
     * 离泊日期
     */
    private String leaveBerthTime;
    private String berthTimeStr;
    private String payTypeCode;
    private String payTypeName;
    private String isShip;
    private Integer isRedRush;
    private Long relationId;
    private Integer isDiscount;
    private Integer isPrint;
}
