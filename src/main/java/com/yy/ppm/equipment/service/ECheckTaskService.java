package com.yy.ppm.equipment.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.InspectionPlanTaskDTO;
import com.yy.ppm.equipment.bean.po.InspectionPlanTaskItemPO;
import com.yy.ppm.equipment.bean.po.InspectionPlanTaskPO;

import java.util.List;
import java.util.Map;

public interface ECheckTaskService {
    Pages<InspectionPlanTaskPO> getList(InspectionPlanTaskDTO searchDTO, PageParameter parameter);
    Pages<InspectionPlanTaskPO> getListAPP(InspectionPlanTaskDTO searchDTO, PageParameter parameter);

    Pages<InspectionPlanTaskItemPO> getById(InspectionPlanTaskDTO searchDTO, PageParameter parameter);

    List<Map<String, Object>> getInstitutionById(InspectionPlanTaskDTO searchDTO);

    List<Map<String, Object>> getUnitById(InspectionPlanTaskDTO searchDTO);

    List<InspectionPlanTaskItemPO> getTaskItemById(InspectionPlanTaskDTO searchDTO);

    void save(List<InspectionPlanTaskItemPO> list);
}
