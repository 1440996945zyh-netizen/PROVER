package com.yy.ppm.equipment.service;

import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMaterialStockDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialStockDetailDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialStockFlowDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialStockSearchDTO;

import java.util.List;

/**
 * 物资库存Service接口
 * @author system
 */
public interface EMaterialStockService {

    /**
     * 查询物资库存列表（分页）
     */
    Pages<EMaterialStockDTO> getList(EMaterialStockSearchDTO searchDTO);

    /**
     * 查询物资库存明细列表（根据仓库ID和物资ID）
     */
    List<EMaterialStockDetailDTO> getStockDetailList(Long warehouseId, Long materialId, String warehouseInTimeStart, String warehouseInTimeEnd);

    /**
     * 查询物资库存流水列表（根据仓库ID和物资ID）
     */
    List<EMaterialStockFlowDTO> getStockFlowList(Long warehouseId, Long materialId, String warehouseInTimeStart, String warehouseInTimeEnd);

    /**
     * 导出物资库存列表
     */
    byte[] pageExport(EMaterialStockSearchDTO searchDTO);
}

