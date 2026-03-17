package com.yy.ppm.equipment.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMaintPartReplaceDTO;
import com.yy.ppm.equipment.bean.dto.EMaintPartReplaceQueryDTO;
import com.yy.ppm.equipment.bean.po.EMaintPartReplacePO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 设备维修配件更换Mapper
 * @author system
 * @version 1.0.0
 * @Description
 */
public interface EMaintPartReplaceMapper {

    /**
     * 根据维修信息ID查询配件更换列表
     */
    List<EMaintPartReplaceDTO> selectListByMaintInfoId(@Param("maintInfoId") Long maintInfoId);

    /**
     * 根据设备ID查询可用的出库单和申领单明细（用于配件更换选择）
     * 查询条件：
     * 1. 出库单明细中的设备ID匹配（通过EQUIP_IDS字段）
     * 2. 出库单状态为已审核通过（状态为'3'）
     * 3. 出库单明细已出库（OUT_QUANTITY_SUM > 0）
     */
    List<EMaintPartReplaceQueryDTO> selectAvailableDetailsByEquipId(@Param("equipId") Long equipId);

    /**
     * 新增配件更换记录
     */
    @Edit
    int insert(EMaintPartReplacePO po);

    /**
     * 修改配件更换记录
     */
    @Edit
    int update(EMaintPartReplacePO po);

    /**
     * 删除配件更换记录（根据维修信息ID，物理删除）
     */
    int deleteByMaintInfoId(@Param("maintInfoId") Long maintInfoId);

    /**
     * 根据出库单明细ID查询已使用数量总和
     */
    java.math.BigDecimal selectTotalUsedQuantityByWarehouseOutDetailId(@Param("warehouseOutDetailId") Long warehouseOutDetailId);
}

