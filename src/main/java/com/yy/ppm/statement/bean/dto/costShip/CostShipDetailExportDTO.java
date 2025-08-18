package com.yy.ppm.statement.bean.dto.costShip;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CostShipDetailExportDTO{

    /**
     * 序号
     */
    private Integer sortNum;
    /**
     * 付款人姓名
     */
    private String customerName;
    /**
     *  费用项目
     */
    private String feeItem;
    /**
     * 费用
     */
    private BigDecimal amount;
    /**
     *  计费依据
     */
    private String payBasis;
    /**
     * 备注
     */
    private String remark;

    private String specialNumber;


}
