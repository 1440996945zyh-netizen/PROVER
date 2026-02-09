package com.yy.ppm.equipment.mapper;

import com.yy.ppm.equipment.bean.dto.EquipmentMaintenanceDTO;
import com.yy.ppm.equipment.bean.dto.EquipmentMaintenanceSearchDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 设备检修历史统计Mapper接口
 * @author system
 */
public interface EquipmentMaintenanceMapper {

    /**
     * 查询设备检修历史统计（按月份）
     */
    List<EquipmentMaintenanceDTO> selectMaintenanceByMonth(EquipmentMaintenanceSearchDTO searchDTO);
}

