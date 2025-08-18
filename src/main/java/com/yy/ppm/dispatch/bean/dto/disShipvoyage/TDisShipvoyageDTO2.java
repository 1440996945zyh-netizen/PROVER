package com.yy.ppm.dispatch.bean.dto.disShipvoyage;

import java.math.BigDecimal;

import com.yy.ppm.dispatch.bean.po.TDisShipvoyageItemPO;
import com.yy.ppm.dispatch.bean.po.TDisShipvoyagePO;

import lombok.Getter;
import lombok.Setter;
/**
 * @Author SunQi
 * @Description
 * @Date 2023-07-04 11:31
 */
@Setter
@Getter
public class TDisShipvoyageDTO2 extends TDisShipvoyagePO {

    private TDisShipvoyageItemPO in;

    /**
     * 子表数据
     */
    private TDisShipvoyageItemPO tDisShipvoyageItemPO;

    private BigDecimal cargoNum;

    private TDisShipvoyageItemPO out;
}
