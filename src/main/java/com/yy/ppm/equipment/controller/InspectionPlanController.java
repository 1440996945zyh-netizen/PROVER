package com.yy.ppm.equipment.controller;


import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.equipment.bean.dto.InspectionPlanDTO;
import com.yy.ppm.equipment.bean.dto.InspectionPlanTaskDTO;
import com.yy.ppm.equipment.bean.po.InspectionPlanPO;
import com.yy.ppm.equipment.bean.po.InspectionPlanTaskPO;
import com.yy.ppm.equipment.bean.po.MEquipmentInfoPO;
import com.yy.ppm.equipment.service.InspectionPlanService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/internal/inspectionPlan")
public class InspectionPlanController {
    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(InspectionPlanController.class);

    @Resource
    private InspectionPlanService inspectionPlanService;

    /**
     * 查询点检计划（分页）
     */
    @GetMapping("/queryAll")
    public Map<String, Object> queryAll(InspectionPlanDTO inspectionPlanDTO, PageParameter parameter) {
        final String methodName = "InspectionPlanController:queryAll";
        LOGGER.enter(methodName + "[start]", "inspectionPlanDTO:" + inspectionPlanDTO);

        Pages<InspectionPlanPO> result = inspectionPlanService.queryAll(inspectionPlanDTO,parameter);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询物资仓库
     */
    @GetMapping("/getById")
    public Map<String, Object> getById(@RequestParam("id") Long id) {
        final String methodName = "InspectionPlanController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        InspectionPlanPO result = inspectionPlanService.getById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增点检计划
     */
    @PostMapping("/add")
    @Log(title = "新增点检计划", value = OperateTypeEnum.INSERT)
    public Map<String, Object> add(@RequestBody InspectionPlanPO dto) {
        final String methodName = "InspectionPlanController:add";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        inspectionPlanService.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 修改点检计划
     */
    @PutMapping("/update")
    @Log(title = "修改点检计划", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> update(@RequestBody InspectionPlanPO dto) {
        final String methodName = "InspectionPlanController:update";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        inspectionPlanService.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 删除点检计划
     */
    @DeleteMapping("/delete")
    @Log(title = "删除点检计划", value = OperateTypeEnum.DELETE)
    public Map<String, Object> delete(Long id) {
        final String methodName = "InspectionPlanController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        inspectionPlanService.deleteById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 根据设备小类查询设备列表
     */
    @GetMapping("/getEquipListById")
    public Map<String, Object> getEquipListById(@RequestParam("id") Long id) {
        final String methodName = "InspectionPlanController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        List<MEquipmentInfoPO> result = inspectionPlanService.getEquipListById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询点检任务（分页）
     */
    @GetMapping("/getTaskDetail")
    public Map<String, Object> getTaskDetail(InspectionPlanTaskDTO inspectionPlanDTO, PageParameter parameter) {
        final String methodName = "InspectionPlanController:getTaskDetail";
        LOGGER.enter(methodName + "[start]", "inspectionPlanDTO:" + inspectionPlanDTO);

        Pages<InspectionPlanTaskPO> result = inspectionPlanService.getTaskDetail(inspectionPlanDTO,parameter);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
}
