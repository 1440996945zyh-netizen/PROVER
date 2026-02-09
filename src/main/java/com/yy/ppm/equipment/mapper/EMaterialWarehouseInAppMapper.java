package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseInDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseInSearchDTO;
import org.apache.ibatis.annotations.Param;

/**
 * 物资入库Mapper接口
 * @author system
 */
public interface EMaterialWarehouseInAppMapper {

    /**
     * 查询物资入库列表（分页）
     */
    Page<EMaterialWarehouseInDTO> selectList(EMaterialWarehouseInSearchDTO searchDTO);

    /**
     * 根据ID查询物资入库
     */
    EMaterialWarehouseInDTO selectById(@Param("id") Long id);

    /**
     * 新增物资入库
     */
    @Edit
    int insert(com.yy.ppm.equipment.bean.po.EMaterialWarehouseInPO po);

    /**
     * 修改物资入库
     */
    @Edit
    int update(com.yy.ppm.equipment.bean.po.EMaterialWarehouseInPO po);

    /**
     * 删除物资入库
     */
    int deleteById(@Param("id") Long id);

    /**
     * 检查入库单号是否重复
     */
    int countByWarehouseInNo(@Param("warehouseInNo") String warehouseInNo, @Param("id") Long id);
}

