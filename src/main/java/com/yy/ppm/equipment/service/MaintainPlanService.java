package com.yy.ppm.equipment.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.InspectionPlanDTO;
import com.yy.ppm.equipment.bean.dto.InspectionPlanTaskDTO;
import com.yy.ppm.equipment.bean.dto.MaintainPlanDTO;
import com.yy.ppm.equipment.bean.po.InspectionPlanPO;
import com.yy.ppm.equipment.bean.po.InspectionPlanTaskPO;
import com.yy.ppm.equipment.bean.po.MEquipmentInfoPO;
import com.yy.ppm.equipment.bean.po.MaintainPlanPO;

import java.util.List;

public interface MaintainPlanService {
    Pages<MaintainPlanPO> queryAll(MaintainPlanDTO maintainPlanDTO, PageParameter parameter);

    MaintainPlanPO getById(Long id);

    void save(MaintainPlanPO dto);

    void deleteById(Long id);

    List<MEquipmentInfoPO> getEquipListById(Long id);

    void report(MaintainPlanPO dto);
}
