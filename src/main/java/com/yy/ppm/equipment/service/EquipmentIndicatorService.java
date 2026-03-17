package com.yy.ppm.equipment.service;

import com.yy.ppm.equipment.bean.dto.EquipmentIndicatorDTO;
import com.yy.ppm.equipment.bean.dto.EquipmentIndicatorSearchDTO;

import java.util.List;

/**
 * 设备指标统计Service接口
 * @author system
 */
public interface EquipmentIndicatorService {

    /**
     * 查询设备指标统计（按月份）
     */
    List<EquipmentIndicatorDTO> getIndicatorByMonth(EquipmentIndicatorSearchDTO searchDTO);
}

