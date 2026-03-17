package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EquipmentSelectDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentInfoDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentInfoSearchDTO;
import com.yy.ppm.equipment.bean.po.MEquipmentInfoPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 设备台账信息Mapper接口
 * @author system
 */
public interface MEquipmentInfoMapper {

    /**
     * 查询设备台账信息列表（分页）
     */
    Page<MEquipmentInfoDTO> selectList(MEquipmentInfoSearchDTO searchDTO);

    /**
     * 根据ID查询设备台账信息
     */
    MEquipmentInfoDTO selectById(@Param("id") Long id);

    /**
     * 新增设备台账信息
     */
    @Edit
    void insert(MEquipmentInfoPO po);

    /**
     * 修改设备台账信息
     */
    @Edit
    void update(MEquipmentInfoPO po);

    /**
     * 删除设备台账信息（逻辑删除）
     */
    @Edit
    void deleteById(MEquipmentInfoPO po);

    /**
     * 检查设备编码是否重复
     */
    int countByEquipCode(@Param("equipCode") String equipCode, @Param("id") Long id);

    /**
     * 查询设备选择列表（用于下拉框）
     * @param keyword 搜索关键词（设备名称或编码）
     */
    List<EquipmentSelectDTO> selectEquipmentList(@Param("keyword") String keyword);
}

