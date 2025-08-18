package com.yy.ppm.business.bean.po;

import lombok.Getter;
import lombok.Setter;

/**
 * @Auther linqi
 * @Description 阶梯费率客户表
 * @Date 2023-11-08 11:38
 */
@Setter
@Getter
public class TBusTrateCustomerPO {

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
    private Long customerId;

    /**
     * 客户名称
     */
    private String customerName;
}
