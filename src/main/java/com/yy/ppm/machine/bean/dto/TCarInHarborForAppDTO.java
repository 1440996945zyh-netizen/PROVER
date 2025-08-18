package com.yy.ppm.machine.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.page.PageParameter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TCarInHarborForAppDTO extends PageParameter  implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -4292983992705760507L;

    /**
     * 作业指令id
     */
    private Long workPlanId;

    /**
     * 当前作业数据id
     */
    private Long macWorkId;

    /**
     * 票货ID
     */
    private Long cargoInfoId;

    /**
     * 机械code
     */
    private String macCode;

    /**
     * SCN
     */
    private String scn;

    /**
     * 航次ID
     */
    private Long shipvoyageId;

    /**
     * 待作业车号
     */
    private String workMacName;

    /**
     * 船名_航次
     */
    private String shipName;

    /**
     * 源垛位
     */
    private String massNamesSource;

    /**
     * 目标垛位
     */
    private String massNamesTarget;

    /**
     * 货名
     */
    private String cargoCode;
    private String cargoName;

    /**
     * 计划类型
     */
    private String planType;

    /**
     * 车辆入港时间
     */
    private Date inPortTime;

    /**
     * 车辆入港时间
     */
    private Date outPortTime;

    /**
     * 车辆入港状态（10：未超1小时，20：超过1小时）
     */
    private String inPortStatus;

    /**
     * 作业开始时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date workTimeStart;

    /**
     * 作业结束时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date workTimeEnd;

    /**
     * 车辆作业状态（10：未作业，20：开始作业， 30：结束作业）
     */
    private String workTimeStatus;

    /**
     * 磅单id
     */
    private Long weighbridgeId;

    /**
     * 设备id
     */
    private Long macId;

    /**
     * 指令票货id
     */
    private Long trustCargoInfoId;

    /**
     * 货主
     */
    private String cargoOwnerName;

    /**
     * 磅单备注
     */
    private String poundRemark;

    /**
     * 入港时间（一次磅时间）
     */
    private String weighInDt;

    private String carNum;
    /**
     * 计划号
     */
    private String planNo;
    /**
     * 日期
     */
    private String workDate;
    /**
     * 班次
     */
    private String classCode;
    /**
     * 理货员
     */
    private String tallyName;
    /**
     * 在港口时长
     */
    private String minutes;

    /**
     * 是否离港
     */
    private String isLeave;


    private String statusLabel;


    private String portCode;

    private String portName;

    private int sortNum;

    private String driverNoOne;
    private String driverNameOne;
    private String driverPhoneOne;

    private String packingCode;
    private String packingName;
    private String location;

    private String tallyId;
    private  String tallRemark;
    private String tytDelFlag;

    private String cargoInfoNo;
}
