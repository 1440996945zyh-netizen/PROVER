package com.yy.ppm.runpile.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.ppm.runpile.bean.dto.MStorageStackPositionDTO;
import com.yy.ppm.runpile.bean.dto.MStorageStackPositionSearchDTO;
import com.yy.ppm.runpile.mapper.TRunPileHistoryMapper;
import com.yy.ppm.runpile.service.TRunPileHistoryService;


@Service
public class TRunPileHistoryServiceImpl implements TRunPileHistoryService {
	
   private static final MicroLogger LOGGER = new MicroLogger(TRunPileHistoryServiceImpl.class);

    @Resource
    private TRunPileHistoryMapper tRunPileHistoryMapper;

	@Override
	public Pages<MStorageStackPositionDTO> getRunPileHistoryList(MStorageStackPositionSearchDTO mStorageStackPositionSearchDTO) {

        final String methodName = "TRunPileHistoryServiceImpl:getRunPileHistoryList";
        LOGGER.enter(methodName, "业务执行");
        Pages<MStorageStackPositionDTO> pages = PageHelperUtils.limit(mStorageStackPositionSearchDTO, () -> {
            return tRunPileHistoryMapper.getRunPileHistoryList(mStorageStackPositionSearchDTO);
        });
        LOGGER.exit(methodName, StringUtils.EMPTY);
        return pages;
	}

	@Override
	public List<MStorageStackPositionDTO> getList(String stackId) {

        final String methodName = "TRunPileHistoryServiceImpl:getList";
        LOGGER.enter(methodName, "业务执行");
        
        List<MStorageStackPositionDTO> resList =  tRunPileHistoryMapper.getList(stackId);

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return resList;
	}
}
