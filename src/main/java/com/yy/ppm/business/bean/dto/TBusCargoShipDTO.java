package com.yy.ppm.business.bean.dto;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class TBusCargoShipDTO extends BasePO {
    private Long id;
    private String type;
    private Long cargoInfoId;
    private Long trustId;
    private Long shipVoyageItemId;
    private Long shipVoyageId;
    private String shipName;
    private String voyage;
}