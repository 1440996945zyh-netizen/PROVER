package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.MEpatrolStandardDTO;
import com.yy.ppm.equipment.bean.po.MEpatrolStandardSubPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 巡检标准
 */
public interface MEpatrolStandardMapper {

    Page<MEpatrolStandardDTO> selectList(MEpatrolStandardDTO searchDTO);

    MEpatrolStandardDTO selectById(@Param("id") Long id);

    List<MEpatrolStandardSubPO> selectSubListByParentId(@Param("parentId") Long parentId);

    @Edit
    void add(MEpatrolStandardDTO dto);

    @Edit
    void update(MEpatrolStandardDTO dto);

    void insertSubBatch(@Param("parentId") Long parentId, @Param("list") List<MEpatrolStandardSubPO> list);

    void deleteSubByParentId(@Param("parentId") Long parentId);

    @Edit
    void delete(@Param("id") Long id);
}
