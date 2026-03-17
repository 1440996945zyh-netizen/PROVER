package com.yy.ppm.equipment.service;

import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.MEquipmentChangeLogDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentChangeLogSearchDTO;

/**
 * 设备变更记录Service接口
 * @author system
 */
public interface MEquipmentChangeLogService {

    /**
     * 查询变更记录列表（分页）
     */
    Pages<MEquipmentChangeLogDTO> getList(MEquipmentChangeLogSearchDTO searchDTO);

    /**
     * 根据ID查询变更记录详情（包含子表数据）
     */
    MEquipmentChangeLogDTO getById(Long id);
}

