package com.yy.ppm.equipment.service.impl;

import com.yy.ppm.equipment.bean.dto.EquipmentIndicatorDTO;
import com.yy.ppm.equipment.bean.dto.EquipmentIndicatorSearchDTO;
import com.yy.ppm.equipment.mapper.EquipmentIndicatorMapper;
import com.yy.ppm.equipment.service.EquipmentIndicatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 设备指标统计Service业务层处理
 * @author system
 */
@RequiredArgsConstructor
@Service
public class EquipmentIndicatorServiceImpl implements EquipmentIndicatorService {

    @Resource
    private EquipmentIndicatorMapper mapper;

    /**
     * 查询设备指标统计（按月份）
     */
    @Override
    public List<EquipmentIndicatorDTO> getIndicatorByMonth(EquipmentIndicatorSearchDTO searchDTO) {
        return mapper.selectIndicatorByMonth(searchDTO);
    }
}

