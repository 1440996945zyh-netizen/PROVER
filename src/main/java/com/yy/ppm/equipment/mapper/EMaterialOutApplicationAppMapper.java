package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMaterialOutApplicationDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialOutApplicationSearchDTO;
import com.yy.ppm.equipment.bean.po.EMaterialOutApplicationPO;
import org.apache.ibatis.annotations.Param;

/**
 * 物资出库申请Mapper接口
 * @author system
 */
public interface EMaterialOutApplicationAppMapper {

    /**
     * 查询物资出库申请列表（分页）
     */
    Page<EMaterialOutApplicationDTO> selectList(EMaterialOutApplicationSearchDTO searchDTO);

    /**
     * 根据ID查询物资出库申请
     */
    EMaterialOutApplicationDTO selectById(@Param("id") Long id);

    /**
     * 新增物资出库申请
     */
    @Edit
    int insert(EMaterialOutApplicationPO po);

    /**
     * 修改物资出库申请
     */
    @Edit
    int update(EMaterialOutApplicationPO po);

    /**
     * 删除物资出库申请（逻辑删除）
     */
    @Edit
    int deleteById(EMaterialOutApplicationPO po);

    /**
     * 检查出库单号是否重复
     */
    int countByWarehouseOutNo(@Param("warehouseOutNo") String warehouseOutNo, @Param("id") Long id);

    /**
     * 查询物资出库申请列表（包含明细列表和库存数量，用于出库时选择）
     */
    Page<EMaterialOutApplicationDTO> selectListWithDetails(EMaterialOutApplicationSearchDTO searchDTO);
}

