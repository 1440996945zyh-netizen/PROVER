package com.yy.ppm.machine.bean.dto;

import java.io.Serializable;
import java.util.Date;

import com.yy.ppm.common.bean.po.BasePO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TMacTerminalWorkPlanDTO extends BasePO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4292983992705760507L;
    
	/**
	 * 指令ID
	 */
	private Long trustId;
	
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
	 * 船名
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
	 * 车辆入港状态（10：未超1小时，20：超过1小时）
	 */
	private String inPortStatus;
	
	/**
	 * 作业开始时间
	 */
	private Date workTimeStart;
	
	/**
	 * 作业结束时间
	 */
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
	 * 货主,cargoOwnerId
	 */
	private Long cargoOwnerId;
	private String cargoOwnerName;

	/**
	 * 磅单备注
	 */
	private String poundRemark;
	
	/**
	 * 入港时间（一次磅时间）
	 */
	private String weighInDt;
}