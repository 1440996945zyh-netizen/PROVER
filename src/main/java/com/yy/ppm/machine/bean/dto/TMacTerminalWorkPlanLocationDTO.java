package com.yy.ppm.machine.bean.dto;

import java.io.Serializable;

import com.yy.ppm.common.bean.po.BasePO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 作业计划位置表
 * @author zcc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TMacTerminalWorkPlanLocationDTO extends BasePO implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 69817959685693330L;
	
	private Long id;
	
	/**
	 * 计划ID
	 */
	private Long workPlanId;
	
	/**
	 * 方向(1源，2目标)
	 */
	private String direction;
	
	/**
	 * 库场ID
	 */
	private Long storehouseId;
	
	/**
	 * 库场名称
	 */
	private String storehouseName;
	
	/**
	 * 区域ID
	 */
	private Long regionId;
	
	/**
	 * 区域名称
	 */
	private String regionName;
	
	/**
	 * 垛位ID
	 */
	private Long massId;
	
	/**
	 * 垛位名称
	 */
	private String massName;
	
	/**
	 * 跑垛表中垛位的id
	 */
	private String stackId;
	
	/**
	 * 票货ID
	 */
	private Long cargoInfoId;
	
	/**
	 * 类型
	 */
	private String type;
}