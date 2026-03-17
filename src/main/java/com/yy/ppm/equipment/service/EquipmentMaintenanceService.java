package com.yy.ppm.equipment.service;

import com.yy.ppm.equipment.bean.dto.EquipmentMaintenanceDTO;
import com.yy.ppm.equipment.bean.dto.EquipmentMaintenanceSearchDTO;

import java.util.List;

/**
 * 设备检修历史统计Service接口
 * @author system
 */
public interface EquipmentMaintenanceService {

    /**
     * 查询设备检修历史统计（按月份）
     */
    List<EquipmentMaintenanceDTO> getMaintenanceByMonth(EquipmentMaintenanceSearchDTO searchDTO);
}

