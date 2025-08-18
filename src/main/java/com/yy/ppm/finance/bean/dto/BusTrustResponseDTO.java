package com.yy.ppm.finance.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.business.bean.po.TBusAssignFleetPO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class BusTrustResponseDTO {
    private static final long serialVersionUID = -73695459173569293L;

    /***
     * 作业通知单编号
     */
    private String trustNo;

    /**
     * 作业通知单主表iD BUS_TRUST_ID
     */
    private Long busTrustId;
    /**
     * 票货表ID BUS_TRUST_CARGO_ID
     */
    private Long busTrustCargoId;

    /** 主键ID */
    private Long id;
    /** 指令类型 指令带过来 */
    @NotBlank(message = "作业类型编码不能为空")
    private String trustType;
    /** 合同ID */
    private Long contractId;
    /** 客户ID，多选 */
    private Long customerId;
    /** 客户名称，多选 */
    private String customerName;
    /** 作业过程代码 */
    @NotBlank(message = "操作过程编码不能为空")
    private String processCode;
    /** 作业过程名称 */
    @NotBlank(message = "操作过程名称不能为空")
    private String processName;
    /** 货种代码 */
    private String cargoCategoryCode;
    /** 货种名称 */
    private String cargoCategoryName;
    /** 货主 */
    private  String cargoOwnerName;
    //货主ID
    private  String cargoOwnerId;
    /** 货代 */
    private String cargoAgentName;
    /** 货名 */
    private String cargoName;
    private String shipNameVoyage;

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

    /**进出口
     *
     */
    private String impExp;
    /**
     * 源
     */
    private String sourceCd;


    /**
     * 显示下拉框
     */
    private String label;

    //预估金额
    private BigDecimal ESTAmount;
    //作业通知单id
    private Long trustId;

    private int tmpNumber;
    /**
     * 票货ID
     */
    private Long cargoInfoId;

}
