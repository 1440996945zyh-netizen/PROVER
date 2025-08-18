package com.yy.ppm.master.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;

/**
 * @Auther linqi
 * @Description
 * @java.util.Date 2023-10-22 14:45
 */
@Setter
@Getter
public class MTrustTypePO extends BasePO {

    private Long id;
    private String trustType;
    private String isSelectShip;
    private String isEstimateAmount;
    private String isCreateCargo;
    private String mark;
    private String isSelectProcess;
    private String trustGroupType;
    private String shortType;
}
