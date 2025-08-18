package com.yy.ppm.business.bean.dto.trustTradeReservation;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author linqi
 * @Description
 * @Date 2023-07-10 16:43
 */
@Setter
@Getter
public class TBusTrustTradeReservationQueryDTO {

    /**
     * 航次子表id
     */
    private Long shipvoyageItemId;

    /**
     * 货主id
     */
    private Long cargoOwnerId;

    /**
     * 货代id
     */
    private Long cargoAgentId;

    /**
     * 货物编码
     */
    private String cargoCode;
}
