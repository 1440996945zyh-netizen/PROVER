package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson2.JSON;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.common.util.UUIDUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.equipment.bean.dto.EEquipAllocateDTO;
import com.yy.ppm.equipment.bean.dto.EEquipAllocateExportDTO;
import com.yy.ppm.equipment.bean.dto.EEquipAllocateSearchDTO;
import com.yy.ppm.equipment.bean.dto.AllocateEquipDTO;
import com.yy.ppm.equipment.bean.po.EEquipAllocateHistoryPO;
import com.yy.ppm.equipment.bean.po.EEquipAllocatePO;
import com.yy.ppm.equipment.bean.po.MEquipmentInfoPO;
import com.yy.ppm.equipment.mapper.EEquipAllocateHistoryMapper;
import com.yy.ppm.equipment.mapper.EEquipAllocateMapper;
import com.yy.ppm.equipment.mapper.MEquipmentInfoMapper;
import com.yy.ppm.equipment.service.EEquipAllocateHistoryService;
import com.yy.ppm.equipment.service.EEquipAllocateService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 设备调拨Service业务层处理
 * @author system
 */
@Service
public class EEquipAllocateServiceImpl implements EEquipAllocateService {

	/**
	 * 日志组件
	 */
	private static final MicroLogger LOGGER = new MicroLogger(EEquipAllocateServiceImpl.class);

	@Resource
	private EEquipAllocateMapper allocateMapper;

	@Resource
	@Lazy
	private EEquipAllocateHistoryService allocateHistoryService;

	@Autowired
	private Snowflake snowflake;

	@Resource
	private EEquipAllocateHistoryMapper allocateHistoryMapper;

	@Resource
	private MEquipmentInfoMapper mEquipmentInfoMapper;


	/**
	 * 查询设备调拨列表（分页）
	 */
	@Override
	public Pages<EEquipAllocateDTO> getList(EEquipAllocateSearchDTO searchDTO) {
		final String methodName = "EEquipAllocateServiceImpl:getList";
		LOGGER.enter(methodName, "查询设备调拨列表（分页）");

		Pages<EEquipAllocateDTO> pages = PageHelperUtils.limit(searchDTO, () -> allocateMapper.getList(searchDTO));

		LOGGER.exit(methodName, "");
		return pages;
	}

	/**
	 * 查询可调拨设备列表（分页）
	 */
	@Override
	public Pages<AllocateEquipDTO> allocateEquipList(EEquipAllocateSearchDTO.EquipSelectSearchDTO searchDTO) {
		final String methodName = "EEquipAllocateServiceImpl:allocateEquipList";
		LOGGER.enter(methodName, "查询可调拨设备列表");

		Pages<AllocateEquipDTO> pages = PageHelperUtils.limit(searchDTO, () -> allocateMapper.allocateEquipList(searchDTO));

		LOGGER.exit(methodName, "");
		return pages;
	}

	/**
	 * 根据ID查询设备调拨
	 */
	@Override
	public EEquipAllocateDTO getById(Long id) {
		final String methodName = "EEquipAllocateServiceImpl:getById";
		LOGGER.enter(methodName, "查询调拨申请，id:" + id);

		EEquipAllocateDTO dto = allocateMapper.getById(id);

		LOGGER.exit(methodName, "");
		return dto;
	}

	/**
	 * 创建设备调拨申请
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int create(EEquipAllocateDTO dto) {
		final String methodName = "EEquipAllocateServiceImpl:create";
		LOGGER.enter(methodName, "创建调拨申请");

		if (dto.getEquipList() == null || dto.getEquipList().isEmpty()) {
			throw new BusinessRuntimeException("请选择需要调拨的设备");
		}

		Long userId = SecurityUtils.getLoginUserId();
		String userName = SecurityUtils.getLoginUserName();
		Date now = new Date();

		EEquipAllocatePO po = new EEquipAllocatePO();
		BeanUtils.copyProperties(dto, po);

		Long id = snowflake.nextId();
		String allocateCode = UUIDUtils.getDbOrderCode(userId);

		po.setId(id);
		po.setAllocateCode(allocateCode);
		po.setStatus(1L);
		po.setApplyUser(userId);
		po.setAllocateTime(now);
		po.setDelFlag(0L);
		po.setCreateTime(now);
		po.setCreateBy(userId);
		po.setCreateByName(userName);
		po.setFlowId("");

		int count = allocateMapper.insert(po);
		if (count > 0 && !dto.getEquipList().isEmpty()) {
			allocateHistoryService.createHistory(id, dto.getEquipList(), userId);

			// TODO: 发起流程，暂时不发起直接更新设备状态
			confirm(id, "");
		}

		LOGGER.exit(methodName, "");
		return count;
	}

	/**
	 * 确认设备调拨
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int confirm(Long id, String flowId) {
		final String methodName = "EEquipAllocateServiceImpl:confirm";
		LOGGER.enter(methodName, "确认调拨，id:" + id);

		EEquipAllocatePO allocatePO = null;
		if (StringUtils.isNotEmpty(flowId)) {
			allocatePO = allocateMapper.getByFlowId(flowId);
		} else {
			allocatePO = allocateMapper.selectById(id);
		}

		if (allocatePO == null) {
			throw new BusinessRuntimeException("调拨申请不存在");
		}

		List<EEquipAllocateHistoryPO> historyList = allocateHistoryMapper.getHistoryByOrderId(allocatePO.getId());

		if (!historyList.isEmpty()) {
			for (EEquipAllocateHistoryPO history : historyList) {
				if (StringUtils.isNotEmpty(history.getLastChangeInfo())) {
					try {
						AllocateEquipDTO equipDTO = JSON.parseObject(history.getLastChangeInfo(), AllocateEquipDTO.class);
						// 更新设备所属单位和部门
						MEquipmentInfoPO mEquipmentInfoPO = new MEquipmentInfoPO();
						mEquipmentInfoPO.setId(equipDTO.getEquipId());
						mEquipmentInfoPO.setUseCompanyId(allocatePO.getToCompanyId());
						mEquipmentInfoPO.setUseOrgId(allocatePO.getToOrgId());
						mEquipmentInfoMapper.update(mEquipmentInfoPO);
					} catch (Exception e) {
						LOGGER.error(methodName, "解析设备JSON失败， equipId:" + history.getEquipId());
					}
				}
			}
		}

		Long userId = SecurityUtils.getLoginUserId();
		String userName = SecurityUtils.getLoginUserName();
		Date now = new Date();

		EEquipAllocatePO updatePO = new EEquipAllocatePO();
		updatePO.setId(allocatePO.getId());
		updatePO.setStatus(2L);
		updatePO.setExecuteUser(userId);
		updatePO.setAllocateFulfilTime(now);
		updatePO.setUpdateTime(now);
		updatePO.setUpdateBy(userId);
		updatePO.setUpdateByName(userName);

		int count = allocateMapper.updateStatus(updatePO);

		LOGGER.exit(methodName, "result:" + count);
		return count;
	}

	/**
	 * 查询调拨详情
	 */
	@Override
	public EEquipAllocateDTO getDetailByOrderId(Long orderId) {
		final String methodName = "EEquipAllocateServiceImpl:getDetailByOrderId";
		LOGGER.enter(methodName, "查询调拨详情，orderId:" + orderId);

		EEquipAllocateDTO allocateDTO = allocateMapper.getById(orderId);
		if (allocateDTO == null) {
			throw new BusinessRuntimeException("调拨申请不存在");
		}

		List<AllocateEquipDTO> equipList = allocateHistoryService.getHistoryByOrderId(orderId);
		allocateDTO.setEquipList(equipList);

		LOGGER.exit(methodName, "");
		return allocateDTO;
	}

	/**
	 * 导出设备调拨列表
	 */
	@Override
	public byte[] exportList(EEquipAllocateSearchDTO searchDTO) {
		final String methodName = "EEquipAllocateServiceImpl:exportList";
		LOGGER.enter(methodName, "导出设备调拨列表");

		// 查询所有数据（不分页）
		List<EEquipAllocateDTO> list = allocateMapper.getListForExport(searchDTO);

		// 转换为导出DTO
		List<EEquipAllocateExportDTO> exportList = new ArrayList<>();
		for (EEquipAllocateDTO dto : list) {
			EEquipAllocateExportDTO exportDTO = new EEquipAllocateExportDTO();
			exportDTO.setAllocateCode(dto.getAllocateCode());
			exportDTO.setTitle(dto.getTitle());
			exportDTO.setToCompanyName(dto.getToCompanyName());
			exportDTO.setToOrgName(dto.getToOrgName());
			exportDTO.setApplyUserName(dto.getApplyUserName());
			exportDTO.setApplyReason(dto.getApplyReason());
			exportDTO.setStatusName(dto.getStatusName());
			exportDTO.setAllocateTime(dto.getAllocateTime());
			exportDTO.setCreateTime(dto.getCreateTime());
			exportList.add(exportDTO);
		}

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (ExcelWriter excelWriter = EasyExcel.write(os, EEquipAllocateExportDTO.class).build()) {
			WriteSheet writeSheet = EasyExcel.writerSheet("设备调拨列表").build();
			excelWriter.write(exportList, writeSheet);
		}

		LOGGER.exit(methodName, "导出数量:" + exportList.size());
		return os.toByteArray();
	}

}
