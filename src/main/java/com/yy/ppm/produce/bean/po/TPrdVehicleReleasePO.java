package com.yy.ppm.produce.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TPrdVehicleReleasePO extends BasePO {
    //车号
    private Long id;
    //车号
    private String vehicleNo;
    //司机
    private String driverNameOne;
    //电话
    private String driverPhoneOne;
    //计划号
    private String planNo;

    private String subPlanNo;
    //货主
    private String consignorName;
    //货主code
    private String consignorCode;
    //货名code
    private String cargoCode;
    //货名
    private String cargoName;
    //作业位置
    private String yardCode;
    //计划开始时间
    private String planStartTime;
    //计划结束时间
    private String planEndTime;

    private String signTime;

    private String signCount;

    private String portName;

    //状态
    private String status;

    //进港状态
    private String hasInPort;
    /**
     * 审核人-ID
     */
    private Long approveBy;

    /**
     * 审核人-姓名
     */
    private String approveByName;

    /**
     * 审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date approveTime;
}
