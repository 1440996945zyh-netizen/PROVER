package com.yy.ppm.equipment.service;

import com.yy.ppm.equipment.bean.dto.MEquipmentFinanceDTO;

/**
 * 设备财务信息Service接口
 * @author system
 */
public interface MEquipmentFinanceService {

    /**
     * 根据设备ID查询财务信息
     */
    MEquipmentFinanceDTO getByEquipId(Long equipId);

    /**
     * 保存设备财务信息
     */
    void save(MEquipmentFinanceDTO dto, Long equipId);

    /**
     * 删除设备财务信息
     */
    void deleteByEquipId(Long equipId);
}

