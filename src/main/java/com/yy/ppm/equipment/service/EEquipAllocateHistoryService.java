package com.yy.ppm.equipment.service;

import com.yy.ppm.equipment.bean.dto.AllocateEquipDTO;
import com.yy.ppm.equipment.bean.po.EEquipAllocateHistoryPO;

import java.util.List;

/**
 * 设备调拨历史Service接口
 * @author system
 */
public interface EEquipAllocateHistoryService {

	/**
	 * 创建调拨历史记录
	 */
	void createHistory(Long orderId, List<AllocateEquipDTO> equipList, Long userId);

	/**
	 * 根据工单ID查询调拨设备列表
	 */
	List<AllocateEquipDTO> getHistoryByOrderId(Long orderId);

}
