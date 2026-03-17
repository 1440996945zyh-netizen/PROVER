package com.yy.ppm.equipment.mapper;

import com.yy.ppm.equipment.bean.dto.EquipmentIndicatorDTO;
import com.yy.ppm.equipment.bean.dto.EquipmentIndicatorSearchDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 设备指标统计Mapper接口
 * @author system
 */
public interface EquipmentIndicatorMapper {

    /**
     * 查询设备指标统计（按月份）
     */
    List<EquipmentIndicatorDTO> selectIndicatorByMonth(EquipmentIndicatorSearchDTO searchDTO);
}

