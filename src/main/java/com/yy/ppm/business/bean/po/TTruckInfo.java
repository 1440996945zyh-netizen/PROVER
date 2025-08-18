package com.yy.ppm.business.bean.po;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-10-16 10:24
 */
@Setter
@Getter
public class TTruckInfo {

    private Integer id;
    private String truckNumber;
    private Integer driverId;
    private Integer delete_flag;
    private Integer type;
    private String cardCode;
    private String creator;
    private Date createTime;
    private String editor;
    private Date editTime;
}
