package com.yy.ppm.equipment.service;

import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseSearchDTO;

/**
 * 物资仓库Service接口
 * @author system
 */
public interface EMaterialWarehouseService {

    /**
     * 查询物资仓库列表（分页）
     */
    Pages<EMaterialWarehouseDTO> getList(EMaterialWarehouseSearchDTO searchDTO);

    /**
     * 根据ID查询物资仓库
     */
    EMaterialWarehouseDTO getById(Long id);

    /**
     * 新增物资仓库
     */
    void save(EMaterialWarehouseDTO dto);

    /**
     * 删除物资仓库
     */
    void deleteById(Long id);

    /**
     * 查询物资仓库列表（不分页，用于下拉框）
     */
    java.util.List<EMaterialWarehouseDTO> getListForSelect();
}

