package com.yy.ppm.statement.bean.dto.storageSettleMix;

import com.yy.ppm.dispatch.bean.po.TDisShipvoyageItemPO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TDisShipvoyageItemDTO extends TDisShipvoyageItemPO {

    /**
     * 船名
     */
    private String shipName;
}
