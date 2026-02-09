package com.yy.ppm.equipment.controller;


import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.equipment.bean.dto.InspectionPlanTaskDTO;
import com.yy.ppm.equipment.bean.po.InspectionPlanTaskItemPO;
import com.yy.ppm.equipment.bean.po.InspectionPlanTaskPO;
import com.yy.ppm.equipment.service.ECheckTaskService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 点检记录Controller
 * @author system
 */
@RestController
@RequestMapping("/api/v1/internal/ECheckTask")
public class ECheckTaskController {
    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(ECheckTaskController.class);

    @Resource
    private ECheckTaskService eCheckTaskService;

    /**
     * 查询点检记录（分页）
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(InspectionPlanTaskDTO searchDTO, PageParameter parameter) {
        final String methodName = "ECheckTaskController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<InspectionPlanTaskPO> result = eCheckTaskService.getList(searchDTO,parameter);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询点检记录APP（分页）
     */
    @GetMapping("/getListAPP")
    public Map<String, Object> getListAPP(InspectionPlanTaskDTO searchDTO, PageParameter parameter) {
        final String methodName = "ECheckTaskController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<InspectionPlanTaskPO> result = eCheckTaskService.getListAPP(searchDTO,parameter);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询设备维修派工信息
     */
    @GetMapping("/getById")
    public Map<String, Object> getById(InspectionPlanTaskDTO searchDTO, PageParameter parameter) {
        final String methodName = "ECheckTaskController:getById";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<InspectionPlanTaskItemPO> result = eCheckTaskService.getById(searchDTO,parameter);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据任务ID查询设备机构
     */
    @GetMapping("/getInstitutionById")
    public Map<String, Object> getInstitutionById(InspectionPlanTaskDTO searchDTO) {
        final String methodName = "ECheckTaskController:getById";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        List<Map<String,Object>> result = eCheckTaskService.getInstitutionById(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    /**
     * 根据任务ID查询设备部件
     */
    @GetMapping("/getUnitById")
    public Map<String, Object> getUnitById(InspectionPlanTaskDTO searchDTO) {
        final String methodName = "ECheckTaskController:getUnitById";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        List<Map<String,Object>> result = eCheckTaskService.getUnitById(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    /**
     * 根据部件ID查询任务子表
     */
    @GetMapping("/getTaskItemById")
    public Map<String, Object> getTaskItemById(InspectionPlanTaskDTO searchDTO) {
        final String methodName = "ECheckTaskController:getTaskItemById";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        List<InspectionPlanTaskItemPO> result = eCheckTaskService.getTaskItemById(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    /**
     * 保存
     */
    @PostMapping("/save")
    @Log(title = "APP点检任务保存", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> save(@RequestBody List<InspectionPlanTaskItemPO> list) {
        final String methodName = "ECheckTaskController:save";
        LOGGER.enter(methodName + "[start]", "list:" + list);

        eCheckTaskService.save(list);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }


}
