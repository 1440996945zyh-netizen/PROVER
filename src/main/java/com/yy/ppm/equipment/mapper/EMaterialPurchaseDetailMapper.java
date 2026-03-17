package com.yy.ppm.equipment.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMaterialPurchaseDetailDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物资采购明细Mapper接口
 * @author system
 */
public interface EMaterialPurchaseDetailMapper {

    /**
     * 根据采购表ID查询明细列表
     */
    List<EMaterialPurchaseDetailDTO> selectListByPurchaseId(@Param("purchaseId") Long purchaseId);

    /**
     * 新增物资采购明细
     */
    @Edit
    int insert(com.yy.ppm.equipment.bean.po.EMaterialPurchaseDetailPO po);

    /**
     * 批量新增物资采购明细
     */
    @Edit
    int batchInsert(@Param("list") List<com.yy.ppm.equipment.bean.po.EMaterialPurchaseDetailPO> list);

    /**
     * 修改物资采购明细
     */
    @Edit
    int update(com.yy.ppm.equipment.bean.po.EMaterialPurchaseDetailPO po);

    /**
     * 删除物资采购明细（根据采购表ID）
     */
    int deleteByPurchaseId(@Param("purchaseId") Long purchaseId);

    /**
     * 删除物资采购明细（根据ID）
     */
    int deleteById(@Param("id") Long id);
}

