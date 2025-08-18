package com.yy.ppm.business.bean.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.google.api.client.util.Lists;
import com.yy.ppm.business.bean.po.TBusVehicleTransferPO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TBusVehicleTransferDTO extends TBusVehicleTransferPO{

	private Long id;
	private Long trustId;
	private Long trustCargoId;
	private Long equipmentId;
	private String equipmentNo;
	private Date startTime;
	private Date endTime;
	//状态 1可用 0不可用
	private Long status;

	private BigDecimal tons;// 总吨数
	private Integer carCount;// 趟数

	private String equipmentTypeName;
	private String flag;
	private Long delFlag;
	private String equipmentTypeId;
	private List<TBusVehicleTransferPO> busVehicleTransferList = Lists.newArrayList();


}