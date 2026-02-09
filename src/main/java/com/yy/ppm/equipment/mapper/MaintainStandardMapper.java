package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.MaintainStandardDTO;
import com.yy.ppm.equipment.bean.po.MaintainStandardPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MaintainStandardMapper {
    List<MaintainStandardPO> queryByUnitId(MaintainStandardDTO maintainStandardDTO);

    void deleteByUnitId(MaintainStandardDTO dto);

    @Edit
    void save(@Param("list") List<MaintainStandardPO> list);

    MaintainStandardPO queryParentByUnitId(MaintainStandardDTO dto);

    <T> Page<T> queryAll(MaintainStandardDTO maintainStandardDTO);
}
