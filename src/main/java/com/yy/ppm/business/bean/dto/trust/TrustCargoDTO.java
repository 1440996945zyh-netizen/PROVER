package com.yy.ppm.business.bean.dto.trust;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-10-18 21:36
 */
@Setter
@Getter
public class TrustCargoDTO {

    @NotNull(message = "指令票货ID不能为空")
    private Long id;

    private Long quantity;

    private BigDecimal ton;

    private BigDecimal estAmount;

    private String isSecondWeigh;

    private String printPoundId;

    private Integer printPoundNum;
    //提运单号
    private String deliveryNumbers;

    /**
     * 委托人id
     */
    private Long consignerId;
    /**
     * 委托人姓名
     */
    private String consignerName;
    /**
     * 票货号
     */
    private String cargoInfoNo;
    /**
     * 集疏港类型
     */
    private String type;

    private String _status;
    private Long cargoInfoId;
}
