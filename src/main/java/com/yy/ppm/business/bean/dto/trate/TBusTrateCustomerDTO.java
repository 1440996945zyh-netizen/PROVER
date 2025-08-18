package com.yy.ppm.business.bean.dto.trate;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @Auther linqi
 * @Description 阶梯费率客户表
 * @Date 2023-11-08 11:38
 */
@Setter
@Getter
public class TBusTrateCustomerDTO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 阶梯费率ID
     */
    private Long trateId;

    /**
     * 客户ID
     */
    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    /**
     * 客户名称
     */
    @NotBlank(message = "客户名称不能为空")
    private String customerName;
}
