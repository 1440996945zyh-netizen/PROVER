package com.yy.ppm.statement.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * (TCostShip)PO
 *
 * @author linqi
 * @since 2023-09-20 11:36:36
 */
@Setter
@Getter
public class TCostShipPO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 10停泊费/20加水接电费
     */
    @NotBlank(message = "类型不能为空")
    private String type;

    /**
     * 船舶预报ID，类型为停泊费时非空
     */
    private Long shipvoyageId;

    /**
     * 船舶航次ID，类型为停泊费时非空
     */
    private Long shipvoyageItemId;

    /**
     * 指令ID，类型为加水接电费时非空
     */
    private Long trustId;

    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    private BigDecimal number;

    /**
     * 数量2（船舶净吨）
     */
    private Integer number2;

    /**
     * 费目编码
     */
    @NotBlank(message = "费目编码不能为空")
    private String rateItemCode;

    /**
     * 费目名称
     */
    @NotBlank(message = "费目名称不能为空")
    private String rateItemName;

    /**
     * 费率
     */
    @NotNull(message = "费率不能为空")
    private BigDecimal rate;

    /**
     * 税率
     */
    @NotNull(message = "税率不能为空")
    private BigDecimal taxRate;

    /**
     * 计费单位编码
     */
    @NotBlank(message = "计费单位编码不能为空")
    private String unitCode;

    /**
     * 计费单位名称
     */
    @NotBlank(message = "计费单位名称不能为空")
    private String unitName;

    /**
     * 金额
     */
    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    /**
     * 税额
     */
    @NotNull(message = "税额不能为空")
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
    @NotNull(message = "作业公司ID不能为空")
    private Long companyId;

    /**
     * 作业公司名称
     */
    @NotBlank(message = "作业公司名称不能为空")
    private String companyName;

    /**
     * 付款人ID
     */
    @NotNull(message = "付款人ID不能为空")
    private Long customerId;

    /**
     * 付款人姓名
     */
    @NotBlank(message = "付款人姓名不能为空")
    private String customerName;


    /**
     * 费率id
     */
    private Long rateId;
}

