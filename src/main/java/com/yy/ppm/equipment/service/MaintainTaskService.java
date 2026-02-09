package com.yy.ppm.equipment.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.MaintainTaskDTO;
import com.yy.ppm.equipment.bean.po.MaintainTaskItemPO;
import com.yy.ppm.equipment.bean.po.MaintainTaskPO;

import java.util.List;
import java.util.Map;

public interface MaintainTaskService {
    Pages<MaintainTaskPO> getList(MaintainTaskDTO searchDTO, PageParameter parameter);
    Pages<MaintainTaskPO> getListAPP(MaintainTaskDTO searchDTO, PageParameter parameter);

    Pages<MaintainTaskItemPO> getById(MaintainTaskDTO searchDTO, PageParameter parameter);

    List<Map<String, Object>> getInstitutionById(MaintainTaskDTO searchDTO);

    List<Map<String, Object>> getUnitById(MaintainTaskDTO searchDTO);

    List<MaintainTaskItemPO> getTaskItemById(MaintainTaskDTO searchDTO);

    void save(List<MaintainTaskItemPO> list);
}
