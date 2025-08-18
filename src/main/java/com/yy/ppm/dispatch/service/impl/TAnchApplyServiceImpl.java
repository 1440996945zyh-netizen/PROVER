package com.yy.ppm.dispatch.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.dispatch.bean.dto.TAnchApplyDTO;
import com.yy.ppm.dispatch.bean.dto.TAnchApplySearchDTO;
import com.yy.ppm.dispatch.mapper.TAnchApplyMapper;
import com.yy.ppm.dispatch.service.TAnchApplyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

@Service
public class TAnchApplyServiceImpl implements TAnchApplyService {

	/**
	 * 日志组件
	 */
	private static final MicroLogger LOGGER = new MicroLogger(TAnchApplyServiceImpl.class);

	@Resource
	private TAnchApplyMapper tAnchApplyMapper;
	@Autowired
    private Snowflake snowflake;

	@Override
	public Pages<TAnchApplyDTO> getList(TAnchApplySearchDTO tAnchApplySearchDTO) {
		final String methodName = "getList";
		LOGGER.enter(methodName, "业务执行");

		Pages<TAnchApplyDTO> pages = PageHelperUtils.limit(tAnchApplySearchDTO, () -> {
			return tAnchApplyMapper.getList(tAnchApplySearchDTO);
		});

		LOGGER.exit(methodName, StringUtils.EMPTY);
		return pages;
	}

	@Override
	public int verify(TAnchApplyDTO tAnchApplyDTO) {
		final String methodName = "verify";
		LOGGER.enter(methodName, "业务执行");

		int verify = tAnchApplyMapper.verify(tAnchApplyDTO);

		LOGGER.exit(methodName, StringUtils.EMPTY);
		return verify;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public boolean updateLeaveAnchTime(TAnchApplyDTO tAnchApplyDTO) {
		TAnchApplyDTO tAnchApplyDTO1 = tAnchApplyMapper.getAnchTimeById(tAnchApplyDTO.getId());
		if(tAnchApplyDTO1 == null){
			throw new BusinessRuntimeException("请选中一条数据");
		}
		if(tAnchApplyDTO1.getAnchTime() == null){
			throw new BusinessRuntimeException("未抵锚，无法起锚");
		}

		if (tAnchApplyDTO.getLeaveAnchTime().before(tAnchApplyDTO1.getAnchTime())) {
			throw new BusinessRuntimeException("起锚时间应大于实际抵锚时间");
		}
		/*if (tAnchApplyDTO.getLeaveAnchTime().compareTo(tAnchApplyDTO1.getAnchTime()) <= 0) {
			throw new BusinessRuntimeException("起锚时间应大于实际抵锚时间");
		}*/
		tAnchApplyDTO.setStatus(1);
		return tAnchApplyMapper.updateLeaveAnchTime(tAnchApplyDTO) == 1;
	}

}
