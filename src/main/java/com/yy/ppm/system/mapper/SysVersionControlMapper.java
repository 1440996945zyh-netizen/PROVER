package com.yy.ppm.system.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.common.bean.po.SysFilePO;
import com.yy.ppm.system.bean.dto.SysVersionControlDTO;
import com.yy.ppm.system.bean.po.SysVersionControlPO;

@Repository
public interface SysVersionControlMapper {
	
	public SysVersionControlPO getVersion(String versionType);

	public SysVersionControlPO getWfmacVersion(String versionType);

	public SysFilePO getFileById(@Param("id") Long id);
	
	public Page<SysVersionControlDTO> getList(SysVersionControlDTO sysVersionControlDTO);

	public SysVersionControlDTO getById(Long id);

	@Edit
	public int insert(SysVersionControlPO sysVersionControlPO);

	@Edit
	public int update(SysVersionControlPO sysVersionControlPO);

	@Edit
	public int updateStatus(SysVersionControlPO sysVersionControlPO);
}