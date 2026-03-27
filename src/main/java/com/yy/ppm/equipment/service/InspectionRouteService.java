package com.yy.ppm.equipment.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.InspectionRouteDTO;

import java.util.List;

public interface InspectionRouteService {

    void insert(InspectionRouteDTO dto);

    void update(InspectionRouteDTO dto);

    void deleteById(Long id);

    InspectionRouteDTO getDetail(Long id);

    Pages<InspectionRouteDTO> getList(InspectionRouteDTO dto, PageParameter parameter);

    List<InspectionRouteDTO> getAllList();
}