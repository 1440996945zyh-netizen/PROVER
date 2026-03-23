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
import com.yy.ppm.equipment.bean.po.EEquipScrapHistoryPO;
import com.yy.ppm.equipment.bean.po.EEquipScrapPO;
import com.yy.ppm.equipment.bean.po.MEquipmentInfoPO;
import com.yy.ppm.equipment.mapper.EEquipScrapHistoryMapper;
import com.yy.ppm.equipment.mapper.EEquipScrapMapper;
import com.yy.ppm.equipment.mapper.MEquipmentInfoMapper;
import com.yy.ppm.equipment.service.EEquipScrapHistoryService;
import com.yy.ppm.equipment.service.EEquipScrapService;
import com.yy.ppm.equipment.service.MEquipmentInfoService;
import com.yy.ppm.flowable.bean.dto.BpmProcessInstanceDTO;
import com.yy.ppm.flowable.service.BpmProcessInstanceService;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
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
 * @Author: fanxianjin
 * @Desc: 设备报废Service业务层处理
 * @Date: 2026/2/28 14:27
 */
@Service
public class EEquipScrapServiceImpl implements EEquipScrapService {

	/**
	 * 日志组件
	 */
	private static final MicroLogger LOGGER = new MicroLogger(EEquipScrapServiceImpl.class);

	@Resource
	private EEquipScrapMapper scrapMapper;

	@Resource
	@Lazy
	private EEquipScrapHistoryService scrapHistoryService;

	@Autowired
	private Snowflake snowflake;

	@Resource
	private EEquipScrapHistoryMapper scrapHistoryMapper;

	@Resource
	private MEquipmentInfoMapper mEquipmentInfoMapper;

	@Resource
	private MEquipmentInfoService mEquipmentInfoService;

	@Resource
	BpmProcessInstanceService bpmProcessInstanceService;


	/**
	 * 查询设备报废列表（分页）
	 */
	@Override
	public Pages<EEquipScrapDTO> getList(EEquipScrapSearchDTO searchDTO) {
		final String methodName = "EEquipScrapServiceImpl:getList";
		LOGGER.enter(methodName, "查询设备报废列表（分页）");

		Pages<EEquipScrapDTO> pages = PageHelperUtils.limit(searchDTO, () -> scrapMapper.getList(searchDTO));

		LOGGER.exit(methodName, "");
		return pages;
	}

	/**
	 * 查询可报废设备列表（分页）
	 */
	@Override
	public Pages<ScrapEquipDTO> scrapEquipList(EEquipScrapSearchDTO.EquipSelectSearchDTO searchDTO) {
		final String methodName = "EEquipScrapServiceImpl:scrapEquipList";
		LOGGER.enter(methodName, "查询可报废设备列表");

		Pages<ScrapEquipDTO> pages = PageHelperUtils.limit(searchDTO, () -> scrapMapper.scrapEquipList(searchDTO));

		LOGGER.exit(methodName, "");
		return pages;
	}

	/**
	 * 根据ID查询设备报废
	 */
	@Override
	public EEquipScrapDTO getById(Long id) {
		final String methodName = "EEquipScrapServiceImpl:getById";
		LOGGER.enter(methodName, "查询报废申请，id:" + id);

		EEquipScrapDTO dto = scrapMapper.getById(id);

		LOGGER.exit(methodName, "");
		return dto;
	}

	/**
	 * 创建设备报废申请
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int create(EEquipScrapDTO dto) {
		final String methodName = "EEquipScrapServiceImpl:create";
		LOGGER.enter(methodName, "创建报废申请");

		if (dto.getEquipList() == null || dto.getEquipList().isEmpty()) {
			throw new BusinessRuntimeException("请选择需要报废的设备");
		}

		Long userId = SecurityUtils.getLoginUserId();
		String userName = SecurityUtils.getLoginUserName();
		Date now = new Date();

		EEquipScrapPO po = new EEquipScrapPO();
		BeanUtils.copyProperties(dto, po);

		Long id = snowflake.nextId();
		String scrapCode = "BF" + id.toString();

		po.setId(id);
		po.setScrapCode(scrapCode);
		po.setScrapCode(UUIDUtils.getScrapCode(userId));
		po.setStatus(0L);
		po.setApplyUser(userId);
		po.setDelFlag(0L);
		po.setCreateTime(now);
		po.setCreateBy(userId);
		po.setCreateByName(userName);
		po.setFlowId("");

		int count = scrapMapper.insert(po);
		if (count > 0 && !dto.getEquipList().isEmpty()) {
			scrapHistoryService.createHistory(id, dto.getEquipList(), userId);

			//TODO 发起流程，暂时不发起直接更新设备状态为报废
//			confirm(id, "");
		}

		LOGGER.exit(methodName, "");
		return count;
	}

	/**
	 * 确认设备报废
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int confirm(Long id, String flowId) {
		final String methodName = "EEquipScrapServiceImpl:confirm";
		LOGGER.enter(methodName, "确认报废，id:" + id);

		EEquipScrapDTO scrapDTO = scrapMapper.getById(id);

		if (scrapDTO == null) {
			throw new BusinessRuntimeException("报废申请不存在");
		}

		List<EEquipScrapHistoryPO> historyList = scrapHistoryMapper.getHistoryByOrderId(scrapDTO.getId());

		if (CollectionUtils.isNotEmpty(historyList)) {
			for (EEquipScrapHistoryPO history : historyList) {
				if (history.getLastChangeInfo() != null && !history.getLastChangeInfo().isEmpty()) {
					try {
						ScrapEquipDTO equipDTO = JSON.parseObject(history.getLastChangeInfo(), ScrapEquipDTO.class);
						// 更新设备状态为报废
						if (equipDTO.getEquipState() != null && !equipDTO.getEquipState().equals("05")) {
							MEquipmentInfoPO mEquipmentInfoPO = new MEquipmentInfoPO();
							mEquipmentInfoPO.setEquipState("05");
							mEquipmentInfoPO.setId(equipDTO.getEquipId());
							mEquipmentInfoPO.setEquipStateName("报废");
							mEquipmentInfoMapper.update(mEquipmentInfoPO);

							// 获取旧数据用于比较
							MEquipmentInfoDTO oldData = new MEquipmentInfoDTO();
							oldData.setId(equipDTO.getEquipId());
							oldData.setEquipState(equipDTO.getEquipState());
							oldData.setEquipStateName(equipDTO.getEquipStateName());
							MEquipmentInfoDTO newData = new MEquipmentInfoDTO();
							newData.setId(equipDTO.getEquipId());
							newData.setEquipState("05");
							newData.setEquipStateName("报废");
							mEquipmentInfoService.recordBasicInfoChange(equipDTO.getEquipId(), oldData, newData);

						}
					} catch (Exception e) {
						LOGGER.error(methodName, "解析设备JSON失败， equipId:" + history.getEquipId());
					}
				}
			}
		}

//		Long userId = SecurityUtils.getLoginUserId();
//		String userName = SecurityUtils.getLoginUserName();
		Date now = new Date();

		EEquipScrapPO updatePO = new EEquipScrapPO();
		updatePO.setId(scrapDTO.getId());
		updatePO.setStatus(2L);
//		updatePO.setExecuteUser(userId);
		updatePO.setExecuteFulfilTime(now);
		updatePO.setUpdateTime(now);
//		updatePO.setUpdateBy(userId);
//		updatePO.setUpdateByName(userName);

		int count = scrapMapper.updateStatus(updatePO);

		LOGGER.exit(methodName, "result:" + count);
		return count;

	}

	/**
	 * 查询报废详情
	 */
	@Override
	public EEquipScrapDTO getDetailByOrderId(Long orderId) {
		final String methodName = "EEquipScrapHistoryServiceImpl:getDetailByOrderId";
		LOGGER.enter(methodName, "查询报废详情，orderId:" + orderId);

		EEquipScrapDTO scrapDTO = scrapMapper.getById(orderId);
		if (scrapDTO == null) {
			throw new BusinessRuntimeException("报废申请不存在");
		}

		List<ScrapEquipDTO> equipList = scrapHistoryService.getHistoryByOrderId(orderId);
		scrapDTO.setEquipList(equipList);

		LOGGER.exit(methodName, "");
		return scrapDTO;
	}

	/**
	 * 导出设备报废列表
	 */
	@Override
	public byte[] exportList(EEquipScrapSearchDTO searchDTO) {
		final String methodName = "EEquipScrapServiceImpl:exportList";
		LOGGER.enter(methodName, "导出设备报废列表");

		// 查询所有数据（不分页）
		List<EEquipScrapDTO> list = scrapMapper.getListForExport(searchDTO);

		// 转换为导出DTO
		List<EEquipScrapExportDTO> exportList = new ArrayList<>();
		for (EEquipScrapDTO dto : list) {
			EEquipScrapExportDTO exportDTO = new EEquipScrapExportDTO();
			exportDTO.setScrapCode(dto.getScrapCode());
			exportDTO.setTitle(dto.getTitle());
			exportDTO.setUseCompanyName(dto.getUseCompanyName());
			exportDTO.setUseOrgName(dto.getUseOrgName());
			exportDTO.setApplyUserName(dto.getApplyUserName());
			exportDTO.setStatusName(dto.getStatusName());
			exportDTO.setCreateTime(dto.getCreateTime());
			exportList.add(exportDTO);
		}

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (ExcelWriter excelWriter = EasyExcel.write(os, EEquipScrapExportDTO.class).build()) {
			WriteSheet writeSheet = EasyExcel.writerSheet("设备报废列表").build();
			excelWriter.write(exportList, writeSheet);
		}

		LOGGER.exit(methodName, "导出数量:" + exportList.size());
		return os.toByteArray();
	}

	@Override
	public void submitEquipScrap(BpmProcessInstanceDTO dto) {
		// 判断当前状态，仅未发起状态下可提交
		if (dto.getBusinessDataId() == null) {
			throw new BusinessRuntimeException("业务数据ID不能为空");
		}
		EEquipScrapDTO scrapDTO = scrapMapper.getById(dto.getBusinessDataId());
		if (scrapDTO == null) {
			throw new BusinessRuntimeException("业务数据不存在");
		}
		if (!"0".equals(scrapDTO.getProcessStatus())) {
			throw new BusinessRuntimeException("仅未发起状态下可提交");
		}
		// 调用流程实例发起
		bpmProcessInstanceService.createProcessInstance(getLoginUserId(), dto);

	}

	@Override
	public Long getBusinessDataIdByProcessInstanceId(String processInstanceId) {
		return scrapMapper.getBusinessDataIdByProcessInstanceId(processInstanceId);
	}

	@Override
	public int updateStatus(EEquipScrapPO po) {
		return  scrapMapper.updateStatus(po);
	}

	@Override
	@Transactional
	public int deleteById(Long id) {
		final String methodName = "EEquipScrapServiceImpl:deleteById";
		LOGGER.enter(methodName, "id:" + id);
		EEquipScrapDTO scrapDTO = scrapMapper.getById(id);
		if (scrapDTO == null) {
			return 0;
		}
		if (!"0".equals(scrapDTO.getProcessStatus())) {
			throw new BusinessRuntimeException("只有未发起的申请才允许删除");
		}
		// 删除子表/历史记录
		scrapHistoryMapper.deleteByOrderId(id);
		// 删除主表
		int result = scrapMapper.deleteById(id);
		LOGGER.exit(methodName, "result:" + result);
		return result;
	}

	@Override
	@Transactional
	public int deleteByIds(List<Long> ids) {
		final String methodName = "EEquipScrapServiceImpl:deleteByIds";
		LOGGER.enter(methodName, "ids:" + ids);
		for (Long id : ids) {
			EEquipScrapDTO scrapDTO = scrapMapper.getById(id);
			if (scrapDTO != null && !"0".equals(scrapDTO.getProcessStatus())) {
				throw new BusinessRuntimeException("工单[" + scrapDTO.getScrapCode() + "]已发起流转，不允许删除");
			}
		}
		// 批量删除子表/历史记录
		scrapHistoryMapper.deleteByOrderIds(ids);
		// 批量删除主表
		int result = scrapMapper.deleteByIds(ids);
		LOGGER.exit(methodName, "result:" + result);
		return result;
	}

}
