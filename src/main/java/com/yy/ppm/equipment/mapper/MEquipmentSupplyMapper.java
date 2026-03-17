package com.yy.ppm.equipment.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.MEquipmentSupplyDTO;
import com.yy.ppm.equipment.bean.po.MEquipmentSupplyPO;
import org.apache.ibatis.annotations.Param;

/**
 * 设备供货信息Mapper接口
 * @author system
 */
public interface MEquipmentSupplyMapper {

    /**
     * 根据设备ID查询供货信息
     */
    MEquipmentSupplyPO selectByEquipId(@Param("equipId") Long equipId);

    /**
     * 新增设备供货信息
     */
    @Edit
    void insert(MEquipmentSupplyPO po);

    /**
     * 修改设备供货信息
     */
    @Edit
    void update(MEquipmentSupplyPO po);

    /**
     * 删除设备供货信息（逻辑删除）
     */
    @Edit
    void deleteByEquipId(MEquipmentSupplyDTO mEquipmentSupplyDTO);
}

