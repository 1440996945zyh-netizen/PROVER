package com.yy.ppm.statement.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 结算单子表(TCostStatementDetail)PO
 *
 * @author linqi
 * @since 2023-09-07 10:41:12
 */
@Setter
@Getter
public class TCostStatementDetailPO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 结算单ID
     */
    private Long statement;

    /**
     * 作业合同ID
     */
    private Long contractId;

    /** 合同费率ID **/
    private Long contractRateId;

    /**
     * 费目代码
     */
    private String rateItemCode;

    /**
     * 费目名称
     */
    private String rateItemName;

    /**
     * 服务内容id
     */
    @NotNull(message = "服务内容ID不能为空")
    private Long serviceContentId;

    /**
     * 服务内容名称
     */
    @NotBlank(message = "服务内容名称不能为空")
    private String serviceContentName;

    /**
     * 作业过程代码
     */
    private String processCode;

    /**
     * 作业过程名称
     */
    private String processName;

    /**
     * 费率值
     */
    private BigDecimal rate;

    /**
     * 计费单位代码(字典：UNIT)
     */
    private String unitCode;

    /**
     * 计费单位名称
     */
    private String unitName;

    /**
     * 计费数量
     */
//    @NotNull(message = "计费数量不能为空")
    private BigDecimal number;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 税率值
     */
    private BigDecimal tax;

    /**
     * 税额
     */
    private BigDecimal taxAmount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 已开票数量
     */
    private BigDecimal invoiceNumber;

    /***
     * 已开票金额
     */
    private BigDecimal invoiceAmount;

    /**
     * 结算单类型为30船方计费/40杂项计费/50堆存费时非空
     */
    private Long businessId;


    /**
     * 费率ID
     */
    private Long rateId;

    /**
     * 计费数量2（船舶净吨）
     */
    private BigDecimal number2;

    private String isDerived;

    private Long trateItemId;

    private BigDecimal preferentialRate;

    private Long trateId;

    /**
     * 场地类型编码，字典STORAGE_YARD_TYPE
     */
    private String storageYardTypeCode;

    /**
     * 场地类型名称，字典STORAGE_YARD_TYPE
     */
    private String storageYardTypeName;

    /**
     * 前沿是否落地 0否/1是
     */
    private String isCuttingEdgeLanding;
    private String isDump;//是否自卸车
}

