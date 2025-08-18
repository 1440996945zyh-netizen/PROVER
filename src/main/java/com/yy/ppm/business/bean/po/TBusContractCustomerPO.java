package com.yy.ppm.business.bean.po;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-10-12 15:23
 */
@Setter
@Getter
public class TBusContractCustomerPO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 客户id
     */
    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    /**
     * 客户名称
     */
    @NotBlank(message = "客户名称不能为空")
    private String customerName;
}
