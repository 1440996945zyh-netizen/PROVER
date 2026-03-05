package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.alibaba.fastjson2.JSON;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.SecurityUtils;
import com.yy.ppm.equipment.bean.dto.ScrapEquipDTO;
import com.yy.ppm.equipment.bean.po.EEquipScrapHistoryPO;
import com.yy.ppm.equipment.mapper.EEquipScrapHistoryMapper;
import com.yy.ppm.equipment.service.EEquipScrapHistoryService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: fanxianjin
 * @Desc: 设备报废历史Service业务层处理
 * @Date: 2026/2/28 14:24
 */
@Service
public class EEquipScrapHistoryServiceImpl implements EEquipScrapHistoryService {

	/**
	 * 日志组件
	 */
	private static final MicroLogger LOGGER = new MicroLogger(EEquipScrapHistoryServiceImpl.class);

	@Resource
	private EEquipScrapHistoryMapper scrapHistoryMapper;

	@Autowired
	private Snowflake snowflake;



	/**
	 * 查询报废设备列表
	 */
	@Override
	public List<ScrapEquipDTO> getHistoryByOrderId(Long orderId) {
		final String methodName = "EEquipScrapHistoryServiceImpl:getHistoryByOrderId";
		LOGGER.enter(methodName, "查询报废设备列表，orderId:" + orderId);

		List<EEquipScrapHistoryPO> historyList = scrapHistoryMapper.getHistoryByOrderId(orderId);

		List<ScrapEquipDTO> resultList = new ArrayList<>();
		for (EEquipScrapHistoryPO dto : historyList) {
			if (dto.getLastChangeInfo() != null && !dto.getLastChangeInfo().isEmpty()) {
				try {
					ScrapEquipDTO equipDto = JSON.parseObject(dto.getLastChangeInfo(), ScrapEquipDTO.class);
					resultList.add(equipDto);
				} catch (Exception e) {
					LOGGER.error(methodName, "解析设备JSON失败: " + dto.getLastChangeInfo());
				}
			}
		}

		LOGGER.exit(methodName, "result size:" + resultList.size());
		return resultList;
	}


	/**
	 * 创建报废历史记录
	 */
	@Transactional(rollbackFor = Exception.class)
	public void createHistory(Long orderId, List<ScrapEquipDTO> equipList, Long userId) {
		final String methodName = "EEquipScrapHistoryServiceImpl:createHistory";
		LOGGER.enter(methodName, "创建报废历史记录");

		Date now = new Date();
		String userName = SecurityUtils.getLoginUserName();

		for (ScrapEquipDTO equip : equipList) {
			EEquipScrapHistoryPO historyPO = new EEquipScrapHistoryPO();
			historyPO.setId(snowflake.nextId());
			historyPO.setOrderId(orderId);
			historyPO.setEquipId(equip.getEquipId());

			historyPO.setLastChangeInfo(equip.getLastChangeInfo());

			historyPO.setDelFlag(0L);
			historyPO.setCreateTime(now);
			historyPO.setCreateBy(userId);
			historyPO.setCreateByName(userName);

			scrapHistoryMapper.insert(historyPO);
		}

		LOGGER.exit(methodName, "");
	}

}
