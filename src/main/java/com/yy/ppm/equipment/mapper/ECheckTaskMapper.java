package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.InspectionPlanTaskDTO;
import com.yy.ppm.equipment.bean.po.InspectionPlanTaskItemPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ECheckTaskMapper {
    <T> Page<T> getList(InspectionPlanTaskDTO searchDTO);
    <T> Page<T> getListAPP(InspectionPlanTaskDTO searchDTO);

    <T> Page<T> getById(InspectionPlanTaskDTO searchDTO);

    List<Map<String, Object>> getInstitutionById(InspectionPlanTaskDTO searchDTO);

    List<Map<String, Object>> getUnitById(InspectionPlanTaskDTO searchDTO);

    List<InspectionPlanTaskItemPO> getTaskItemById(InspectionPlanTaskDTO searchDTO);

    @Edit
    void updateTaskItemList(@Param("list") List<InspectionPlanTaskItemPO> list);

    int getTaskItemCountById(Long equipTaskId);

    @Edit
    void updateTaskById(@Param("equipTaskId") Long equipTaskId, @Param("status") Integer status);
}
