package com.yy.ppm.business.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName 费率(TBusRate)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月03日 16:48:00
 */
@Data
public class TBusRatePO extends BasePO implements Serializable {

    private static final long serialVersionUID = 655326951248452483L;

    /** ID */
    private Long id;
    /** 费目CODE */
    private String rateItemCode;
    /** 费目名称 */
    private String rateItemName;
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
    /** 费率值 */
    private BigDecimal rate;
    /** 税率 */
    private BigDecimal taxRate;
    /** 计量单位代码（字典：MEASUREMENT_UNIT） */
    private String measurementUnitCode1;
    /** 计量单位代码（字典：MEASUREMENT_UNIT） */
    private String measurementUnitName1;
    /** 计量单位代码2 */
    private String measurementUnitCode2;
    /** 计量单位代码2 */
    private String measurementUnitName2;
    /** 0停用 1审核中 9未通过 10通过 */
    private String status;
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
    /** 备注 */
    private String remark;
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
     * 费率表
     */
    private Long rateId;


    /**
     *  作业模式
     */
    private Integer workType;

    /**
     *  货物标识码
     */
    private String sign;

    /**
     *  数据来源（1标准费率，2包干费）
     */
    private Integer dataSource;
    private String flag;
    /**
     * 是否主营收入
     */
    private String isMainIncome;

    //金蝶应收账款类型
    private String easItemRateCode;
    private String easItemRateName;
}

