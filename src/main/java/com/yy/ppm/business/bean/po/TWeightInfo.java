package com.yy.ppm.business.bean.po;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-10-26 19:31
 */
@Setter
@Getter
public class TWeightInfo {

    private Integer id;
    private String weightCode;
    private String amCode;
    private String planCode;
    private String IDNumber;
    private String QRcode;
    private String QRcode_md5;
    private String ICCode;
    private Date enter_time;
    private String platenumber;
    private String platenumber_recognized;
    private String weightbridge_code_1st;
    private String weightbridge_code_2nd;
    private BigDecimal weight_1st;
    private Date weight_1st_time;
    private Date weight_1st_uptime;
    private Date weight_1st_downtime;
    private Integer tallying_status;
    private String tallyman_1st;
    private String tallyman_2nd;
    private Date tallying_time;
    private Date tallying2_time;
    private String freight_yard_code;
    private BigDecimal weight_2nd;
    private Date weight_2nd_time;
    private Date weight_2nd_uptime;
    private Date weight_2nd_downtime;
    private BigDecimal expect_weight;
    private BigDecimal net_weight;
    private Date net_weight_time;
    private Integer print_control;
    private Integer print_count;
    private Date print_time;
    private Date print_time2;
    private Integer control_status;
    private Integer weight_status;
    private Integer uploaded;
    private Date uploadTime;
    private Integer invalid;
    private String picture_rec;
    private String picture_1;
    private String picture_2;
    private String picture_3;
    private String memo;
    //    private Integer synchronized;
    private String b1;
    private String b2;
    private String b3;
    private Integer truckplanid;
    private String whname;
    private String wqmemo;
    private String twshipname;
    private String twshipcode;
    private String truckPlaner;
    private String picture_01;
    private String picture_02;
    private String picture_03;
    private Integer moveAmId;
    private Date moveTime;
    private String moveUser;
    private String mark;
}
