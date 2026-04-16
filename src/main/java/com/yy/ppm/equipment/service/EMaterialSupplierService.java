package com.yy.ppm.equipment.service;

import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMaterialSupplierDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialSupplierSearchDTO;

/**
 * 供应商Service
 */
public interface EMaterialSupplierService {

    /**
     * 列表查询
     */
    Pages<EMaterialSupplierDTO> getList(EMaterialSupplierSearchDTO searchDTO);

    /**
     * 详情
     */
    EMaterialSupplierDTO getById(Long id);

    /**
     * 新增/修改
     */
    void save(EMaterialSupplierDTO dto);

    /**
     * 作废
     */
    void deleteById(Long id);
}
