package com.yy.ppm.business.bean.po;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-10-16 10:20
 */
@Setter
@Getter
public class TTruckDriver {

    private Integer id;
    private String driverName;
    private String driverIDNumber;
    private String driverPhone;
    private Integer type;
    private Integer delete_flag;
    private String creator;
    private Date createTime;
    private String editor;
    private Date editTime;
}
