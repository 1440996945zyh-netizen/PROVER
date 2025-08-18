package com.yy.ppm.customs.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TCustomsDTO {

    private String id;

    private String informNo;

    private String businessNo;

    private String createTime;

    private String billType;

    private String cargoInfoNo;

    private String startTime;

    private String endTime;

    private String scn;

    private String shipName;

    private String voyage;

    private String tradeType;

    private String cargoOwnerName;

    private String cargoAgentName;

    private String cargoName;

    private String packingName;

    private String quantity;

    private String ton;

    private String storehouseName;

    private String regionName;

    private List<TDriverDTO> driverList;
}
