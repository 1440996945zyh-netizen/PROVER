package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EPatrolPlanDTO;
import com.yy.ppm.equipment.bean.dto.InspectionRouteDTO;
import com.yy.ppm.equipment.bean.dto.InspectionRouteSubDTO;
import com.yy.ppm.equipment.bean.po.EPatrolPlanPO;
import com.yy.ppm.equipment.bean.po.EPatrolTaskPO;
import com.yy.ppm.equipment.bean.po.EPatrolTaskSubPO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface HomeMapper {

    Map<String, Object> getMaintIfon();

    Map<String, Object> getPatrolTask();

    Map<String, Object> getCheckPlan();

    Map<String, Object> ePmMaintainTask();

    Map<String, Object> eMaintIfonType();

    Map<String, Object> eMaintIfonToday(Date date);

    Map<String, Object> getEqptInfo();

    Map<String, Object> getEqptStatus();
}
