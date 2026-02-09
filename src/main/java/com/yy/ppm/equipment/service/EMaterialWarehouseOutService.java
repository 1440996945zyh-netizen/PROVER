package com.yy.ppm.equipment.service;

import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseOutDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseOutSearchDTO;

/**
 * 物资出库Service接口
 * @author system
 */
public interface EMaterialWarehouseOutService {

    /**
     * 查询物资出库列表（分页）
     */
    Pages<EMaterialWarehouseOutDTO> getList(EMaterialWarehouseOutSearchDTO searchDTO);

    /**
     * 根据ID查询物资出库（包含明细）
     */
    EMaterialWarehouseOutDTO getById(Long id);

    /**
     * 新增或修改物资出库
     */
    void save(EMaterialWarehouseOutDTO dto);

    /**
     * 删除物资出库
     */
    void deleteById(Long id);

    /**
     * 确认物资出库
     */
    void confirm(Long id);
}

