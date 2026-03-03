package com.yy.ppm.equipment.service;

import com.yy.ppm.equipment.bean.dto.ScrapEquipDTO;

import java.util.List;

/**
 * @Author: fanxianjin
 * @Desc: 设备报废历史Service接口
 * @Date: 2026/2/28 14:29
 */
public interface EEquipScrapHistoryService {

	/**
	 * 查询报废设备列表
	 */
	List<ScrapEquipDTO> getHistoryByOrderId(Long orderId);




	/**
	 * 创建报废历史记录
	 */
	void createHistory(Long orderId, List<ScrapEquipDTO> equipList, Long userId);

}
