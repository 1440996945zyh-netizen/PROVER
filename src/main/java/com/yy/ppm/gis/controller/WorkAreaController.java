package com.yy.ppm.gis.controller;

import com.yy.common.enums.Response;
import com.yy.common.util.ValidatorUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.gis.dto.workArea.Route;
import com.yy.ppm.gis.dto.workArea.ShiftPlan;
import com.yy.ppm.gis.po.TPlanRoutePO;
import com.yy.ppm.gis.po.TPlanWorkareaPO;
import com.yy.ppm.gis.service.WorkAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author linqi
 * @Description 作业区域
 * @Date 2023-06-16 09:34
 */
@RestController
@RequestMapping("/api/external/workArea")
@Validated
public class WorkAreaController {

    @Autowired
    private WorkAreaService workAreaService;

    /**
     * 工班计划列表
     *
     * @param workDate
     * @param workShift
     * @return
     */
    @GetMapping("/listShiftPlan")
    public Map<String, Object> listShiftPlan(@DateTimeFormat(pattern = "yyyy-MM-dd") Date workDate, String workShift) {
        List<ShiftPlan> shiftPlans = workAreaService.listShiftPlan(workDate, workShift);
        return Response.SUCCESS.newBuilder().toResult(shiftPlans);
    }

    /**
     * 查询路线
     */
    @GetMapping("/listRoute")
    public Map<String, Object> listRoute() {
        List<Route> routes = workAreaService.listRoute();
        return Response.SUCCESS.newBuilder().toResult(routes);
    }

    /**
     * 新增计划和路线关联
     *
     * @param planRoute
     * @return
     */
    @PostMapping("/insertPlanRoute")
    public Map<String, Object> insertPlanRoute(@RequestBody TPlanRoutePO planRoute) {
        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(planRoute);
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }

        workAreaService.insertPlanRoute(planRoute);
        return Response.SUCCESS.newBuilder().out("新增完成").toResult();
    }

    /**
     * 新增区域
     *
     * @param planArea
     * @return
     */
    @PostMapping("/insertPlanArea")
    public Map<String, Object> insertArea(@RequestBody TPlanWorkareaPO planArea) {
        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(planArea);
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }

        workAreaService.insertPlanArea(planArea);
        return Response.SUCCESS.newBuilder().out("新增完成").toResult();
    }

    /**
     * 删除计划和路线关联，根据计划和路线关联id
     *
     * @param planRouteId
     * @return
     */
    @DeleteMapping("/deletePlanRoute")
    public Map<String, Object> deleteRoute(@NotNull(message = "计划和路线关联id不能为空") Long planRouteId) {
        workAreaService.deletePlanRoute(planRouteId);
        return Response.SUCCESS.newBuilder().out("删除完成").toResult();
    }

    /**
     * 删除区域，根据区域id
     *
     * @param areaId
     * @return
     */
    @DeleteMapping("/deletePlanArea")
    public Map<String, Object> deleteArea(@NotNull(message = "计划和区域关联id不能为空") Long areaId) {
        workAreaService.deletePlanArea(areaId);
        return Response.SUCCESS.newBuilder().out("删除完成").toResult();
    }

    /**
     * 查询计划和路线关联，根据计划id
     *
     * @return
     */
    @GetMapping("/listPlanRoute")
    public Map<String, Object> listPlanRoute(@NotNull(message = "计划id不能为空") Long planId) {
        List<TPlanRoutePO> planRoutes = workAreaService.listPlanRoute(planId);
        return Response.SUCCESS.newBuilder().toResult(planRoutes);
    }

    /**
     * 查询区域，根据计划id
     *
     * @return
     */
    @GetMapping("/listPlanArea")
    public Map<String, Object> listPlanArea(@NotNull(message = "计划id不能为空") Long planId) {
        List<TPlanWorkareaPO> planAreas = workAreaService.listPlanArea(planId);
        return Response.SUCCESS.newBuilder().toResult(planAreas);
    }
}
