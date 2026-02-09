package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseSearchDTO;
import com.yy.ppm.equipment.bean.po.EMaterialWarehousePO;
import org.apache.ibatis.annotations.Param;

/**
 * 物资仓库Mapper接口
 * @author system
 */
public interface EMaterialWarehouseMapper {

    /**
     * 查询物资仓库列表
     */
    Page<EMaterialWarehouseDTO> selectList(EMaterialWarehouseSearchDTO searchDTO);

    /**
     * 根据ID查询物资仓库
     */
    EMaterialWarehouseDTO selectById(@Param("id") Long id);

    /**
     * 新增物资仓库
     */
    @Edit
    void insert(EMaterialWarehousePO po);

    /**
     * 修改物资仓库
     */
    @Edit
    void update(EMaterialWarehousePO po);

    /**
     * 删除物资仓库（逻辑删除）
     */
    @Edit
    void deleteById(EMaterialWarehousePO po);

    /**
     * 检查仓库编号是否重复
     */
    int countByWarehouseCode(@Param("warehouseCode") String warehouseCode,
                            @Param("id") Long id);

    /**
     * 查询物资仓库列表（不分页，用于下拉框）
     */
    java.util.List<EMaterialWarehouseDTO> selectListForSelect();
}

