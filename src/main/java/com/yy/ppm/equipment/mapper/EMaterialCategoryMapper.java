package com.yy.ppm.equipment.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMaterialCategoryDTO;
import com.yy.ppm.equipment.bean.po.EMaterialCategoryPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物资类别Mapper接口
 * @author system
 */
public interface EMaterialCategoryMapper {

    /**
     * 查询物资类别树形列表
     */
    List<EMaterialCategoryDTO> selectMaterialCategoryTree(@Param("categoryName") String categoryName);

    /**
     * 根据父级ID查询子级列表
     */
    List<EMaterialCategoryDTO> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 根据ID查询物资类别
     */
    EMaterialCategoryDTO selectById(@Param("id") Long id);

    /**
     * 新增物资类别
     */
    @Edit
    void insert(EMaterialCategoryPO po);

    /**
     * 修改物资类别
     */
    @Edit
    void update(EMaterialCategoryPO po);

    /**
     * 删除物资类别（逻辑删除）
     */
    @Edit
    void deleteById(EMaterialCategoryPO po);

    /**
     * 检查是否有子级
     */
    int countByParentId(@Param("parentId") Long parentId);

    /**
     * 检查同级下名称是否重复
     */
    int countByNameAndParent(@Param("categoryName") String categoryName, 
                            @Param("parentId") Long parentId, 
                            @Param("id") Long id);

    /**
     * 检查同级下编码是否重复
     */
    int countByCodeAndParent(@Param("categoryCode") String categoryCode, 
                             @Param("parentId") Long parentId, 
                             @Param("id") Long id);

    /**
     * 检查全表中编码是否重复（排除当前ID）
     */
    int countByCode(@Param("categoryCode") String categoryCode, 
                    @Param("id") Long id);

    /**
     * 根据级别和父级ID查询物资类别列表
     */
    List<EMaterialCategoryDTO> selectByLevelAndParent(@Param("categoryLevel") Integer categoryLevel, 
                                                      @Param("parentId") Long parentId);

    /**
     * 更新父级的CODE_COUNT
     */
    @Edit
    void updateParentCodeCount(@Param("parentId") Long parentId, @Param("codeCount") Integer codeCount);
}

