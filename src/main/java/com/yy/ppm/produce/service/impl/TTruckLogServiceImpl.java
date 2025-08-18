package com.yy.ppm.produce.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.ppm.common.bean.po.SysFilePO;
import com.yy.ppm.common.mapper.CommonMapper;
import com.yy.ppm.common.mapper.SysFileMapper;
import com.yy.ppm.produce.bean.dto.TTruckLogDTO;
import com.yy.ppm.produce.mapper.TTruckLogMapper;
import com.yy.ppm.produce.service.TTruckLogService;
import com.yy.ppm.system.bean.dto.SysVersionControlDTO;
import com.yy.ppm.system.bean.po.SysVersionControlPO;
import com.yy.ppm.system.mapper.SysVersionControlMapper;
import com.yy.ppm.system.service.SysVersionControlService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class TTruckLogServiceImpl implements TTruckLogService {

	/**
	 * 日志组件
	 */
	private static final MicroLogger LOGGER = new MicroLogger(TTruckLogServiceImpl.class);
	
	@Autowired
    private TTruckLogMapper tTruckLogMapper;

	@Autowired
	private Snowflake snowflake;


	@Override
	public Pages<TTruckLogDTO> getList(TTruckLogDTO query) {

		final String methodName = "TTruckLogServiceImpl:getList";
		LOGGER.enter(methodName, "业务执行");
		Pages<TTruckLogDTO> pages = PageHelperUtils.limit(query, () -> {
			return tTruckLogMapper.getList(query);
		});
		pages.getPages().forEach(o->{
			if("1".equals(o.getWeightType())){
				o.setWeightType("进港");
			}else if ("0".equals(o.getWeightType())){
				o.setWeightType("出港");
			}
			if("0".equals(o.getPlanType())){
				o.setPlanType("内倒");
			}else if("1".equals(o.getPlanType())){
				o.setPlanType("集疏港");
			}else if("2".equals(o.getPlanType())){
				o.setPlanType("杂货");
			}
		});
		LOGGER.exit(methodName, StringUtils.EMPTY);
		return pages;
	}


}