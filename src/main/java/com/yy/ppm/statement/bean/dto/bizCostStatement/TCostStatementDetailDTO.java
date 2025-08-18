package com.yy.ppm.statement.bean.dto.bizCostStatement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-18 16:38
 */
@Setter
@Getter
public class  TCostStatementDetailDTO extends BasePO {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空")
    private Long id;

    /**
     * 结算单ID
     */
    private Long statement;

    /**
     * 作业合同ID
     */
    @NotNull(message = "作业合同ID不能为空")
    private Long contractId;

    /**
     * 费目代码
     */
    @NotBlank(message = "费目代码不能为空")
    private String rateItemCode;

    /**
     * 费目名称
     */
    @NotBlank(message = "费目名称不能为空")
    private String rateItemName;

    /**
     * 服务内容id
     */
    @NotNull(message = "服务内容id不能为空")
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
    @NotNull(message = "费率不能为空")
    private BigDecimal rate;

    /**
     * 计费单位代码(字典：UNIT)
     */
    @NotBlank(message = "计费单位代码不能为空")
    private String unitCode;

    /**
     * 计费单位名称
     */
    @NotBlank(message = "计费单位名称不能为空")
    private String unitName;

    /**
     * 计费数量
     */
    private BigDecimal number;
    private BigDecimal number2;

    /**
     * 结算单金额
     */
    @NotNull(message = "金额不能为空")
    private BigDecimal amount;
    /**
     * 计费金额
     */
    private BigDecimal amountjf;
    /**
     * 折扣金额
     */
    private BigDecimal amountzk;

    /**
     * 税率值
     */
    @NotNull(message = "税率不能为空")
    private BigDecimal tax;

    /**
     * 税额
     */
    @NotNull(message = "税额不能为空")
    private BigDecimal taxAmount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 结算单类型Label
     */
    private String typeLabel;

    /**
     * 费率id
     */
    @NotNull(message = "费率ID不能为空")
    private Long rateId;

    /**
     * 是否来自阶梯费率拆分 0否/1是
     */
    private String isDerived;

    /**
     * 阶梯费率条目ID
     */
    private Long trateItemId;

    /**
     * 优惠费率
     */
    private BigDecimal preferentialRate;

    private String storageYardTypeCode;

    private String storageYardTypeName;

    private String isCuttingEdgeLanding;
    private String isDump;

    /**
     * 临时费率展示
     */
    private BigDecimal tempRate;

    /**
     * 导出excel用
     */
    private String feeName;

    /**
     * 船名航次 导出excel用
     */
    private String shipNameVoyage;
    /**
     * 结算方式 导出excel用
     */
    private String settlementBasisName;
    /**
     * huoming
    */
    private String cargoName;
    private String cargoCategoryName;
    private BigDecimal costDays;

    private String contactNo;

    private String statementName;
    private String reviewName;
    private String confirmName;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date reviewTime;
    private BigDecimal invoiceNumber;
    private BigDecimal invoiceAmount;
    // 收款金额
    private BigDecimal utilizedAmount;

    private String startPositionName;
    private String endPositionName;

    /**
     * 是否最终结算 0否/1是
     */
    @NotBlank(message = "是否最终结算不能为空")
    private String isFinal;
    private String companyName;
}