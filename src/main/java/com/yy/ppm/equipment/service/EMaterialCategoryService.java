package com.yy.ppm.equipment.service;

import com.yy.ppm.equipment.bean.dto.EMaterialCategoryDTO;

import java.util.List;

/**
 * 物资类别Service接口
 * @author system
 */
public interface EMaterialCategoryService {

    /**
     * 查询物资类别树形列表
     */
    List<EMaterialCategoryDTO> getTreeList(String categoryName);

    /**
     * 根据ID查询物资类别
     */
    EMaterialCategoryDTO getById(Long id);

    /**
     * 新增物资类别
     */
    void save(EMaterialCategoryDTO dto);

    /**
     * 删除物资类别
     */
    void deleteById(Long id);

    /**
     * 根据父级ID查询子级列表
     */
    List<EMaterialCategoryDTO> getByParentId(Long parentId);

    /**
     * 根据级别和父级ID查询物资类别列表
     */
    List<EMaterialCategoryDTO> getByLevelAndParent(Integer categoryLevel, Long parentId);

    /**
     * 更新类别的CODE_COUNT
     */
    void updateCodeCount(Long categoryId, Integer codeCount);
}

