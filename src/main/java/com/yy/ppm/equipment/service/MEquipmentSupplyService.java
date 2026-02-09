package com.yy.ppm.equipment.service;

import com.yy.ppm.equipment.bean.dto.MEquipmentSupplyDTO;

/**
 * 设备供货信息Service接口
 * @author system
 */
public interface MEquipmentSupplyService {

    /**
     * 根据设备ID查询供货信息
     */
    MEquipmentSupplyDTO getByEquipId(Long equipId);

    /**
     * 保存设备供货信息
     */
    void save(MEquipmentSupplyDTO dto, Long equipId);

    /**
     * 删除设备供货信息
     */
    void deleteByEquipId(Long equipId);
}

