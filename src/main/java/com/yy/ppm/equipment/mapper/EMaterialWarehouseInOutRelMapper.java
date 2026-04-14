package com.yy.ppm.equipment.mapper;

import com.yy.framework.annotation.Edit;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 物资入库出库关系Mapper接口
 * @author system
 */
public interface EMaterialWarehouseInOutRelMapper {

    /**
     * 新增物资入库出库关系
     */
    @Edit
    int insert(com.yy.ppm.equipment.bean.po.EMaterialWarehouseInOutRelPO po);

    /**
     * 批量新增物资入库出库关系
     */
    @Edit
    int batchInsert(List<com.yy.ppm.equipment.bean.po.EMaterialWarehouseInOutRelPO> list);

    /**
     * 根据出库明细ID删除关系
     */
    int deleteByWarehouseOutDetailId(@Param("warehouseOutDetailId") Long warehouseOutDetailId);

    /**
     * 根据出库明细ID列表删除关系
     */
    int deleteByWarehouseOutDetailIds(@Param("warehouseOutDetailIds") List<Long> warehouseOutDetailIds);

    /**
     * 根据出库明细ID查询关系列表
     */
    List<com.yy.ppm.equipment.bean.po.EMaterialWarehouseInOutRelPO> selectListByWarehouseOutDetailId(@Param("warehouseOutDetailId") Long warehouseOutDetailId);

    List<Map<String, Object>> selectReservedQuantitiesByWarehouseOutIdAndMaterial(@Param("warehouseOutId") Long warehouseOutId,
                                                                                  @Param("materialId") Long materialId,
                                                                                  @Param("excludeWarehouseOutDetailId") Long excludeWarehouseOutDetailId);
}

