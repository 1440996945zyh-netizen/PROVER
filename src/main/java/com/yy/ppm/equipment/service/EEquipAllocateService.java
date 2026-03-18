package com.yy.ppm.equipment.service;

import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EEquipAllocateDTO;
import com.yy.ppm.equipment.bean.dto.EEquipAllocateSearchDTO;
import com.yy.ppm.equipment.bean.dto.AllocateEquipDTO;
import com.yy.ppm.equipment.bean.po.EEquipAllocatePO;
import com.yy.ppm.flowable.bean.dto.BpmProcessInstanceDTO;

import java.util.List;

/**
 * 设备调拨Service接口
 * @author system
 */
public interface EEquipAllocateService {

	/**
	 * 查询设备调拨列表（分页）
	 */
	Pages<EEquipAllocateDTO> getList(EEquipAllocateSearchDTO searchDTO);

	/**
	 * 查询可调拨设备列表（分页）
	 */
	Pages<AllocateEquipDTO> allocateEquipList(EEquipAllocateSearchDTO.EquipSelectSearchDTO searchDTO);

	/**
	 * 根据ID查询设备调拨
	 */
	EEquipAllocateDTO getById(Long id);

	/**
	 * 创建设备调拨申请
	 */
	int create(EEquipAllocateDTO dto);

	/**
	 * 确认设备调拨
	 */
	int confirm(Long id, String flowId);

    /**
     * 删除设备调拨单
     */
    int deleteById(Long id);

    /**
     * 批量删除设备调拨单
     */
    int deleteByIds(List<Long> ids);

	/**
	 * 查询调拨详情
	 */
	EEquipAllocateDTO getDetailByOrderId(Long orderId);

	/**
	 * 导出设备调拨列表
	 */
	byte[] exportList(EEquipAllocateSearchDTO searchDTO);

	/**
	 * 设备调拨提交审批
	 */
    void submitEquipAllocate(BpmProcessInstanceDTO dto);

	/**
	 * 功能描述: 根据流程实例ID获取业务ID
	 * @return : java.lang.Long
	 */
	Long getBusinessDataIdByProcessInstanceId(String processInstanceId);

	int updateStatus(EEquipAllocatePO updatePO);
}
