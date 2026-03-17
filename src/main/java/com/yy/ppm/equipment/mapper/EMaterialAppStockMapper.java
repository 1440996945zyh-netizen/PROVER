package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.ppm.equipment.bean.dto.EMaterialStockDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialStockDetailDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialStockFlowDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialStockSearchDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物资库存Mapper接口
 * @author system
 */
public interface EMaterialAppStockMapper {

    /**
     * 查询物资库存列表（分页）
     */
    List<EMaterialStockDTO> selectStockList(EMaterialStockSearchDTO searchDTO);

    /**
     * 查询物资库存明细列表（根据仓库ID和物资ID）
     */
    List<EMaterialStockDetailDTO> selectStockDetailList(@Param("warehouseId") Long warehouseId, @Param("materialId") Long materialId, @Param("warehouseInTimeStart") String warehouseInTimeStart, @Param("warehouseInTimeEnd") String warehouseInTimeEnd);

    /**
     * 查询物资库存流水列表（根据仓库ID和物资ID）
     */
    List<EMaterialStockFlowDTO> selectStockFlowList(@Param("warehouseId") Long warehouseId, @Param("materialId") Long materialId, @Param("warehouseInTimeStart") String warehouseInTimeStart, @Param("warehouseInTimeEnd") String warehouseInTimeEnd);
}

