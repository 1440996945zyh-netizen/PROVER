package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.InspectionStandardDTO;
import com.yy.ppm.equipment.bean.po.InspectionStandardPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InspectionStandardMapper {
    List<InspectionStandardPO> queryByUnitId(InspectionStandardDTO inspectionStandardDTO);

    void deleteByUnitId(InspectionStandardDTO dto);

    @Edit
    void save(@Param("list") List<InspectionStandardPO> list);

    InspectionStandardPO queryParentByUnitId(InspectionStandardDTO dto);

    <T> Page<T> queryAll(InspectionStandardDTO inspectionStandardDTO);
}
