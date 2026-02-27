package com.yy.ppm.system.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.ppm.common.bean.po.SysFilePO;
import com.yy.ppm.common.mapper.CommonMapper;
import com.yy.ppm.common.mapper.SysFileMapper;
import com.yy.ppm.system.bean.dto.SysVersionControlDTO;
import com.yy.ppm.system.bean.po.SysVersionControlPO;
import com.yy.ppm.system.mapper.SysVersionControlMapper;
import com.yy.ppm.system.service.SysVersionControlService;

import cn.hutool.core.lang.Snowflake;


@Service
public class SysVersionControlServiceImpl implements SysVersionControlService {

	/**
	 * 日志组件
	 */
	private static final MicroLogger LOGGER = new MicroLogger(SysVersionControlServiceImpl.class);

	@Autowired
    private SysVersionControlMapper sysVersionControlMapper;

    @Autowired
	private CommonMapper baseMapper;

	@Autowired
    private SysFileMapper sysFileMapper;

	@Autowired
	private Snowflake snowflake;

	@Override
	public SysVersionControlPO getVersion(String versionType) {
		return sysVersionControlMapper.getVersion(versionType);
	}

	@Override
	public SysVersionControlPO getWfmacVersion(String versionType) {
		return sysVersionControlMapper.getWfmacVersion(versionType);
	}


	@Override
    public SysFilePO getFileById(Long id) {
        return sysVersionControlMapper.getFileById(id);
    }

	@Override
	public Pages<SysVersionControlDTO> getList(SysVersionControlDTO sysVersionControlDTO) {

		final String methodName = "SysVersionControlServiceImpl:getList";
		LOGGER.enter(methodName, "业务执行");
		Pages<SysVersionControlDTO> pages = PageHelperUtils.limit(sysVersionControlDTO, () -> {
			return sysVersionControlMapper.getList(sysVersionControlDTO);
		});
		LOGGER.exit(methodName, StringUtils.EMPTY);
		return pages;
	}

	@Override
	public SysVersionControlDTO getById(Long id) {

		final String methodName = "SysVersionControlServiceImpl:getById";
		LOGGER.enter(methodName, "业务执行");

		SysVersionControlDTO sysVersionControlDTO = sysVersionControlMapper.getById(id);

		LOGGER.exit(methodName, StringUtils.EMPTY);
		return sysVersionControlDTO;
	}

	@Override
	@Transactional
	public int deleteById(List<Long> idList) {
		int count = 0;
		final String methodName = "SysVersionControlServiceImpl:deleteById";

		LOGGER.enter(methodName, "业务执行");
		for(Long id : idList){
			SysVersionControlDTO sysVersionControlDTO = sysVersionControlMapper.getById(id);
			if (sysVersionControlDTO != null) {

		        // 先删除
		        sysFileMapper.deleteRelationByBusinessId(id.toString());

				count = baseMapper.deleteById("SYS_VERSION_CONTROL", id);
			}
		}
		LOGGER.exit(methodName, StringUtils.EMPTY);

		return count;
	}

	@Override
	public int save(SysVersionControlPO sysVersionControlPO) {
		final String methodName = "SysVersionControlServiceImpl:save";
		LOGGER.enter(methodName, "业务执行");

		int count = 0;

		if (sysVersionControlPO.getId() == null) {
			sysVersionControlPO.setId(snowflake.nextId());
			count = sysVersionControlMapper.insert(sysVersionControlPO);
		} else {
	        // 先删除
	        sysFileMapper.deleteRelationByBusinessId(sysVersionControlPO.getId().toString());
			count = sysVersionControlMapper.update(sysVersionControlPO);
		}

        if (sysVersionControlPO.getId() != null && sysVersionControlPO.getFileId() != null) {
        	sysFileMapper.insertFileBusiness(sysVersionControlPO.getFileId(), sysVersionControlPO.getId().toString());
        }

		LOGGER.exit(methodName, StringUtils.EMPTY);
		return count;
	}

	@Override
	public int updateStatus(SysVersionControlPO sysVersionControlPO) {
		final String methodName = "SysVersionControlServiceImpl:updateStatus";
		LOGGER.enter(methodName, "业务执行");

		int count = sysVersionControlMapper.updateStatus(sysVersionControlPO);

		LOGGER.exit(methodName, StringUtils.EMPTY);
		return count;
	}
}
