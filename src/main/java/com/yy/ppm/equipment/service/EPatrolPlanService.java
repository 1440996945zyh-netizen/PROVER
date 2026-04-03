package com.yy.ppm.equipment.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMEquipRepairUserDTO;
import com.yy.ppm.equipment.bean.dto.EPatrolPlanDTO;
import com.yy.ppm.equipment.bean.dto.InspectionRouteDTO;

import java.util.Date;
import java.util.List;

public interface EPatrolPlanService {
    Pages<EPatrolPlanDTO> getList(EPatrolPlanDTO searchDTO, PageParameter parameter);

    EPatrolPlanDTO getById(EPatrolPlanDTO searchDTO);

    void save(EPatrolPlanDTO po);

    void delete(Long id);

    List<InspectionRouteDTO> getRouteList(InspectionRouteDTO searchDTO);


    Boolean isCreateTask(String patrolType, Date initialDate, String setDate);
}
