package com.yy.ppm.equipment.service;

import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseInDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseInSearchDTO;

/**
 * 物资入库Service接口
 * @author system
 */
public interface EMaterialWarehouseInAppService {

    /**
     * 查询物资入库列表（分页）
     */
    Pages<EMaterialWarehouseInDTO> getList(EMaterialWarehouseInSearchDTO searchDTO);

    /**
     * 根据ID查询物资入库（包含明细）
     */
    EMaterialWarehouseInDTO getById(Long id);

    /**
     * 新增或修改物资入库
     */
    void save(EMaterialWarehouseInDTO dto);

    /**
     * 删除物资入库
     */
    void deleteById(Long id);

    /**
     * 验收物资入库
     */
    void acceptance(com.yy.ppm.equipment.bean.dto.EMaterialWarehouseInAcceptanceDTO dto);

    /**
     * 查询物资库存数量（按物资ID和仓库ID）
     * @param materialId 物资ID
     * @param warehouseId 仓库ID
     * @return 库存数量
     */
    java.math.BigDecimal getStockQuantity(Long materialId, Long warehouseId);
}

