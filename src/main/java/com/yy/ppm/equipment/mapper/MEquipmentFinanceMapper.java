package com.yy.ppm.equipment.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.po.MEquipmentFinancePO;
import org.apache.ibatis.annotations.Param;

/**
 * 设备财务信息Mapper接口
 * @author system
 */
public interface MEquipmentFinanceMapper {

    /**
     * 根据设备ID查询财务信息
     */
    MEquipmentFinancePO selectByEquipId(@Param("equipId") Long equipId);

    /**
     * 新增设备财务信息
     */
    @Edit
    void insert(MEquipmentFinancePO po);

    /**
     * 修改设备财务信息
     */
    @Edit
    void update(MEquipmentFinancePO po);

    /**
     * 删除设备财务信息（逻辑删除）
     */
    @Edit
    void deleteByEquipId(MEquipmentFinancePO po);
}

