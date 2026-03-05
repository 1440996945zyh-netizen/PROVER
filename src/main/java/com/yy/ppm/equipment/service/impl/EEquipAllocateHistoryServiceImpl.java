package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.alibaba.fastjson2.JSON;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.SecurityUtils;
import com.yy.ppm.equipment.bean.dto.AllocateEquipDTO;
import com.yy.ppm.equipment.bean.po.EEquipAllocateHistoryPO;
import com.yy.ppm.equipment.mapper.EEquipAllocateHistoryMapper;
import com.yy.ppm.equipment.service.EEquipAllocateHistoryService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 设备调拨历史Service业务层处理
 * @author system
 */
@Service
public class EEquipAllocateHistoryServiceImpl implements EEquipAllocateHistoryService {

	/**
	 * 日志组件
	 */
	private static final MicroLogger LOGGER = new MicroLogger(EEquipAllocateHistoryServiceImpl.class);

	@Resource
	private EEquipAllocateHistoryMapper allocateHistoryMapper;

	@Autowired
	private Snowflake snowflake;

	/**
	 * 创建调拨历史记录
	 */
	@Override
	public void createHistory(Long orderId, List<AllocateEquipDTO> equipList, Long userId) {
		final String methodName = "EEquipAllocateHistoryServiceImpl:createHistory";
		LOGGER.enter(methodName, "创建调拨历史记录，orderId:" + orderId);

		Date now = new Date();
		String userName = SecurityUtils.getLoginUserName();

		for (AllocateEquipDTO equip : equipList) {
			EEquipAllocateHistoryPO historyPO = new EEquipAllocateHistoryPO();
			historyPO.setId(snowflake.nextId());
			historyPO.setOrderId(orderId);
			historyPO.setEquipId(equip.getEquipId());
			historyPO.setLastChangeInfo(equip.getLastChangeInfo());
			historyPO.setRemark(equip.getRemark());
			historyPO.setDelFlag(0L);
			historyPO.setCreateTime(now);
			historyPO.setCreateBy(userId);
			historyPO.setCreateByName(userName);

			allocateHistoryMapper.insert(historyPO);
		}

		LOGGER.exit(methodName, "创建数量:" + equipList.size());
	}

	/**
	 * 根据工单ID查询调拨设备列表
	 */
	@Override
	public List<AllocateEquipDTO> getHistoryByOrderId(Long orderId) {
		final String methodName = "EEquipAllocateHistoryServiceImpl:getHistoryByOrderId";
		LOGGER.enter(methodName, "查询调拨设备列表，orderId:" + orderId);

		List<AllocateEquipDTO> list = allocateHistoryMapper.getEquipListByOrderId(orderId);

		LOGGER.exit(methodName, "查询数量:" + list.size());
		return list;
	}

}
