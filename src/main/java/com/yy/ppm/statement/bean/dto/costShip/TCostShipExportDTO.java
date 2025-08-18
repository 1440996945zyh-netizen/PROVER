package com.yy.ppm.statement.bean.dto.costShip;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.excel.export.bean.SheetMapping;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class TCostShipExportDTO extends SheetMapping {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 付款人ID
     */
    private Long customerId;

    /**
     * 付款人姓名
     */
    private String customerName;


    /**
     * 船舶预报ID，类型为停泊费时非空
     */
    private Long shipvoyageId;


    /**
     * 船舶航次ID，类型为停泊费时非空
     */
    private Long shipvoyageItemId;

    /**
     * 船舶类型
     */
    private String shipKindName;
    /**
     * 合计
     */
    private BigDecimal countAmount;

    /**
     * 10停泊费/20加水接电费
     */
    private String type;




    /**
     * 指令ID，类型为加水接电费时非空
     */
    private Long trustId;

    /**
     * 数量
     */

    private Integer number;

    /**
     * 数量2（船舶净吨）
     */
    private Integer number2;

    /**
     * 费目编码
     */
    private String rateItemCode;

    /**
     * 费目名称
     */
    private String rateItemName;

    /**
     * 费率
     */
    private BigDecimal rate;

    /**
     * 税率
     */
    private BigDecimal taxRate;

    /**
     * 计费单位编码
     */
    private String unitCode;

    /**
     * 计费单位名称
     */
    private String unitName;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 税额
     */
    private BigDecimal taxAmount;

    /**
     * 状态 10未审核/20已审核（生成结算单）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 作业公司ID
     */
    private Long companyId;

    /**
     * 作业公司名称
     */
    private String companyName;




    /**
     *  费用项目
     */
    private String feeItem;


    /**
     * 计费人
     */
    private String feeManName;
    /**
     * 审核人
     */
    private String reviewByName;

    private String reviewTime;

    List<CostShipDetailExportDTO> detailList;






    /**
     * 费率id
     */
    private Long rateId;

    /** 服务内容id */
    private Long serviceContentId;
    /** 服务内容名称 */
    private String serviceContentName;
    /** 作业过程CODE */
    private String processCode;
    /** 作业过程NAME */
    private String processName;
    /** 货物代码 */
    private String cargoCode;
    /** 货物名称 */
    private String cargoName;
    /** 货类代码 */
    private String cargoTypeCode;
    /** 货类名称 */
    private String cargoTypeName;
    /** 货种代码 */
    private String cargoCategoryCode;
    /** 货种名称 */
    private String cargoCategoryName;
    /** 内外贸 */
    private String inteFore;

    /** 计量单位代码（字典：MEASUREMENT_UNIT） */
    private String measurementUnitCode1;
    /** 计量单位代码（字典：MEASUREMENT_UNIT） */
    private String measurementUnitName1;
    /** 计量单位代码2 */
    private String measurementUnitCode2;
    /** 计量单位代码2 */
    private String measurementUnitName2;
    /*    *//** 0停用 1审核中 9未通过 10通过 *//*
    private String status;*/
    /** 0停用 1审核中 9未通过 10通过 */
    private String statusLabel;
    /** 免堆场期 */
    /** 有效期起 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    /** 有效期止 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    /*    *//** 备注 *//*
    private String remark;*/
    /** 进出口 */
    private String inOut;

    /**
     * 审核人
     */
    private Long examineBy;

    /**
     * 审核人姓名
     */
    private String examineByName;

    /**
     * 审核时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date examineTime;

    /**
     * 免堆存期，仅堆存费有
     */
    private Integer freeStorageDays;
    /**
     *  作业模式
     */
    private Integer workType;
    /**
     *  计费依据
     */
    private String payBasis;

    private String scn;

    private String shipName;

    private String voyage;

    private String tradeType;

    private String loadUnload;


    private BigDecimal netWeight;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date berthTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date leaveBerthTime;

    private Integer berthDays;

    private String statementNo;


    private String createByName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


    private String berthName;

    private String arrivalAnchorageTime;

    private String anchorageDays;

    private String tinggongFugongTimes;

    private Integer tinggongFugongHours;

}
