package com.yy.ppm.equipment.controller;


import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.equipment.bean.dto.EMEquipRepairUserDTO;
import com.yy.ppm.equipment.bean.dto.EPatrolPlanDTO;
import com.yy.ppm.equipment.bean.dto.InspectionRouteDTO;
import com.yy.ppm.equipment.service.EMEquipRepairUserService;
import com.yy.ppm.equipment.service.EPatrolPlanService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 巡检计划Controller
 * @author system
 */
@RestController
@RequestMapping("/api/internal/ePatrolPlan")
public class EPatrolPlanController {
    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(EPatrolPlanController.class);

    @Resource
    private EPatrolPlanService ePatrolPlanService;

    /**
     * 查询巡检计划（分页）
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('equipment:ePatrolPlan:query')")
    public Map<String, Object> getList(EPatrolPlanDTO searchDTO, PageParameter parameter) {
        final String methodName = "MEquipmentOperationController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EPatrolPlanDTO> result = ePatrolPlanService.getList(searchDTO,parameter);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询巡检计划
     */
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('equipment:ePatrolPlan:getById')")
    public Map<String, Object> getById(EPatrolPlanDTO searchDTO) {
        final String methodName = "MEquipmentOperationController:getById";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        EPatrolPlanDTO result = ePatrolPlanService.getById(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增巡检计划
     */
    @PostMapping("/add")
    @Log(title = "新增巡检计划", value = OperateTypeEnum.INSERT)
    @PreAuthorize("hasAuthority('equipment:ePatrolPlan:add')")
    public Map<String, Object> add(@RequestBody EPatrolPlanDTO po) {
        final String methodName = "MEquipmentOperationController:add";
        LOGGER.enter(methodName + "[start]", "po:" + po);

        ePatrolPlanService.save(po);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 新增
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('equipment:ePatrolPlan:update')")
    @Log(title = "修改巡检计划", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> update(@RequestBody EPatrolPlanDTO po) {
        final String methodName = "MEquipmentOperationController:add";
        LOGGER.enter(methodName + "[start]", "po:" + po);

        ePatrolPlanService.save(po);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 删除巡检计划
     */
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('equipment:ePatrolPlan:delete')")
    @Log(title = "删除巡检计划", value = OperateTypeEnum.DELETE)
    public Map<String, Object> delete(@RequestParam("id") Long id) {
        final String methodName = "MEquipmentOperationController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        ePatrolPlanService.delete(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }



    /**
     * 查询巡检线路
     */
    @GetMapping("/getRouteList")
    public Map<String, Object> getRouteList(InspectionRouteDTO searchDTO) {
        final String methodName = "MEquipmentOperationController:getRouteList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        List<InspectionRouteDTO> result = ePatrolPlanService.getRouteList(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
}
