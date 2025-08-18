package com.yy.ppm.business.bean.po;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-10-17 15:11
 */
@Setter
@Getter
public class TTruckplan {

    private Long id;
    private String planCode;
    private String truckNumber;
    private String driverName;
    private String driverIDNumber;
    private String driverPhone;
    private String truckPlaner;
    private Date truckPlanTime;
    private Integer disable;
    private Integer delete_flag;
    private Integer truckType;
    private Integer planType;
    private String tpmemo;
    private String memo;
    private String wqshipname;
    private String wqshipcode;
}
