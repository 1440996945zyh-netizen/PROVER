package com.yy.ppm.equipment.service;

import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EEquipScrapDTO;
import com.yy.ppm.equipment.bean.dto.EEquipScrapSearchDTO;
import com.yy.ppm.equipment.bean.dto.ScrapEquipDTO;

/**
 * @Author: fanxianjin
 * @Desc: 设备报废Service接口
 * @Date: 2026/2/28 14:28
 */
public interface EEquipScrapService {

	/**
	 * 查询设备报废列表（分页）
	 */
	Pages<EEquipScrapDTO> getList(EEquipScrapSearchDTO searchDTO);

	/**
	 * 查询可报废设备列表（分页）
	 */
	Pages<ScrapEquipDTO> scrapEquipList(EEquipScrapSearchDTO.EquipSelectSearchDTO searchDTO);

	/**
	 * 根据ID查询设备报废
	 */
	EEquipScrapDTO getById(Long id);

	/**
	 * 创建设备报废申请
	 */
	int create(EEquipScrapDTO dto);

	/**
	 * 确认设备报废
	 */
	int confirm(Long id, String flowId);

	/**
	 * 查询报废详情
	 */
	EEquipScrapDTO getDetailByOrderId(Long orderId);

	/**
	 * 导出设备报废列表
	 */
	byte[] exportList(EEquipScrapSearchDTO searchDTO);

}
