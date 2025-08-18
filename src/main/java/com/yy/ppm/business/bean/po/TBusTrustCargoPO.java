package com.yy.ppm.business.bean.po;


import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName (TBusTrustCargo)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月05日 09:21:00
 */
@Setter
@Getter
public class TBusTrustCargoPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 641976268656174849L;

    /** 主键ID */
    private Long id;
    /** 指令ID */
    private Long trustId;
    /** 票货ID */
    private Long cargoInfoId;
    /** 方向（1源，2目标） */
    private String direction;
    /** 计划件数 */
    private Long quantity;
    /** 计划重量 */
    private BigDecimal ton;
    /** 货主ID */
    private Long cargoOwnerId;
    /** 货主名称 */
    private String cargoOwnerName;
    /** 货代ID */
    private Long cargoAgentId;
    /** 货代名称 */
    private String cargoAgentName;
    /** 货物代码 */
    private String cargoCode;
    /** 货物名称 */
    private String cargoName;
    /** 包装代码（字典：PACKING） */
    private String packingCode;
    /** 包装名称 */
    private String packingName;
    /** 渤海通ID */
    private Integer bhtId;
    /** 作业伴随ID */
    private Long workAccompanyingId;
    /** 合同ID **/
    private Long contractId;
    /** 合同费率ID **/
    private Long contractRateId;
    /** 费率 **/
    private BigDecimal rate;
    /** 预估金额 **/
    private BigDecimal estAmount;
    /** 合同编号 **/
    private String contractName;

    /**
     * 业务号（指令编号+两位流水）
     */
    private String businessNo;

    /**
     * 舱口号
     */
    private String hatchs;

    /**
     * 是否二次过磅 0否/1是
     */
    private String isSecondWeigh;

    /**
     * 打印次数是否有效 0否/1是
     */
    private String printPoundId;

    /**
     * 打印次数
     */
    private Integer printPoundNum;

    /**
     * 内倒类型
     */
    private String innertransportType;

    /**
     * 费率LABEL（服务内容名称:费率值）
     */
    private String rateLabel;
    /**
     * 内倒过磅顺序，1先轻后重2先重后轻
     */
    private String transportType;
    /**
     * 空车重量不高于
     */
    private BigDecimal emptyWeight;
    /**
     * 重车重量不低于
     */
    private BigDecimal heavyWeight;
    /**
     * 是否启用内倒规则
     */
    private String isRule;

    /** 委托人id **/
    private Long consignerId;
    /** 委托人名称 **/
    private String consignerName;

    private String status;

    /**
     * 提运单id
     */
    private Long orderItemId;

    /**
     * 提运单号
     */
    private String billNo;


}

