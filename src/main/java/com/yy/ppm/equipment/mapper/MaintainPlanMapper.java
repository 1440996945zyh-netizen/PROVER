package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EquipSmallCategorySelectDTO;
import com.yy.ppm.equipment.bean.dto.InspectionPlanTaskDTO;
import com.yy.ppm.equipment.bean.dto.MaintainPlanDTO;
import com.yy.ppm.equipment.bean.po.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MaintainPlanMapper {
    <T> Page<T> queryAll(MaintainPlanDTO maintainPlanDTO);

    MaintainPlanPO queryById(@Param("id") Long id);

    @Edit
    void insert(MaintainPlanPO dto);

    @Edit
    void update(MaintainPlanPO dto);

    void deleteById(@Param("id") Long id);

    List<MEquipmentInfoPO> getEquipListById(@Param("id") Long id);

    @Edit
    void insertPlanTask(MaintainTaskPO maintainTaskPO);

    <T> Page<T> getTaskDetail(InspectionPlanTaskDTO inspectionPlanDTO);

    List<MaintainPlanPO> getInspectionPlanList();

    List<InspectionPlanTaskPO> getInspectionPlanTaskList();

    @Edit
    void insertPlanItem(@Param("list") List<MaintainPlanItemPO> itemList);

    void deletePlanItemByPlanId(MaintainPlanPO dto);

    @Edit
    void insertPlanTaskItem(@Param("list") List<MaintainTaskItemPO> taskItemList);

    List<MaintainPlanItemPO> getPlanItem(@Param("id") Long id);

    List<MaintainPlanItemPO> getInspectionPlanItemList();

    @Edit
    void insertPlanTaskList(@Param("list") List<MaintainTaskPO> taskList);

    @Edit
    void updatePlanList(@Param("list") List<MaintainPlanPO> planList);

    void insertPlanTaskItemList(@Param("list") List<MaintainTaskItemPO> taskItemList);

    List<MEquipmentOperationPO> getEquipmentOperationList();

    @Edit
    void updateById(MaintainPlanPO po);
}
