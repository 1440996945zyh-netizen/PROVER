package com.yy.ppm.equipment.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.InspectionStandardDTO;
import com.yy.ppm.equipment.bean.dto.MaintainStandardDTO;
import com.yy.ppm.equipment.bean.po.InspectionStandardPO;
import com.yy.ppm.equipment.bean.po.MaintainStandardPO;

import java.util.List;

public interface MaintainStandardService {

    List<MaintainStandardPO> queryByUnitId(MaintainStandardDTO maintainStandardDTO);

    void save(MaintainStandardDTO dto);

    Pages<MaintainStandardPO> queryAll(MaintainStandardDTO maintainStandardDTO, PageParameter parameter);
}
