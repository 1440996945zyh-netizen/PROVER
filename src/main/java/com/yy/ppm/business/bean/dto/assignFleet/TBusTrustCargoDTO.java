package com.yy.ppm.business.bean.dto.assignFleet;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Author linqi
 * @Description
 * @Date 2023-07-05 10:14
 */
@Setter
@Getter
public class TBusTrustCargoDTO {

    /**
     * 主键ID
     */
    private Long tbtcId;

    /**
     * 指令ID
     */
    private Long tbtcTrustId;

    /**
     * 票货ID
     */
    private Long tbtcCargoInfoId;

    /**
     * 方向（1源，2目标）
     */
    private String tbtcDirection;

    /**
     * 计划件数
     */
    private Integer tbtcQuantity;

    /**
     * 计划重量
     */
    private BigDecimal tbtcTon;

    /**
     * 货主id
     */
    private Long tbtcCargoOwnerId;

    /**
     * 货主名称
     */
    private String tbtcCargoOwnerName;

    /**
     * 货代id
     */
    private Long tbtcCargoAgentId;

    /**
     * 货代名称
     */
    private String tbtcCargoAgentName;

    /**
     * 货物编码
     */
    private String tbtcCargoCode;

    /**
     * 货名
     */
    private String tbtcCargoName;

    /**
     * 包装编码
     */
    private String tbtcPackingCode;

    /**
     * 包装名称
     */
    private String tbtcPackingName;

    private Long tbtcCreateBy;

    private String tbtcCreateByName;

    private Date tbtcCreateTime;

    private Long tbtcUpdateBy;

    private String tbtcUpdateByName;

    private Date tbtcUpdateTime;

    /**
     * 主键ID
     */
    private Long tbtId;

    /**
     * 指令编号
     */
    private String tbtTrustNo;

    /**
     * 指令类型（1.装卸船2.集疏港3.倒运4.杂项/辅助）
     */
    private Integer tbtTrustType;

    /**
     * 合同ID
     */
    private Long tbtContractId;

    /**
     * 客户ID，多选
     */
    private String tbtCustomerId;

    /**
     * 客户名称，多选
     */
    private String tbtCustomerName;

    /**
     * 作业过程代码
     */
    private String tbtProcessCode;

    /**
     * 作业过程名称
     */
    private String tbtProcessName;

    /**
     * 货种代码
     */
    private String tbtCargoCategoryCode;

    /**
     * 货种名称
     */
    private String tbtCargoCategoryName;

    /**
     * 贸别，内贸、外贸
     */
    private String tbtTradeType;

    /**
     * 计划件数
     */
    private Integer tbtPlanQuantity;

    /**
     * 计划重量
     */
    private BigDecimal tbtPlanTon;

    /**
     * 作业要求
     */
    private String tbtRemark;

    /**
     * 航次ID
     */
    private Long tbtShipvoyageId;

    /**
     * 航次子表ID
     */
    private Long tbtShipvoyageItemId;

    /**
     * 中文船名
     */
    private String tbtShipName;

    /**
     * 结算依据代码(字典:SETTLEMENT_ BASIS)
     */
    private String tbtSettlementBasisCode;

    /**
     * 结算依据名称（1.货物交接清单数、2.疏港过磅数、3.海关报关单数、4.集港过磅数、5.水尺数）
     */
    private String tbtSettlementBasisName;

    /**
     * 核销重量
     */
    private BigDecimal tbtCheckTon;

    /**
     * 核销数量
     */
    private Integer tbtCheckNumber;

    /**
     * 作业公司id
     */
    private Long tbtCompanyId;

    /**
     * 作业公司NAME
     */
    private String tbtCompanyName;

    /**
     * 状态，10：待审核20：已审核30：已发布：40：作业中50：核销
     */
    private Integer tbtStatus;

    /**
     * 发布人
     */
    private Long tbtReleaseBy;

    /**
     * 发布人姓名
     */
    private String tbtReleaseByName;

    /**
     * 发布时间
     */
    private Date tbtReleaseTime;

    /**
     * 核销人
     */
    private Long tbtCheckBy;

    /**
     * 核销姓名
     */
    private String tbtCheckByName;

    /**
     * 核销时间
     */
    private Date tbtCheckTime;

    /**
     * 审核人
     */
    private Long tbtExamineBy;

    /**
     * 审核人姓名
     */
    private String tbtExamineByName;

    /**
     * 审核时间
     */
    private Date tbtExamineTime;

    private Long tbtCreateBy;

    private String tbtCreateByName;

    private Date tbtCreateTime;

    private Long tbtUpdateBy;

    private String tbtUpdateByName;

    private Date tbtUpdateTime;

    /**
     * 主键ID
     */
    private Long tbciId;

    /**
     * 航次ID
     */
    private Long tbciShipvoyageId;

    /**
     * 航次子表ID
     */
    private Long tbciShipvoyageItemId;

    /**
     * SCN
     */
    private String tbciScn;

    /**
     * 船名
     */
    private String tbciShipName;

    /**
     * 票货号（自动生成，如果是货权转移生成的在原票货号上追加序号）
     */
    private String tbciCargoInfoNo;

    /**
     * 货主ID
     */
    private Long tbciCargoOwnerId;

    /**
     * 货主名称
     */
    private String tbciCargoOwnerName;

    /**
     * 货代ID
     */
    private Long tbciCargoAgentId;

    /**
     * 货代名称
     */
    private String tbciCargoAgentName;

    /**
     * 货物代码
     */
    private String tbciCargoCode;

    /**
     * 货物名称
     */
    private String tbciCargoName;

    /**
     * 贸别，内贸、外贸
     */
    private String tbciTradeType;

    /**
     * 包装代码（字典：PACKING）
     */
    private String tbciPackingCode;

    /**
     * 包装名称
     */
    private String tbciPackingName;

    /**
     * 作业公司ID
     */
    private Long tbciCompanyId;

    /**
     * 作业公司NAME
     */
    private String tbciCompanyName;

    /**
     * 父ID
     */
    private Long tbciParentId;

    /**
     * 根票货ID
     */
    private Long tbciRootId;

    /**
     * 件数
     */
    private Integer tbciQuantity;

    /**
     * 重量
     */
    private BigDecimal tbciTon;

    /**
     * 货权量
     */
    private BigDecimal tbciRightsQuantity;

    /**
     * 剩余货权量
     */
    private BigDecimal tbciSurplusRightsQuantity;

    /**
     * 是否完货 0：否 1：是
     */
    private String tbciIsClear;

    /**
     * 清场人-ID
     */
    private Long tbciClearBy;

    /**
     * 清场人-姓名
     */
    private String tbciClearByName;

    /**
     * 清场日期
     */
    private Date tbciClearDate;

    private Long tbciCreateBy;

    private String tbciCreateByName;

    private Date tbciCreateTime;

    private Long tbciUpdateBy;

    private String tbciUpdateByName;

    private Date tbciUpdateTime;

    /**
     * 航次
     */
    private String tdsiVoyage;

    /**
     * 船名航次
     */
    private String shipNameVoyage;

    /**
     * 已指派物流车队名称
     */
    private String customerNames;

    private List<TBusAssignFleetDTO> assignFleets;
}
