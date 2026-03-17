package com.yy.ppm.equipment.service;

import com.yy.ppm.equipment.bean.dto.EquipmentTypePathDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentTypeDTO;

import java.util.List;

/**
 * 设备类型分类Service接口
 * @author system
 */
public interface MEquipmentTypeService {

    /**
     * 查询设备类型分类树形列表
     */
    List<MEquipmentTypeDTO> getTreeList(String typeName);
    /**
     * 查询设备类型分类树形列表
     */
    List<MEquipmentTypeDTO> partsTree(MEquipmentTypeDTO mEquipmentTypeDTO);

    /**
     * 根据ID查询设备类型分类
     */
    MEquipmentTypeDTO getById(Long id);

    /**
     * 新增设备类型分类
     */
    void save(MEquipmentTypeDTO dto);
    void addParts(MEquipmentTypeDTO dto);

    /**
     * 删除设备类型分类
     */
    void deleteById(Long id);

    /**
     * 根据父级ID查询子级列表
     */
    List<MEquipmentTypeDTO> getByParentId(Long parentId);

    /**
     * 根据级别和父级ID查询设备类型列表
     */
    List<MEquipmentTypeDTO> getByLevelAndParent(Integer categoryLevel, Long parentId);

    /**
     * 根据小类ID获取完整路径（大类、中类、小类）
     */
    EquipmentTypePathDTO getPathBySmallCategoryId(Long smallCategoryId);
}

