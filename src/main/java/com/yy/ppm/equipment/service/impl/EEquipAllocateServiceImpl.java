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
import com.yy.ppm.equipment.bean.dto.*;
import com.yy.ppm.equipment.bean.po.EEquipAllocateHistoryPO;
import com.yy.ppm.equipment.bean.po.EEquipAllocatePO;
import com.yy.ppm.equipment.bean.po.MEquipmentInfoPO;
import com.yy.ppm.equipment.mapper.EEquipAllocateHistoryMapper;
import com.yy.ppm.equipment.mapper.EEquipAllocateMapper;
import com.yy.ppm.equipment.mapper.MEquipmentInfoMapper;
import com.yy.ppm.equipment.service.EEquipAllocateHistoryService;
import com.yy.ppm.equipment.service.EEquipAllocateService;
import com.yy.ppm.equipment.service.MEquipmentInfoService;
import com.yy.ppm.flowable.bean.dto.BpmProcessInstanceDTO;
import com.yy.ppm.flowable.service.BpmProcessInstanceService;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
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

import static com.yy.common.util.SecurityUtils.getLoginUserId;

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

	@Resource
	private MEquipmentInfoService mEquipmentInfoService;

	@Resource
	BpmProcessInstanceService bpmProcessInstanceService;


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
		po.setStatus(0L);
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
//			confirm(id, "");
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

		EEquipAllocateDTO allocateDTO = allocateMapper.getById(id);
		if (allocateDTO == null) {
			throw new BusinessRuntimeException("调拨申请不存在");
		}

		List<EEquipAllocateHistoryPO> historyList = allocateHistoryMapper.getHistoryByOrderId(allocateDTO.getId());
		if (CollectionUtils.isNotEmpty(historyList)) {
			for (EEquipAllocateHistoryPO history : historyList) {
				if (StringUtils.isNotEmpty(history.getLastChangeInfo())) {
					try {
						MEquipmentInfoDTO mEquipmentInfoDTO = mEquipmentInfoService.getById(history.getEquipId());
						AllocateEquipDTO equipDTO = JSON.parseObject(history.getLastChangeInfo(), AllocateEquipDTO.class);
						MEquipmentInfoDTO oldData = new MEquipmentInfoDTO();
						oldData.setId(equipDTO.getEquipId());
						oldData.setUseCompanyId(equipDTO.getUseCompanyId());
						oldData.setUseCompanyName(equipDTO.getUseCompanyName());
						oldData.setUseOrgId(equipDTO.getUseOrgId());
						oldData.setUseOrgName(equipDTO.getUseOrgName());
						oldData.setResponsiCode(mEquipmentInfoDTO.getResponsiCode());
						oldData.setResponsiName(mEquipmentInfoDTO.getResponsiName());
						MEquipmentInfoDTO newData = new MEquipmentInfoDTO();
						newData.setId(equipDTO.getEquipId());
						newData.setUseCompanyId(allocateDTO.getToCompanyId());
						newData.setUseCompanyName(allocateDTO.getToCompanyName());
						newData.setUseOrgId(allocateDTO.getToOrgId());
						newData.setUseOrgName(allocateDTO.getToOrgName());
						newData.setResponsiCode(equipDTO.getResponsiCode());
						newData.setResponsiName(equipDTO.getResponsiName());
						mEquipmentInfoService.recordBasicInfoChange(equipDTO.getEquipId(), oldData, newData);

						// 更新设备所属单位和部门
						MEquipmentInfoPO mEquipmentInfoPO = new MEquipmentInfoPO();
						mEquipmentInfoPO.setId(equipDTO.getEquipId());
						mEquipmentInfoPO.setUseCompanyId(allocateDTO.getToCompanyId());
						mEquipmentInfoPO.setUseOrgId(allocateDTO.getToOrgId());
						mEquipmentInfoPO.setResponsiCode(equipDTO.getResponsiCode());
						mEquipmentInfoMapper.update(mEquipmentInfoPO);

					} catch (Exception e) {
						LOGGER.error(methodName, "解析设备JSON失败， equipId:" + history.getEquipId());
					}
				}
			}
		}

//		Long userId = SecurityUtils.getLoginUserId();
//		String userName = SecurityUtils.getLoginUserName();
		Date now = new Date();

		EEquipAllocatePO updatePO = new EEquipAllocatePO();
		updatePO.setId(allocateDTO.getId());
		updatePO.setStatus(2L);
//		updatePO.setExecuteUser(userId);
		updatePO.setAllocateFulfilTime(now);
		updatePO.setUpdateTime(now);
//		updatePO.setUpdateBy(userId);
//		updatePO.setUpdateByName(userName);

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

    @Override
    public void submitEquipAllocate(BpmProcessInstanceDTO dto) {
		// 判断当前状态，仅未发起状态下可提交
		if (dto.getBusinessDataId() == null) {
			throw new BusinessRuntimeException("业务数据ID不能为空");
		}
		EEquipAllocateDTO allocateDTO = allocateMapper.getById(dto.getBusinessDataId());
		if (allocateDTO == null) {
			throw new BusinessRuntimeException("业务数据不存在");
		}
		if (!"0".equals(allocateDTO.getProcessStatus())) {
			throw new BusinessRuntimeException("仅未发起状态下可提交");
		}
		// 调用流程实例发起
		bpmProcessInstanceService.createProcessInstance(getLoginUserId(), dto);
    }


	@Override
	public Long getBusinessDataIdByProcessInstanceId(String processInstanceId) {
		return allocateMapper.getBusinessDataIdByProcessInstanceId(processInstanceId);
	}

	@Override
	public int updateStatus(EEquipAllocatePO po) {
		return  allocateMapper.updateStatus(po);
	}

	@Override
	@Transactional
	public int deleteById(Long id) {
		final String methodName = "EEquipAllocateServiceImpl:deleteById";
		LOGGER.enter(methodName, "id:" + id);
		EEquipAllocateDTO allocateDTO = allocateMapper.getById(id);
		if (allocateDTO == null) {
			return 0;
		}
		if (!"0".equals(allocateDTO.getProcessStatus())) {
			throw new BusinessRuntimeException("只有未发起的申请才允许删除");
		}
		// 删除子表/历史记录
		allocateHistoryMapper.deleteByOrderId(id);
		// 删除主表
		int result = allocateMapper.deleteById(id);
		LOGGER.exit(methodName, "result:" + result);
		return result;
	}

	@Override
	@Transactional
	public int deleteByIds(List<Long> ids) {
		final String methodName = "EEquipAllocateServiceImpl:deleteByIds";
		LOGGER.enter(methodName, "ids:" + ids);
		for (Long id : ids) {
			EEquipAllocateDTO allocateDTO = allocateMapper.getById(id);
			if (allocateDTO != null && !"0".equals(allocateDTO.getProcessStatus())) {
				throw new BusinessRuntimeException("工单[" + allocateDTO.getAllocateCode() + "]已发起流转，不允许删除");
			}
		}
		// 批量删除子表/历史记录
		allocateHistoryMapper.deleteByOrderIds(ids);
		// 批量删除主表
		int result = allocateMapper.deleteByIds(ids);
		LOGGER.exit(methodName, "result:" + result);
		return result;
	}

}
