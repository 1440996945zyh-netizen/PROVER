package com.yy.ppm.equipment.service;

import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.SpecialEquipmentDTO;
import com.yy.ppm.equipment.bean.dto.SpecialEquipmentSearchDTO;

/**
 * 特种设备查询Service接口
 * @author system
 */
public interface SpecialEquipmentService {

    /**
     * 查询特种设备列表（分页）
     */
    Pages<SpecialEquipmentDTO> getList(SpecialEquipmentSearchDTO searchDTO);
}

