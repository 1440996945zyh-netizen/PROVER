package com.yy.ppm.statement.bean.dto.storageFee;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
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
public class TBusCargoInfoExportDTO{

    /**
     * 票货来源 卸船、集港、货转、混配
     */
    @ExcelProperty(value = "票货来源",index = 0) private String source;

    /**
     * 主键ID
     */
    @ExcelIgnore
    private Long id;

    /**
     * 票货号
     */
    @ExcelProperty(value = "票货号",index = 1) private String cargoInfoNo;

    /**
     * 货主ID
     */
    @ExcelIgnore private Long cargoOwnerId;

    /**
     * 货主名称
     */
    @ExcelProperty(value = "货主",index = 2) private String cargoOwnerName;

    /**
     * 货名
     */
    @ExcelProperty(value = "货名",index = 3) private String cargoName;

    /**
     * 作业模式Label
     */
    @ExcelProperty(value = "作业模式",index = 4) private String workTypeLabel;

    /**
     * 船舶预报ID
     */
   @ExcelIgnore private Long shipvoyageId;

    /**
     * 船舶航次ID
     */
    @ExcelIgnore private Long shipvoyageItemId;

    /**
     * 船名航次
     */
    @ExcelProperty(value = "船名航次" ,index = 5) private String shipNameVoyage;

    @ExcelProperty(value = "泊位" ,index = 6) private String berthName;
    /**
     * SCN
     */
    @ExcelProperty(value = "SCN" ,index = 7) private String scn;

    /**
     * 靠泊时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty(value = "靠泊时间" ,index = 8) private Date berthTime;

    /**
     * 开工时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty(value = "开工时间",index = 9) private Date workStartTime;

    /**
     * 完工时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty(value = "完工时间" ,index = 10) private Date workEndTime;

    /**
     * 离泊时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty(value = "离泊时间" ,index = 11) private Date leaveBerthTime;

    /**
     * 装卸
     */
    @ExcelProperty(value = "装卸" ,index = 12) private String loadUnload;

    /**
     * 进出口Label
     */
    @ExcelProperty(value = "进出口" ,index = 13) private String impExpLabel;

    /**
     * 贸别
     */
    @ExcelProperty(value = "贸别" ,index = 14)private String tradeType;

    /**
     * 首次集港时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty(value = "首次集港时间" ,index = 15) private Date minWeighOutDt;

    /**
     * 货转时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty(value = "货转时间" ,index = 16) private Date transferDate;

    /**
     * 混配时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty(value = "混配时间" ,index = 17) private Date mixTime;

    /**
     * 完货人姓名
     */
    @ExcelProperty(value = "完货人" ,index = 18) private String clearByName;

    /**
     * 完货日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ExcelProperty(value = "完货时间" ,index = 19) private Date realClearDate;

    /**
     * 指令编号
     */
    @ExcelProperty(value = "指令编号" ,index = 20)  private String trustNo;

    /**
     * 指令发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty(value = "指令发布时间" ,index = 21) private Date releaseTime;

    /**
     * 作业公司ID
     */
    @ExcelIgnore private Long companyId;

    /**
     * 作业公司名称
     */
    @ExcelProperty(value = "作业公司" ,index = 22) private String companyName;

    /**
     * 超期天数
     */
    @ExcelProperty(value = "作业公司" ,index = 23) private Integer overdueDays;

    @ExcelProperty(value = "是否超期" ,index = 24) private String isOverdue;

    /**
     * 是否完货Label
     */
    @ExcelProperty(value = "是否完货" ,index = 25) private String isClearLabel;

    /**
     * 是否完货
     */
    private String isClear;

    /**
     * 结算状态
     */
    @ExcelProperty(value = "结算状态" ,index = 26)private String statementStatus;


    /**
     * 库场使用费结算单ID
     */
    @ExcelIgnore  private Long tcssId;

    /**
     * 结算单ID
     */
    @ExcelIgnore  private Long statementId;
    /**
     * 结算单号
     */
    @ExcelIgnore  private String statementNo;

    /**
     * 本次结算金额
     */
    @ExcelIgnore private BigDecimal amount;

    /**
     * 免堆存期
     */
    @ExcelIgnore private Integer freeDays;

    /**
     * 驳回相关信息
     */
    @ExcelIgnore private Long rejectBy;
    @ExcelIgnore private String rejectByName;
    @ExcelIgnore private String rejectReason;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date rejectTime;

    /**
     * 税务类型
     */
    @ExcelIgnore private String taxationInvoiceCode;

    /**
     * 回执备注
     */
    @ExcelIgnore private String receiptRemark;

    @ExcelIgnore private String settleStatus;
    @ExcelIgnore private String cargoCategoryName;
    @ExcelIgnore private String cargoTypeName;

    @ExcelIgnore private String flowDirection;
    @ExcelIgnore private Date  overDate;
}
