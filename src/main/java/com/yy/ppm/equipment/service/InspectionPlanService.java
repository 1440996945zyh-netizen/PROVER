package com.yy.ppm.equipment.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseDTO;
import com.yy.ppm.equipment.bean.dto.InspectionPlanDTO;
import com.yy.ppm.equipment.bean.dto.InspectionPlanTaskDTO;
import com.yy.ppm.equipment.bean.po.InspectionPlanPO;
import com.yy.ppm.equipment.bean.po.InspectionPlanTaskPO;
import com.yy.ppm.equipment.bean.po.InspectionStandardPO;
import com.yy.ppm.equipment.bean.po.MEquipmentInfoPO;

import java.util.List;

public interface InspectionPlanService {
    Pages<InspectionPlanPO> queryAll(InspectionPlanDTO inspectionPlanDTO, PageParameter parameter);

    InspectionPlanPO getById(Long id);

    void save(InspectionPlanPO dto);

    void deleteById(Long id);

    List<MEquipmentInfoPO> getEquipListById(Long id);

    Pages<InspectionPlanTaskPO> getTaskDetail(InspectionPlanTaskDTO inspectionPlanDTO, PageParameter parameter);
}
