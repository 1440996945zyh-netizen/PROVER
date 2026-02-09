package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.ppm.equipment.bean.dto.SpecialEquipmentDTO;
import com.yy.ppm.equipment.bean.dto.SpecialEquipmentSearchDTO;

/**
 * 特种设备查询Mapper接口
 * @author system
 */
public interface SpecialEquipmentMapper {

    /**
     * 查询特种设备列表（分页）
     */
    Page<SpecialEquipmentDTO> selectList(SpecialEquipmentSearchDTO searchDTO);
}

