package com.yy.ppm.dispatch.bean.dto;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.page.PageParameter;

import lombok.Data;

@Data
public class TAnchApplySearchDTO extends PageParameter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3171068949321381508L;

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
    private Integer draft;
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
    
    private Long createBy;

}
