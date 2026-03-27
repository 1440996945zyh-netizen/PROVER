package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.InspectionRouteDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InspectionRouteMapper {

    @Edit
    void insert(InspectionRouteDTO dto);

    @Edit
    void update(InspectionRouteDTO dto);

    @Edit
    Integer deleteById(Long id);

    InspectionRouteDTO getDetail(Long id);

    Page<InspectionRouteDTO> getList(InspectionRouteDTO dto);

    List<InspectionRouteDTO> getAllList();
}