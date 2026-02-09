package com.yy.ppm.equipment.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseInDetailDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物资入库明细Mapper接口
 * @author system
 */
public interface EMaterialWarehouseInDetailAppMapper {

    /**
     * 根据入库主表ID查询明细列表
     */
    List<EMaterialWarehouseInDetailDTO> selectListByWarehouseInId(@Param("warehouseInId") Long warehouseInId);

    /**
     * 根据ID查询入库明细
     */
    EMaterialWarehouseInDetailDTO selectById(@Param("id") Long id);

    /**
     * 批量新增物资入库明细
     */
    @Edit
    int batchInsert(List<com.yy.ppm.equipment.bean.po.EMaterialWarehouseInDetailPO> list);

    /**
     * 修改物资入库明细
     */
    @Edit
    int update(com.yy.ppm.equipment.bean.po.EMaterialWarehouseInDetailPO po);

    /**
     * 根据入库主表ID删除明细
     */
    int deleteByWarehouseInId(@Param("warehouseInId") Long warehouseInId);

    /**
     * 根据ID删除明细
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据采购明细ID查询已入库数量总和（排除当前入库单）
     * @param purchaseDetailId 采购明细ID
     * @param excludeWarehouseInId 排除的入库单ID（可为null）
     * @return 已入库数量总和
     */
    java.math.BigDecimal sumWarehouseInQuantityByPurchaseDetailId(@Param("purchaseDetailId") Long purchaseDetailId, @Param("excludeWarehouseInId") Long excludeWarehouseInId);

    /**
     * 根据申报明细ID查询采购明细ID和采购数量
     * @param applicationId 申报明细ID
     * @return 包含采购明细ID和采购数量的Map，key为purchaseDetailId和purchaseQuantity
     */
    java.util.Map<String, Object> getPurchaseDetailInfoByApplicationId(@Param("applicationId") Long applicationId);

    /**
     * 检查采购明细是否有入库记录
     * @param purchaseDetailId 采购明细ID
     * @return 入库记录数量
     */
    int countByPurchaseDetailId(@Param("purchaseDetailId") Long purchaseDetailId);

    /**
     * 查询物资库存数量（按物资ID和仓库ID）
     * @param materialId 物资ID（关联E_MATERIAL_CODE表）
     * @param warehouseId 仓库ID
     * @return 库存数量 = SUM(入库数量) - SUM(已出库数量)
     */
    java.math.BigDecimal getStockQuantity(@Param("materialId") Long materialId, @Param("warehouseId") Long warehouseId);

    /**
     * 批量查询物资库存数量（按物资ID列表和仓库ID）
     * @param materialIds 物资ID列表（关联E_MATERIAL_CODE表）
     * @param warehouseId 仓库ID
     * @return Map列表，每个Map包含materialId和stockQuantity
     */
    List<java.util.Map<String, Object>> batchGetStockQuantity(@Param("materialIds") List<Long> materialIds, @Param("warehouseId") Long warehouseId);

    /**
     * 查询可用的入库明细（按FIFO原则，优先使用创建时间早的，且未出库数量大于0的）
     * @param materialId 物资ID
     * @param warehouseId 仓库ID
     * @return 入库明细列表，按创建时间升序排列
     */
    List<EMaterialWarehouseInDetailDTO> selectAvailableInDetails(@Param("materialId") Long materialId, @Param("warehouseId") Long warehouseId);

    /**
     * 批量更新入库明细的已出库数量和未出库数量
     * @param detailList 入库明细列表（包含id、outQuantity、remainingQuantity）
     */
    @Edit
    int batchUpdateOutQuantity(List<com.yy.ppm.equipment.bean.po.EMaterialWarehouseInDetailPO> detailList);

    /**
     * 根据设备ID查询备品备件列表（通过申报明细表的equipIds字段关联）
     * @param equipId 设备ID
     * @param materialName 物资名称（可选，用于模糊查询）
     * @param warehouseName 仓库名称（可选，用于过滤）
     * @return 备品备件列表
     */
    List<com.yy.ppm.equipment.bean.dto.EquipmentSpareDTO> selectSpareListByEquipId(
            @Param("equipId") String equipId,
            @Param("materialName") String materialName,
            @Param("warehouseName") String warehouseName
    );
}

