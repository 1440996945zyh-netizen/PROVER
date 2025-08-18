package com.yy.ppm.statement.bean.dto.busHandoverlist;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-10-08 10:26
 */
@Setter
@Getter
public class TBusTrustDTO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 指令编号
     */
    private String trustNo;

    /**
     * 指令类型（1.装卸船2.集疏港3.倒运4.杂项/辅助）
     */
    private Integer trustType;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 客户ID，多选
     */
    private String customerId;

    /**
     * 客户名称，多选
     */
    private String customerName;

    /**
     * 作业过程代码
     */
    private String processCode;

    /**
     * 作业过程名称
     */
    private String processName;

    /**
     * 货种代码
     */
    private String cargoCategoryCode;

    /**
     * 货种名称
     */
    private String cargoCategoryName;

    /**
     * 贸别，内贸、外贸
     */
    private String tradeType;

    /**
     * 计划件数
     */
    private Integer planQuantity;

    /**
     * 计划重量
     */
    private Double planTon;

    /**
     * 作业要求
     */
    private String remark;

    /**
     * 航次ID
     */
    private Long shipvoyageId;

    /**
     * 中文船名
     */
    private String shipName;

    /**
     * 结算依据代码(字典:SETTLEMENT_ BASIS)
     */
    private String settlementBasisCode;

    /**
     * 结算依据名称（1.货物交接清单数、2.疏港过磅数、3.海关报关单数、4.集港过磅数、5.水尺数）
     */
    private String settlementBasisName;

    /**
     * 核销重量
     */
    private Double checkTon;

    /**
     * 核销数量
     */
    private Integer checkNumber;

    /**
     * 作业公司id
     */
    private Long companyId;

    /**
     * 作业公司NAME
     */
    private String companyName;

    /**
     * 状态，10：待审核20：已审核30：已发布：40：作业中50：核销
     */
    private Integer status;

    /**
     * 发布人
     */
    private Long releaseBy;

    /**
     * 发布人姓名
     */
    private String releaseByName;

    /**
     * 发布时间
     */

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date releaseTime;

    /**
     * 核销人
     */
    private Long checkBy;

    /**
     * 核销姓名
     */
    private String checkByName;

    /**
     * 核销时间
     */

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkTime;

    /**
     * 发布人
     */
    private Long examineBy;

    /**
     * 发布人姓名
     */
    private String examineByName;

    /**
     * 发布时间
     */

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date examineTime;

    /**
     * 货主
     */
    private String cargoOwnerName;

    /**
     * 货代
     */
    private String cargoAgentName;

    /**
     * 货名
     */
    private String cargoName;

    /**
     * 航次子表ID
     */
    private Long shipvoyageItemId;

    /**
     * 进出口(1进口 2 出口 3进出口)
     */
    private String impExp;

    /**
     * 作业伴随ID
     */
    private Long workAccompanyingId;

    /**
     * 是否有交接清单 0否/1是
     */
    private String isHaveHandoverlist;

    /**
     * 是否有交接清单Label
     */
    private String isHaveHandoverlistLabel;

    private String cargoNames;

    private String cargoOwnerNames;

    private String cargoInfoNo;
}
