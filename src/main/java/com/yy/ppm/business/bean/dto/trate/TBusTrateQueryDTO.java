package com.yy.ppm.business.bean.dto.trate;

import lombok.Getter;
import lombok.Setter;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-11-08 16:41
 */
@Setter
@Getter
public class TBusTrateQueryDTO {

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 客户ID
     */
    private Long customerId;
}
