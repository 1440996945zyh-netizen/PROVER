package com.yy.ppm.equipment.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMaterialOutApplicationDetailDTO;
import com.yy.ppm.equipment.bean.po.EMaterialOutApplicationPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物资出库申请明细Mapper接口
 * @author system
 */
public interface EMaterialOutApplicationDetailMapper {

    /**
     * 根据出库申请主表ID查询明细列表
     */
    List<EMaterialOutApplicationDetailDTO> selectListByOutApplicationId(@Param("outApplicationId") Long outApplicationId);

    /**
     * 新增物资出库申请明细
     */
    @Edit
    int insert(com.yy.ppm.equipment.bean.po.EMaterialOutApplicationDetailPO po);

    /**
     * 修改物资出库申请明细
     */
    @Edit
    int update(com.yy.ppm.equipment.bean.po.EMaterialOutApplicationDetailPO po);

    /**
     * 删除物资出库申请明细（根据出库申请主表ID）
     */
    @Edit
    int deleteByOutApplicationId(EMaterialOutApplicationPO po);

    /**
     * 删除物资出库申请明细（根据ID）
     */
    @Edit
    int deleteById(EMaterialOutApplicationDetailDTO dto);

    /**
     * 累加已出库数量
     */
    @Edit
    int addOutQuantitySum(@Param("id") Long id, @Param("outQuantity") java.math.BigDecimal outQuantity);

    /**
     * 减少已出库数量
     */
    @Edit
    int subtractOutQuantitySum(@Param("id") Long id, @Param("outQuantity") java.math.BigDecimal outQuantity);

    /**
     * 查询已出库数量（根据物资ID和仓库ID，统计已审批通过的出库申请明细的申请数量总和，排除指定的出库申请ID）
     * @param materialId 物资ID
     * @param warehouseId 仓库ID
     * @param excludeOutApplicationId 排除的出库申请ID（编辑时排除当前申请）
     * @return 已出库数量总和
     */
    java.math.BigDecimal selectOutQuantityByMaterialAndWarehouse(
            @Param("materialId") Long materialId,
            @Param("warehouseId") Long warehouseId,
            @Param("excludeOutApplicationId") Long excludeOutApplicationId
    );
}

