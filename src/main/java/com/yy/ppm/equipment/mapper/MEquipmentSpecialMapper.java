package com.yy.ppm.equipment.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.MEquipmentSpecialDTO;
import com.yy.ppm.equipment.bean.po.MEquipmentSpecialPO;
import org.apache.ibatis.annotations.Param;

/**
 * 特种设备Mapper接口
 * @author system
 */
public interface MEquipmentSpecialMapper {

    /**
     * 根据设备ID查询特种设备信息
     */
    MEquipmentSpecialPO selectByEquipId(@Param("equipId") Long equipId);

    /**
     * 新增特种设备信息
     */
    @Edit
    void insert(MEquipmentSpecialPO po);

    /**
     * 修改特种设备信息
     */
    @Edit
    void update(MEquipmentSpecialPO po);

    /**
     * 删除特种设备信息（逻辑删除）
     */
    @Edit
    void deleteByEquipId(MEquipmentSpecialDTO mEquipmentSpecialDTO);
}

