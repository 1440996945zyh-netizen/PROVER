package com.yy.ppm.produce.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@EqualsAndHashCode
public class TTruckLogPO implements Serializable {

    private static final long serialVersionUID = -6734686200034099011L;
    /**
     * ID
     */
    private String id;

    /**
     * 车牌号
     */
    private String truckNo;

    /**
     * 节点
     */
    private String pointName;

    /**
     * 节点时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date pointTime;

    /**
     * 操作人
     */
    private String operName;

    /**
     * 位置
     */
    private String position;

    /**
     * 计划号
     */
    private String planNo;

    /**
     * 身份证号
     */
    private String idNumber;

    /**
     * 标记
     */
    private String flag;

    /**
     * 状态
     */
    private String status;

    /**
     * 业务ID
     */
    private String businessId;

    /**
     * 登录用户名
     */
    @JsonIgnore
    private Long loginUserId;

    /**
     * 登录用户名
     */
    @JsonIgnore
    private String loginUserName;

    /**
     * 登录用户类型
     */
    @JsonIgnore
    private String loginUserType;

    /**
     * 登录时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonIgnore
    private Date now;
}

