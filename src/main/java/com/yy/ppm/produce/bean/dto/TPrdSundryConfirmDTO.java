package com.yy.ppm.produce.bean.dto;

import lombok.Data;

@Data
public class TPrdSundryConfirmDTO {
    private String planNo;
    private String workTypeName;
    private String customerName;
    private String shipName;
    private String cargoName;
    private String truckPlate;
    private String driver;
    private String idNumber;
    private String tel;
    private String isTally;
    private String createByName;
    private String createTime;
    private Long noteId;
}
