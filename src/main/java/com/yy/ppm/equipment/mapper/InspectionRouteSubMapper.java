package com.yy.ppm.equipment.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.InspectionRouteSubDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InspectionRouteSubMapper {

    @Edit
    void insert(InspectionRouteSubDTO dto);

    void deleteByParentId(Long parentId);

    List<InspectionRouteSubDTO> listByParentId(Long parentId);
}