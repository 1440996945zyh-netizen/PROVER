package com.yy.ppm.business.bean.dto.trate;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

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
    @NotBlank(message = "货物编码不能为空")
    private String cargoCode;

    /**
     * 货名
     */
    @NotBlank(message = "货名不能为空")
    private String cargoName;
}
