package com.yy.ppm.system.service;

import java.util.List;

import com.yy.common.page.Pages;
import com.yy.ppm.common.bean.po.SysFilePO;
import com.yy.ppm.system.bean.dto.SysVersionControlDTO;
import com.yy.ppm.system.bean.po.SysVersionControlPO;

public interface SysVersionControlService {
	
	SysVersionControlPO getVersion(String versionType);

	SysVersionControlPO getWfmacVersion(String versionType);

	SysFilePO getFileById(Long fileId);

	Pages<SysVersionControlDTO> getList(SysVersionControlDTO sysVersionControlDTO);

	SysVersionControlDTO getById(Long id);

	int deleteById(List<Long> idList);

	int save(SysVersionControlPO sysVersionControlPO);

	int updateStatus(SysVersionControlPO sysVersionControlPO);
}
