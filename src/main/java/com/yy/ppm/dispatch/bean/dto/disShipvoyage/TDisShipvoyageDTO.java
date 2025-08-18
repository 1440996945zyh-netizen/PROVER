package com.yy.ppm.dispatch.bean.dto.disShipvoyage;

import com.yy.ppm.dispatch.bean.po.TDisShipvoyageItemPO;
import com.yy.ppm.dispatch.bean.po.TDisShipvoyagePO;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author linqi
 * @Description
 * @Date 2023-07-04 11:31
 */
@Setter
@Getter
public class TDisShipvoyageDTO extends TDisShipvoyagePO {

    private TDisShipvoyageItemPO in;

    private TDisShipvoyageItemPO out;

    /**
     * 舱口数
     */
    private String hatchNum;
    private String nationCode;
    private String shipLength;
    private String shipWidth;
    private String dwt;
    private String totalWeight;
    private String netWeight;
    private String shipKindName;
    private String mmsi;

    private String firCargoCate;
    private String secCargoCate;
    private String isFocusShip;




}
