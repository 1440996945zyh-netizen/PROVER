package com.yy.ppm.equipment.service;

import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMEqptFileDTO;
import com.yy.ppm.equipment.bean.dto.EMEqptFileSearchDTO;

/**
 * 设备资料文件Service接口
 * @author system
 */
public interface EMEqptFileService {

    /**
     * 查询设备资料文件列表（分页）
     */
    Pages<EMEqptFileDTO> getList(EMEqptFileSearchDTO searchDTO);

    /**
     * 根据ID查询设备资料文件
     */
    EMEqptFileDTO getById(Long id);

    /**
     * 新增设备资料文件
     */
    void save(EMEqptFileDTO dto);

    /**
     * 删除设备资料文件
     */
    void deleteById(Long id);
}

