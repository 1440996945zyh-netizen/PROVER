package com.yy.ppm.gis.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.util.SecurityUtils;
import com.yy.ppm.gis.dto.workArea.Route;
import com.yy.ppm.gis.dto.workArea.ShiftPlan;
import com.yy.ppm.gis.mapper.WorkAreaMapper;
import com.yy.ppm.gis.po.TPlanRoutePO;
import com.yy.ppm.gis.po.TPlanWorkareaPO;
import com.yy.ppm.gis.service.WorkAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.yy.common.util.str.StringUtil.getString;

/**
 * @Author linqi
 * @Description
 * @Date 2023-06-16 10:00
 */
@Service
public class WorkAreaServiceImpl implements WorkAreaService {

    @Autowired
    private WorkAreaMapper workAreaMapper;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private SecurityUtils securityUtils;

    @Override
    public List<ShiftPlan> listShiftPlan(Date workDate, String workShift) {
        List<ShiftPlan> shiftPlans = workAreaMapper.listShiftPlan(workDate, workShift);
        if (!shiftPlans.isEmpty()) {
            List<String> vesselVisitIds = shiftPlans.stream().map(ShiftPlan::getVesselVisitId).collect(Collectors.toList());
            List<Map<String, Object>> vesselVisits = workAreaMapper.listVesselVisit(vesselVisitIds);
            shiftPlans.forEach(v1 -> {
                Map<String, Object> vesselVisit = vesselVisits.stream().filter(v2 -> v1.getVesselVisitId().equals(v2.get("id"))).findFirst().orElse(Collections.emptyMap());
                v1.setVesselName(getString(vesselVisit.get("vesselName")));
                v1.setVoyage(getString(vesselVisit.get("voyage")));
                v1.setBerthCodeCur(getString(vesselVisit.get("berthCodeCur")));
                v1.setBerthName(getString(vesselVisit.get("berthName")));
                v1.setCargoCode(getString(vesselVisit.get("cargoCode")));
                v1.setCargoName(getString(vesselVisit.get("cargoName")));
            });
        }
        return shiftPlans;
    }

    @Override
    public List<Route> listRoute() {
        return workAreaMapper.listRoute();
    }

    @Override
    public void insertPlanRoute(TPlanRoutePO planRoute) {
        planRoute.setId(snowflake.nextId());
        planRoute.setCreateBy(securityUtils.getLoginUserId());
        planRoute.setCreateByName(securityUtils.getLoginUserName());
        planRoute.setCreateTime(new Date());

        workAreaMapper.insertPlanRoute(planRoute);
    }

    @Override
    public void insertPlanArea(TPlanWorkareaPO planArea) {
        planArea.setId(snowflake.nextId());
        planArea.setCreateBy(securityUtils.getLoginUserId());
        planArea.setCreateByName(securityUtils.getLoginUserName());
        planArea.setCreateTime(new Date());

        workAreaMapper.insertPlanArea(planArea);
    }

    @Override
    public void deletePlanRoute(Long planRouteId) {
        workAreaMapper.deletePlanRoute(planRouteId);
    }

    @Override
    public void deletePlanArea(Long areaId) {
        workAreaMapper.deletePlanArea(areaId);
    }

    @Override
    public List<TPlanRoutePO> listPlanRoute(Long planId) {
        return workAreaMapper.listPlanRoute(planId);
    }

    @Override
    public List<TPlanWorkareaPO> listPlanArea(Long planId) {
        return workAreaMapper.listPlanArea(planId);
    }
}
