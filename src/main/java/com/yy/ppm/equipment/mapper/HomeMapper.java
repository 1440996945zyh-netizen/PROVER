package com.yy.ppm.equipment.mapper;

import com.yy.ppm.equipment.bean.dto.HomeDTO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface HomeMapper {

    Map<String, Object> getMaintInfo(Long id);

    Map<String, Object> getPatrolTask(Long id);

    Map<String, Object> getCheckPlan(Long id);

    Map<String, Object> ePmMaintainTask(Long id);

    Map<String, Object> eMaintIfonType(Long id);

    Map<String, Object> eMaintIfonToday(@Param("id")Long id);

    Map<String, Object> getEqptInfo( Long deptId);

    Map<String, Object> getEqptStatus(Long deptId);

    List<HomeDTO> getMainInfo(HomeDTO homeDTO);
}
