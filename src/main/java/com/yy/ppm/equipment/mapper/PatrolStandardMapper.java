package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.PatrolStandardDTO;
import com.yy.ppm.equipment.bean.po.PatrolStandardSubPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 巡检标准
 */
public interface PatrolStandardMapper {

    Page<PatrolStandardDTO> selectList(PatrolStandardDTO searchDTO);

    PatrolStandardDTO selectById(@Param("id") Long id);

    List<PatrolStandardSubPO> selectSubListByParentId(@Param("parentId") Long parentId);

    @Edit
    void add(PatrolStandardDTO dto);

    @Edit
    void update(PatrolStandardDTO dto);

    void insertSubBatch(@Param("parentId") Long parentId, @Param("list") List<PatrolStandardSubPO> list);

    void deleteSubByParentId(@Param("parentId") Long parentId);

    @Edit
    void delete(@Param("id") Long id);
}
