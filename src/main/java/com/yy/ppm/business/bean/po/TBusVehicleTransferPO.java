package com.yy.ppm.business.bean.po;

import java.util.Date;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TBusVehicleTransferPO extends BasePO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1377431499844407473L;
	
	private Long id;
	private Long trustId;
	private Long trustCargoId;
	private Long equipmentId;
	private String equipmentNo;
	private Long delFlag;
	private Date startTime;
	private Date endTime;
	private Long status;

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		TBusVehicleTransferPO other = (TBusVehicleTransferPO) obj;
		return id != null ? id.equals(other.id) : other.id == null;
	}

	// 重写hashCode方法，只使用ID生成hashCode
	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}