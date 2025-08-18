package com.yy.ppm.business.bean.dto.contract;

import lombok.Getter;
import lombok.Setter;

/**
 * @Auther linqi
 * @Description 阶梯费率合同表
 * @Date 2023-11-08 11:37
 */
@Setter
@Getter
public class TBusTrateContractDTO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 阶梯费率ID
     */
    private Long trateId;

    /**
     * 合同编号
     */
    private String contractNo;
}
