package com.yy.ppm.equipment.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.MEquipmentModificationDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentModificationSearchDTO;
import com.yy.ppm.equipment.bean.po.MEquipmentModificationPO;
import org.apache.ibatis.annotations.Param;
import com.github.pagehelper.Page;
import java.util.List;

/**
 * 设备改造记录Mapper接口
 * @author system
 */
public interface MEquipmentModificationMapper {

    /**
     * 查询设备改造记录列表
     */
    Page<MEquipmentModificationDTO> selectList(MEquipmentModificationSearchDTO searchDTO);

    /**
     * 根据ID查询设备改造记录
     */
    MEquipmentModificationPO selectById(@Param("id") Long id);

    /**
     * 新增设备改造记录
     */
    @Edit
    void insert(MEquipmentModificationPO po);

    /**
     * 修改设备改造记录
     */
    @Edit
    void update(MEquipmentModificationPO po);

    /**
     * 删除设备改造记录（逻辑删除）
     */
    @Edit
    void deleteById(MEquipmentModificationDTO dto);

    /**
     * 批量删除设备改造记录（逻辑删除）
     */
    @Edit
    void deleteBatch(List<Long> ids);
}

