package com.yy.ppm.business.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 作业指令表(TBusTrust)SearchDTO
 * @Description TODO
 * @createTime 2023年07月05日 09:21:00
 */
@Data
public class TBusTrustSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -63505224967815201L;

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
    private String trustType;
    /**
     * 合同ID
     */
    private Long contractId;
    /**
     * 客户ID
     */
    private Long cargoOwnerId;
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
    private Long planQuantity;
    /**
     * 计划重量
     */
    private BigDecimal planTon;
    /**
     * 作业要求
     */
    private String remark;
    /**
     * 航次ID
     */
    private Long shipvoyageId;
    /**
     * 航次子表ID
     */
    private Long shipvoyageItemId;
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
    private BigDecimal checkTon;
    /**
     * 核销数量
     */
    private BigDecimal checkNumber;
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
    private String status;
    /**
     * 创建者-ID
     */
    private Long createBy;
    /**
     * 创建者-姓名
     */
    private String createByName;
    /**
     * 更新者-姓名
     */
    private String updateByName;
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
    private Date examineTime;

    /**
     * 票货编号
     */
    private String cargoInfoNo;
    /**
     * 货名
     */
    private String cargoName;

    /**
     * 货物代码
     */
    private String cargoCode;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date beginDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    private String consignerType;

    private String businessNo;

    private String voyage;

    /**
     * 合同号
      */
    private String contractCode;

    private String statusLabel;
}

