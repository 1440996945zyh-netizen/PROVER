package com.yy.ppm.dispatch.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 
 * @author admin
 *
 */
@Getter
@Setter
@ToString
public class TAnchApplyPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -16363434437183329L;

    /**
     * 主键
     */
    private Long id;
    /**
     * 船舶ID
     */
    private Long shipId;
    /**
     * 船名
     */
    private String shipName;
    /**
     * MMSI
     */
    private String mmsi;
    /**
     * 锚地ID
     */
    private Long anchId;
    /**
     * 锚地名称
     */
    private String anchName;
    /**
     * 锚位ID
     */
    private Long positionId;
    /**
     * 锚位
     */
    private String position;
    /**
     * 船舶吃水（米）
     */
    private double draft;
    /**
     * 0本港船1过境船
     */
    private Integer thisPort;
    /**
     * 0重载1空载
     */
    private Integer isEmpty;
    /**
     * 联系人
     */
    private String contacts;
    /**
     * 联系电话
     */
    private String contactNum;
    /**
     * 预计抛锚时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date preAnchTime;
    /**
     * 预计离锚时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date preLeaveAnchTime;
    /**
     * 是否危险品船0否1是
     */
    private Integer isDanger;
    /**
     * 锚泊原因
     */
    private String anchReason;
    /**
     * 审核状态,0待审1通过2不通过
     */
    private Integer auditStatus;
    /**
     * 审核者-ID
     */
    private Long auditBy;
    /**
     * 审核者-姓名
     */
    private String auditByName;
    /**
     * 审核者-姓名
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;
    /**
     * 备注
     */
    private String remark;
    /**
     * 离锚状态，0未离1已离
     */
    private Integer status;
    /**
     * 航次ID
     */
    private Long shipvoyageId;
    /**
     * 24小时确报 预计抵港时间
     */

    private String arrivalTimePlan;

    private String leaveAnchorageTime;

    private  String arrivalAnchorageTime;

    //审核不通过原因
    private String failReason;
}
