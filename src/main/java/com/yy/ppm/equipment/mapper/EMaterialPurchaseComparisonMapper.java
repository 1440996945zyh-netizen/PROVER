package com.yy.ppm.equipment.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMaterialPurchaseComparisonDTO;
import com.yy.ppm.equipment.bean.po.EMaterialPurchaseComparisonPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物资采购比价信息Mapper接口
 * @author system
 */
public interface EMaterialPurchaseComparisonMapper {

    /**
     * 根据采购主表ID查询比价信息列表
     */
    List<EMaterialPurchaseComparisonDTO> selectListByPurchaseId(@Param("purchaseId") Long purchaseId);

    /**
     * 新增物资采购比价信息
     */
    @Edit
    int insert(EMaterialPurchaseComparisonPO po);

    /**
     * 批量新增物资采购比价信息
     */
    @Edit
    int batchInsert(@Param("list") List<EMaterialPurchaseComparisonPO> list);

    /**
     * 修改物资采购比价信息
     */
    @Edit
    int update(EMaterialPurchaseComparisonPO po);

    /**
     * 删除物资采购比价信息（根据采购主表ID）
     */
    int deleteByPurchaseId(@Param("purchaseId") Long purchaseId);

    /**
     * 删除物资采购比价信息（根据ID）
     */
    int deleteById(@Param("id") Long id);
}

