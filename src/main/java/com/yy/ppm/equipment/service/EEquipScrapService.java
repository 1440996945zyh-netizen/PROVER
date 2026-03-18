package com.yy.ppm.equipment.service;

import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EEquipScrapDTO;
import com.yy.ppm.equipment.bean.dto.EEquipScrapSearchDTO;
import com.yy.ppm.equipment.bean.dto.ScrapEquipDTO;
import com.yy.ppm.equipment.bean.po.EEquipScrapPO;
import com.yy.ppm.flowable.bean.dto.BpmProcessInstanceDTO;

import java.util.List;

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
     * 删除设备报废单
     * @param id
     * @return
     */
    int deleteById(Long id);

    /**
     * 批量删除设备报废单
     * @param ids
     * @return
     */
    int deleteByIds(List<Long> ids);

	/**
	 * 查询报废详情
	 */
	EEquipScrapDTO getDetailByOrderId(Long orderId);

	/**
	 * 导出设备报废列表
	 */
	byte[] exportList(EEquipScrapSearchDTO searchDTO);

	/**
	 * 流程提交设备报废申请
	 */
	void submitEquipScrap(BpmProcessInstanceDTO dto);

	/**
	 * 功能描述: 根据流程实例ID获取业务ID
	 * @return : java.lang.Long
	 */
	Long getBusinessDataIdByProcessInstanceId(String processInstanceId);

	int updateStatus(EEquipScrapPO po);



}
