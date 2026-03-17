package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseOutDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseOutSearchDTO;
import org.apache.ibatis.annotations.Param;

/**
 * 物资出库Mapper接口
 * @author system
 */
public interface EMaterialWarehouseOutAppMapper {

    /**
     * 查询物资出库列表（分页）
     */
    Page<EMaterialWarehouseOutDTO> selectList(EMaterialWarehouseOutSearchDTO searchDTO);

    /**
     * 根据ID查询物资出库
     */
    EMaterialWarehouseOutDTO selectById(@Param("id") Long id);

    /**
     * 新增物资出库
     */
    @Edit
    int insert(com.yy.ppm.equipment.bean.po.EMaterialWarehouseOutPO po);

    /**
     * 修改物资出库
     */
    @Edit
    int update(com.yy.ppm.equipment.bean.po.EMaterialWarehouseOutPO po);

    /**
     * 删除物资出库
     */
    int deleteById(@Param("id") Long id);

    /**
     * 检查出库单号是否重复
     */
    int countByWarehouseOutNo(@Param("warehouseOutNo") String warehouseOutNo, @Param("id") Long id);
}

