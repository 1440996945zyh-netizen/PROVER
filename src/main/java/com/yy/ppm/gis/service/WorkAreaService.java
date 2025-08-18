package com.yy.ppm.gis.service;

import com.yy.ppm.gis.dto.workArea.Route;
import com.yy.ppm.gis.dto.workArea.ShiftPlan;
import com.yy.ppm.gis.po.TPlanRoutePO;
import com.yy.ppm.gis.po.TPlanWorkareaPO;

import java.util.Date;
import java.util.List;

/**
 * @Author linqi
 * @Description
 * @Date 2023-06-16 10:00
 */
public interface WorkAreaService {

    List<ShiftPlan> listShiftPlan(Date workDate, String workShift);

    List<Route> listRoute();

    void insertPlanRoute(TPlanRoutePO planRoute);

    void insertPlanArea(TPlanWorkareaPO planArea);

    void deletePlanRoute(Long planRouteId);

    void deletePlanArea(Long areaId);

    List<TPlanRoutePO> listPlanRoute(Long planId);

    List<TPlanWorkareaPO> listPlanArea(Long planId);
}
