package com.yy.ppm.equipment.service;

import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.MEquipmentModificationDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentModificationSearchDTO;

import java.util.List;

/**
 * 设备改造记录Service接口
 * @author system
 */
public interface MEquipmentModificationService {

    /**
     * 查询设备改造记录列表（分页）
     */
    Pages<MEquipmentModificationDTO> getList(MEquipmentModificationSearchDTO searchDTO);

    /**
     * 根据ID查询设备改造记录
     */
    MEquipmentModificationDTO getById(Long id);

    /**
     * 新增设备改造记录
     */
    void add(MEquipmentModificationDTO dto);

    /**
     * 修改设备改造记录
     */
    void update(MEquipmentModificationDTO dto);

    /**
     * 删除设备改造记录
     */
    void delete(Long id);

    /**
     * 批量删除设备改造记录
     */
    void deleteBatch(List<Long> ids);
}

