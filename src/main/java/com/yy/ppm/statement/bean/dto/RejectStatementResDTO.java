package com.yy.ppm.statement.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementDetailDTO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class RejectStatementResDTO extends BasePO {
    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空")
    private String id;

    /**
     * 作业公司id
     */
    private String companyId;

    /**
     * 作业公司NAME
     */
    private String companyName;

    /**
     * 结算单编号
     */
    private String statementNo;

    /**
     * 客户ID
     */
    private String customerId;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 结算单类型（10.船舶货方结算单、20.陆集陆疏货方结算单 ）30.船方计费 40.杂项计费
     */
    private String type;

    /**
     * 指令ID
     */
    private String trustId;

    /**
     * 指令票货id
     */
    private String trustCargoId;

    /**
     * 货物清单ID
     */
    private String handoverlistId;

    /**
     * 航次ID
     */
    private String shipvoyageId;

    /**
     * 航次子表ID
     */
    private String shipvoyageItemId;

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
    private String settlementDate;

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
    private String statementBy;
    private String statementByName;

    private String statementTime;

    /***
     * 审核人相关
     */
    private String reviewBy;
    private String reviewByName;

    private String reviewTime;

    /**
     * 商务确认人相关信息
     */
    private String confirmBy;
    private String confirmByName;
    private String confirmTime;



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
    private String rejectBy;
    private String rejectByName;
    private String rejectTime;


    /**
     * 文件集合
     */
    private List<String> fileIds;

    private List<String> statementIds;

    private String routeType;

    private String inTime;

    private String contactNo;

    private BigDecimal Amount;
    private BigDecimal rate;

    private String tbhId;

    private String cargoInfoId;

    //指令编号回显
    private String trustNo;

    private String isClear;

    private String berthName;

    private String cargoInfoNo;
    //回显作业过程
    private String processName;

}
