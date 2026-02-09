package com.yy.ppm.equipment.service;

import com.yy.ppm.equipment.bean.dto.MEquipmentSpecialDTO;

/**
 * 特种设备Service接口
 * @author system
 */
public interface MEquipmentSpecialService {

    /**
     * 根据设备ID查询特种设备信息
     */
    MEquipmentSpecialDTO getByEquipId(Long equipId);

    /**
     * 保存设备特种设备信息
     */
    void save(MEquipmentSpecialDTO dto, Long equipId);

    /**
     * 删除设备特种设备信息
     */
    void deleteByEquipId(Long equipId);
}

