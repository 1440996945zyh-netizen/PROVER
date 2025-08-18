package com.yy.ppm.business.bean.dto.contract;

import lombok.Getter;
import lombok.Setter;

/**
 * @Auther linqi
 * @Description 阶梯费率条目货物表
 * @Date 2023-11-08 11:41
 */
@Setter
@Getter
public class TBusTrateItemCargoDTO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 阶梯费率条目ID
     */
    private Long trateItemId;

    /**
     * 货物编码
     */
    private String cargoCode;

    /**
     * 货名
     */
    private String cargoName;
}
