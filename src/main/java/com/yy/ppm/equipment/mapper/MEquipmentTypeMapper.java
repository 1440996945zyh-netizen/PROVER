package com.yy.ppm.equipment.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.MEquipmentTypeDTO;
import com.yy.ppm.equipment.bean.po.MEquipmentTypePO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 设备类型分类Mapper接口
 * @author system
 */
public interface MEquipmentTypeMapper {

    /**
     * 查询设备类型分类树形列表
     */
    List<MEquipmentTypeDTO> selectEquipmentTypeTree(@Param("typeName") String typeName);
    List<MEquipmentTypeDTO> partsTree(MEquipmentTypeDTO mEquipmentTypeDTO);

    /**
     * 根据父级ID查询子级列表
     */
    List<MEquipmentTypeDTO> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 根据ID查询设备类型分类
     */
    MEquipmentTypeDTO selectById(@Param("id") Long id);

    /**
     * 新增设备类型分类
     */
    @Edit
    void insert(MEquipmentTypePO po);

    /**
     * 修改设备类型分类
     */
    @Edit
    void update(MEquipmentTypePO po);

    /**
     * 删除设备类型分类（逻辑删除）
     */
    @Edit
    void deleteById(MEquipmentTypePO po);

    /**
     * 检查是否有子级
     */
    int countByParentId(@Param("parentId") Long parentId);

    /**
     * 检查同级下名称是否重复
     */
    int countByNameAndParent(@Param("typeName") String typeName, 
                            @Param("parentId") Long parentId, 
                            @Param("id") Long id);

    /**
     * 根据级别和父级ID查询设备类型列表
     */
    List<MEquipmentTypeDTO> selectByLevelAndParent(@Param("categoryLevel") Integer categoryLevel, 
                                                   @Param("parentId") Long parentId);
}

