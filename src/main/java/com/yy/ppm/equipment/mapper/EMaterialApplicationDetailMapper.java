package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMaterialApplicationDetailDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialApplicationDetailForWarehouseInDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialApplicationDetailForWarehouseInSearchDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialApplicationDetailSearchDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物资申报明细Mapper接口
 * @author system
 */
public interface EMaterialApplicationDetailMapper {

    /**
     * 根据申请表ID查询明细列表
     */
    List<EMaterialApplicationDetailDTO> selectListByApplicationId(@Param("applicationId") Long applicationId);

    /**
     * 根据申请表ID查询明细列表（包含库存数量）
     * @param applicationId 申请表ID
     * @param warehouseId 仓库ID（用于查询库存数量，可为null）
     */
    List<EMaterialApplicationDetailDTO> selectListByApplicationIdWithStock(@Param("applicationId") Long applicationId, @Param("warehouseId") Long warehouseId);

    /**
     * 查询申报物资明细列表（用于采购时选择，关联物资申报表，只查询已审批通过的）
     */
    Page<EMaterialApplicationDetailDTO> selectDetailListForPurchase(EMaterialApplicationDetailSearchDTO searchDTO);

    /**
     * 新增物资申报明细
     */
    @Edit
    int insert(com.yy.ppm.equipment.bean.po.EMaterialApplicationDetailPO po);


    /**
     * 修改物资申报明细
     */
    @Edit
    int update(com.yy.ppm.equipment.bean.po.EMaterialApplicationDetailPO po);

    /**
     * 删除物资申报明细（根据申请表ID）
     */
    int deleteByApplicationId(@Param("applicationId") Long applicationId);

    /**
     * 删除物资申报明细（根据ID）
     */
    int deleteById(@Param("id") Long id);

    /**
     * 更新物资申报明细的采购明细ID
     */
    @Edit
    int updatePurchaseDetailId(@Param("id") Long id, @Param("purchaseDetailId") Long purchaseDetailId);

    /**
     * 批量更新物资申报明细的采购明细ID
     * @param updateList 更新列表，每个元素包含 id 和 purchaseDetailId
     */
    @Edit
    int batchUpdatePurchaseDetailId(@Param("updateList") List<java.util.Map<String, Object>> updateList);

    /**
     * 清空物资申报明细的采购明细ID（根据采购明细ID）
     */
    @Edit
    int clearPurchaseDetailIdByPurchaseDetailId(@Param("purchaseDetailId") Long purchaseDetailId);

    /**
     * 查询物资申报明细关联采购明细列表（用于入库时选择，关联物资申报表、采购明细表、采购主表）
     */
    Page<EMaterialApplicationDetailForWarehouseInDTO> selectDetailListForWarehouseIn(EMaterialApplicationDetailForWarehouseInSearchDTO searchDTO);

    /**
     * 根据ID查询申请单明细的规格描述
     */
    String selectSpecificationDescById(@Param("id") Long id);
}

