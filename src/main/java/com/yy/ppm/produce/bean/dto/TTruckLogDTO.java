package com.yy.ppm.produce.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yy.common.page.PageParameter;
import com.yy.ppm.produce.bean.dto.workTicket.TPrdWorkTicketDetailDTO;
import com.yy.ppm.produce.bean.po.TTruckLogPO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Auther wangxd
 * @Description
 * @Date 2023-11-17 20:28
 */
@Setter
@Getter
public class TTruckLogDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -2579207412322054679L;
    /**
     * 节点时间
     */
    private String pointTimeStart;
    /**
     * 节点时间
     */
    private String pointTimeEnd;


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

    //过磅类型
    private String weightType;
    //计划类型
    private String planType;
    //过磅吨数
    private String ton;
    //备注
    private String remark;


}
