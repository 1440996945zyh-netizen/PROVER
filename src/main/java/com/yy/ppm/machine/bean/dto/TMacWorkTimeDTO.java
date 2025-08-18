package com.yy.ppm.machine.bean.dto;

import java.io.Serializable;
import java.util.Date;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.yy.ppm.common.bean.po.BasePO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备作业时间表
 * @author zcc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TMacWorkTimeDTO extends BasePO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 5172498133428513888L;

	private Long id; // 主键

	private String imei;// 设备串号

	@Valid @NotNull(message = "workPlanId不能为空！")
	private Long workPlanId; // 作业指令id

	@Valid @NotNull(message = "planType不能为空！")
	private String planType; // 计划类型同作业过程

	@Valid @NotNull(message = "macWorkId不能为空！")
	private Long macWorkId; // 设备作业ID

	private Long reservatCarId; // 入港车辆id
	private String carNo; // 车牌号
	private Long dispatchSecondaryId; // 二次配工id
	private Long macId; // 设备id
	private String macCode; // 设备code
	private String macName; // 设备名称
	private Date workTimeStart; // 作业开始时间
	private Date workTimeEnd; // 作业结束时间
	private String remark; // 备注

    /**
     * 航次ID
     */
    private Long shipvoyageId;
	/**
	 * 磅单id
	 */
	private Long weighbridgeId;

	private Long cargoInfoId;


	private String cargoCode;

	private String cargoName;

	private Long trustCargoInfoId;
	private Long storehouseId;// 库场ID
	private String storehouseName;// 库场名称
	private Long regionId;// 区域ID
	private String regionName;// 区域名称
	private Long massId;// 垛位ID
	private String massName;// 垛位名称
	private String directFetching;// 直取
}
