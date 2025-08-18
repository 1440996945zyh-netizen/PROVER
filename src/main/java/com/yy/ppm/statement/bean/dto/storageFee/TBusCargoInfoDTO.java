package com.yy.ppm.statement.bean.dto.storageFee;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @java.util.Date 2023-11-24 14:05
 */
@Setter
@Getter
public class TBusCargoInfoDTO extends BasePO {

    /**
     * 票货来源 卸船、集港、货转、混配
     */
    private String source;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 票货号
     */
    private String cargoInfoNo;

    /**
     * 货主ID
     */
    private Long cargoOwnerId;

    /**
     * 货主名称
     */
    private String cargoOwnerName;

    /**
     * 货名
     */
    private String cargoName;

    /**
     * 作业模式Label
     */
    private String workTypeLabel;

    /**
     * 船舶预报ID
     */
    private Long shipvoyageId;

    /**
     * 船舶航次ID
     */
    private Long shipvoyageItemId;

    /**
     * 船名航次
     */
    private String shipNameVoyage;

    /**
     * SCN
     */
    private String scn;

    /**
     * 靠泊时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date berthTime;

    /**
     * 开工时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date workStartTime;

    /**
     * 完工时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date workEndTime;

    /**
     * 离泊时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date leaveBerthTime;

    /**
     * 装卸
     */
    private String loadUnload;

    /**
     * 进出口Label
     */
    private String impExpLabel;

    /**
     * 贸别
     */
    private String tradeType;

    /**
     * 首次集港时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date minWeighOutDt;

    /**
     * 货转时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date transferDate;

    /**
     * 混配时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date mixTime;

    /**
     * 完货人姓名
     */
    private String clearByName;

    /**
     * 完货日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date realClearDate;

    /**
     * 指令编号
     */
    private String trustNo;

    /**
     * 指令发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date releaseTime;

    /**
     * 作业公司ID
     */
    private Long companyId;

    /**
     * 作业公司名称
     */
    private String companyName;

    /**
     * 超期天数
     */
    private Integer overdueDays;

    /**
     * 是否完货Label
     */
    private String isClearLabel;

    /**
     * 是否完货
     */
    private String isClear;

    /**
     * 结算状态
     */
    private String statementStatus;

    private String berthName;

    /**
     * 库场使用费结算单ID
     */
    private Long tcssId;

    /**
     * 结算单ID
     */
    private Long statementId;
    /**
     * 结算单号
     */
    private String statementNo;

    /**
     * 本次结算金额
     */
    private BigDecimal amount;

    /**
     * 免堆存期
     */
    private Integer freeDays;

    /**
     * 驳回相关信息
     */
    private Long rejectBy;
    private String rejectByName;
    private String rejectReason;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date rejectTime;

    /**
     * 税务类型
     */
    private String taxationInvoiceCode;

    /**
     * 回执备注
     */
    private String receiptRemark;

    private String settleStatus;
    private String cargoCategoryName;
    private String cargoTypeName;

    private String flowDirection;
    private Date  overDate;
    private String isReduceTypeThree;
}
