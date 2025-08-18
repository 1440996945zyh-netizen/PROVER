package com.yy.ppm.produce.bean.dto;

import com.yy.ppm.produce.bean.po.TPrdVehicleReleasePO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TPrdVehicleReleaseDTO extends TPrdVehicleReleasePO {

    /**
     * 计划号
     */
    private String planNo;

    private String subPlanNo;

    /**
     * 车号
     */
    private String vehicleNo;

    /**
     * 货主
     */
    private String consignorName;

}
