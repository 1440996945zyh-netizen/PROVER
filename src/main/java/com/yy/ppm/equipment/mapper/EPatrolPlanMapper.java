package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.*;
import com.yy.ppm.equipment.bean.po.EPatrolPlanPO;
import com.yy.ppm.equipment.bean.po.EPatrolTaskPO;
import com.yy.ppm.equipment.bean.po.EPatrolTaskSubPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EPatrolPlanMapper {

    <T> Page<T> getList(EPatrolPlanDTO searchDTO);

    EPatrolPlanDTO getById(EPatrolPlanDTO searchDTO);

    @Edit
    void insert(EPatrolPlanDTO po);

    @Edit
    void update(EPatrolPlanDTO po);

    void deleteById(@Param("id") Long id);

    List<EPatrolPlanDTO> getpatrolPlanList();

    List<InspectionRouteSubDTO> getrouteSubList(Long routeId);

    @Edit
    void updatePlanList(@Param("list") List<EPatrolPlanPO> planList);

    @Edit
    void insertTaskList(@Param("list") List<EPatrolTaskPO> taskList);

    @Edit
    void insertTaskItemList(@Param("list") List<EPatrolTaskSubPO> taskItemList);

    List<InspectionRouteDTO> getRouteList(InspectionRouteDTO searchDTO);
}
