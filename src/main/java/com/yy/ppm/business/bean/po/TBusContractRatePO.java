package com.yy.ppm.business.bean.po;


import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName 合同费率表(TBusContractRate)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月29日 10:49:00
 */
@Data
public class TBusContractRatePO extends BasePO implements Serializable {

    private static final long serialVersionUID = 174466643714599312L;

        /** 主键ID */
    private Long id;
            /** 合同ID */
    private Long contractId;
            /** 货物代码 */
    private String cargoCode;
            /** 货物名称 */
    private String cargoName;
            /** 费目代码 */
    @NotBlank(message = "费目编码不能为空")
    private String rateItemCode;
            /** 费目名称 */
    @NotBlank(message = "费目名称不能为空")
    private String rateItemName;
            /** 贸别，内贸、外贸 */
    private String tradeType;
            /** 服务内容id */
    private Long serviceContentId;
            /** 服务内容名称 */
    private String serviceContentName;
            /** 账期类型（字典:PAYMENT_TYPE） */
    private String paymentTypeCode;
            /** 账期类型NAME(发票、结算、次月) */
    private String paymentTypeName;
            /** 账期天数 */
    private Long paymentDays;
            /** 费率值 */

    @NotNull(message = "费率值不能为空")
    private BigDecimal rate;
            /** 税率值 */
    @NotNull(message = "税率值不能为空")
    private BigDecimal tax;
            /** 计费单位代码（字典：UNIT） */
    @NotBlank(message = "计费单位代码不能为空")
    private String unitCode;
            /** 计费单位名称 */
    @NotBlank(message = "计费单位名称不能为空")
    private String unitName;

    /**
     * 费率类型 10货物费率/20服务费率
     **/
    @NotBlank(message = "费率类型不能为空")
    private String type;

    /**
     * 费率ID
     **/
    @NotNull(message = "费率ID不能为空")
    private Long rateId;

    /**
     * 免堆存期，仅堆存费有
     */
    private Integer freeStorageDays;

    /**
     * 阶梯费率条目ID，仅包干费有
     */
    private Long trateItemId;

    private String processCode;

    private String processName;
}

