package com.yy.ppm.equipment.service.impl;

import com.yy.ppm.equipment.bean.dto.EquipmentMaintenanceDTO;
import com.yy.ppm.equipment.bean.dto.EquipmentMaintenanceSearchDTO;
import com.yy.ppm.equipment.mapper.EquipmentMaintenanceMapper;
import com.yy.ppm.equipment.service.EquipmentMaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 设备检修历史统计Service业务层处理
 * @author system
 */
@RequiredArgsConstructor
@Service
public class EquipmentMaintenanceServiceImpl implements EquipmentMaintenanceService {

    @Resource
    private EquipmentMaintenanceMapper mapper;

    /**
     * 查询设备检修历史统计（按月份）
     */
    @Override
    public List<EquipmentMaintenanceDTO> getMaintenanceByMonth(EquipmentMaintenanceSearchDTO searchDTO) {
        return mapper.selectMaintenanceByMonth(searchDTO);
    }
}

