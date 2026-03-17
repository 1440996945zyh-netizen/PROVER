package com.yy.ppm.equipment.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.InspectionStandardDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentTypeDTO;
import com.yy.ppm.equipment.bean.po.InspectionStandardPO;

import java.util.List;

public interface InspectionStandardService {

    List<InspectionStandardPO> queryByUnitId(InspectionStandardDTO inspectionStandardDTO);

    void save(InspectionStandardDTO dto);

    Pages<InspectionStandardPO> queryAll(InspectionStandardDTO inspectionStandardDTO, PageParameter parameter);
}
