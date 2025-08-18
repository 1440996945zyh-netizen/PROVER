package com.yy.ppm.business.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName 作业指令表(TBusTrust)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月05日 09:21:00
 */
@Data
public class TBusTrustPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -73695459173569293L;

    /** 主键ID */
    private Long id;
    /** 指令编号 */
    private String trustNo;
    /** 指令类型 指令带过来 */
    private String trustType;
    /** 合同ID */
    private Long contractId;
    /** 客户ID，多选 */
    private Long customerId;
    /** 客户名称，多选 */
    private String customerName;
    /** 作业过程代码 */
    private String processCode;
    /** 作业过程名称 */
    private String processName;
    /** 货种代码 */
    private String cargoCategoryCode;
    /** 货种名称 */
    private String cargoCategoryName;
    /** 货主 */
    private  String cargoOwnerName;
    /** 货代 */
    private String cargoAgentName;
    /** 货名 */
    private String cargoName;

    /** 贸别，内贸、外贸 */
    private String tradeType;
    /** 计划件数 */
    private Long planQuantity;
    /** 计划重量 */
    private BigDecimal planTon;
    /** 作业要求 */
    private String remark;
    /** 航次ID */
    private Long shipvoyageId;
    /** 航次子表ID **/
    private Long shipvoyageItemId;
    /** 中文船名 */
    private String shipName;
    /** 结算依据代码(字典:SETTLEMENT_ BASIS) */
    private String settlementBasisCode;
    /** 结算依据名称（1.货物交接清单数、2.疏港过磅数、3.海关报关单数、4.集港过磅数、5.水尺数） */
    private String settlementBasisName;
    /** 核销重量 */
    private BigDecimal checkTon;
    /** 核销数量 */
    private Integer checkNumber;
    /** 作业公司id */
    private Long companyId;
    /** 作业公司NAME */
    private String companyName;
    /** 状态，10：待审核20：已审核30：已发布：40：作业中50：核销 */
    private String status;
    /** 发布人 */
    private Long releaseBy;
    /** 发布人姓名 */
    private String releaseByName;
    /** 发布时间 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date releaseTime;
    /** 核销人 */
    private Long checkBy;
    /** 核销姓名 */
    private String checkByName;
    /** 核销时间 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date checkTime;
    /** 发布人 */
    private Long examineBy;
    /** 发布人姓名 */
    private String examineByName;
    /** 发布时间 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date examineTime;
    /** 进出口 */
    private String impExp;

    /**
     * 作业伴随ID
     */
    private Long workAccompanyingId;

    /**
     * 是否收费，仅杂项有 0否/1是
     */
    private String isBill;

    /**
     * 预估金额
     */
    private BigDecimal estAmount;

    /**
     * 通知单类型
     */
    @NotBlank(message = "通知单类型不能为空")
    private String type;

    /**
     * 计划开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date startTime;

    /**
     * 计划结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date endTime;

    /**
     * 是否魏桥磅单备注 0否/1是
     */
    private String isWeiqiaoPoundRemark;

    /**
     * 磅单备注
     */
    private String poundRemark;

    /**
     * 航次
     */
    private String voyage;
    /**
     * 作业港区 01东港02中港03西港
     */
    private String portCode;
    /**
     * 作业港区
     */
    private String portName;

    //转水前船名
    private String preChangeShipName;

    //转水前编号
    private String preChangeShipNo;
    /**
     * 来源：1 生产；2 渤海通
     */
    private String source;

    /**
     * 是否驳回：1 是；0 否
     */
    private Integer isReject;
    /**
     * 驳回人
     */
    private Long rejectBy;
    /**
     * 驳回人
     */
    private String rejectByName;
    /**
     * 驳回原因
     */
    private String rejectReason;
    /**
     * 驳回日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date rejectDate;
}

