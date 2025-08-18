package com.yy.ppm.dispatch.bean.po;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class TDisLeavePortCostShipPO  {

    /**
     * 航次ID
     */
    private Long shipvoyageId;
    /**
     * 数量
     */
    private BigDecimal number;

    /**
     * 数量2（船舶净吨）
     */
    private Integer number2;

    /**
     * 费目编码
     */
    private String rateItemCode;

    /**
     * 费目名称
     */
    private String rateItemName;

    /**
     * 费率
     */
    private BigDecimal rate;

    /**
     * 税率
     */
    private BigDecimal tax;

    /**
     * 计费单位编码
     */
    private String unitCode;

    /**
     * 计费单位名称
     */
    private String unitName;

    /**
     * 金额
     */

    private BigDecimal amount;

    /**
     * 税额
     */
    private BigDecimal taxAmount;
    /**
     * 备注
     */
    private String remark;


}
