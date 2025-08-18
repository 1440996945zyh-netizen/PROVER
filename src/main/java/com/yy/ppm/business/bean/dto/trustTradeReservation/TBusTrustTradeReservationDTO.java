package com.yy.ppm.business.bean.dto.trustTradeReservation;

import com.yy.ppm.business.bean.po.TBusTrustTradeReservatCarPO;
import com.yy.ppm.business.bean.po.TBusTrustTradeReservationPO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Author linqi
 * @Description
 * @Date 2023-07-05 16:17
 */
@Setter
@Getter
public class TBusTrustTradeReservationDTO extends TBusTrustTradeReservationPO {

    private List<TBusTrustTradeReservatCarPO> cars;
}
