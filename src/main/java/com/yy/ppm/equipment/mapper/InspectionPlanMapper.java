package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.InspectionPlanDTO;
import com.yy.ppm.equipment.bean.dto.InspectionPlanTaskDTO;
import com.yy.ppm.equipment.bean.po.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InspectionPlanMapper {
    <T> Page<T> queryAll(InspectionPlanDTO inspectionPlanDTO);

    InspectionPlanPO queryById(@Param("id") Long id);

    @Edit
    void insert(InspectionPlanPO dto);

    @Edit
    void update(InspectionPlanPO dto);

    void deleteById(@Param("id") Long id);

    List<MEquipmentInfoPO> getEquipListById(@Param("id") Long id);

    @Edit
    void insertPlanTask(InspectionPlanTaskPO inspectionPlanTaskPO);

    <T> Page<T> getTaskDetail(InspectionPlanTaskDTO inspectionPlanDTO);

    List<InspectionPlanPO> getInspectionPlanList();

    List<InspectionPlanTaskPO> getInspectionPlanTaskList();

    @Edit
    void insertPlanItem(@Param("list") List<InspectionPlanItemPO> itemList);

    void deletePlanItemByPlanId(InspectionPlanPO dto);

    @Edit
    void insertPlanTaskItem(@Param("list") List<InspectionPlanTaskItemPO> taskItemList);

    List<InspectionPlanItemPO> getPlanItem(@Param("id") Long id);

    List<InspectionPlanItemPO> getInspectionPlanItemList();

    @Edit
    void insertPlanTaskList(@Param("list") List<InspectionPlanTaskPO> taskList);

    @Edit
    void updatePlanList(@Param("list") List<InspectionPlanPO> planList);

    void insertPlanTaskItemList(@Param("list") List<InspectionPlanTaskItemPO> taskItemList);

    List<MEquipmentOperationPO> getEquipmentOperationList();
}
