package com.yy.ppm.equipment.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseOutDetailDTO;
import com.yy.ppm.equipment.bean.po.EMaterialWarehouseOutDetailPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物资出库明细Mapper接口
 * @author system
 */
public interface EMaterialWarehouseOutDetailMapper {

    /**
     * 根据出库主表ID查询明细列表
     */
    List<EMaterialWarehouseOutDetailDTO> selectListByWarehouseOutId(@Param("warehouseOutId") Long warehouseOutId);

    /**
     * 新增物资出库明细
     */
    @Edit
    int insert(EMaterialWarehouseOutDetailPO po);

    /**
     * 批量新增物资出库明细
     */
    @Edit
    int batchInsert(List<EMaterialWarehouseOutDetailPO> list);

    /**
     * 修改物资出库明细
     */
    @Edit
    int update(EMaterialWarehouseOutDetailPO po);

    /**
     * 根据出库主表ID删除明细
     */
    int deleteByWarehouseOutId(@Param("warehouseOutId") Long warehouseOutId);

    /**
     * 根据ID删除明细
     */
    int deleteById(@Param("id") Long id);
}

