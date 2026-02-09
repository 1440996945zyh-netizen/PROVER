package com.yy.ppm.equipment.controller;


import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.equipment.bean.dto.InspectionPlanTaskDTO;
import com.yy.ppm.equipment.bean.dto.MaintainTaskDTO;
import com.yy.ppm.equipment.bean.po.InspectionPlanTaskItemPO;
import com.yy.ppm.equipment.bean.po.InspectionPlanTaskPO;
import com.yy.ppm.equipment.bean.po.MaintainTaskItemPO;
import com.yy.ppm.equipment.bean.po.MaintainTaskPO;
import com.yy.ppm.equipment.service.ECheckTaskService;
import com.yy.ppm.equipment.service.MaintainTaskService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 润滑保养记录Controller
 * @author system
 */
@RestController
@RequestMapping("/api/v1/internal/maintainTask")
public class MaintainTaskController {
    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(MaintainTaskController.class);

    @Resource
    private MaintainTaskService maintainTaskService;

    /**
     * 查询点检记录（分页）
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(MaintainTaskDTO searchDTO, PageParameter parameter) {
        final String methodName = "MaintainTaskController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<MaintainTaskPO> result = maintainTaskService.getList(searchDTO,parameter);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询点检记录APP（分页）
     */
    @GetMapping("/getListAPP")
    public Map<String, Object> getListAPP(MaintainTaskDTO searchDTO, PageParameter parameter) {
        final String methodName = "MaintainTaskController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<MaintainTaskPO> result = maintainTaskService.getListAPP(searchDTO,parameter);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询设备维修派工信息
     */
    @GetMapping("/getById")
    public Map<String, Object> getById(MaintainTaskDTO searchDTO, PageParameter parameter) {
        final String methodName = "MaintainTaskController:getById";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<MaintainTaskItemPO> result = maintainTaskService.getById(searchDTO,parameter);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据任务ID查询设备机构
     */
    @GetMapping("/getInstitutionById")
    public Map<String, Object> getInstitutionById(MaintainTaskDTO searchDTO) {
        final String methodName = "MaintainTaskController:getById";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        List<Map<String,Object>> result = maintainTaskService.getInstitutionById(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    /**
     * 根据任务ID查询设备部件
     */
    @GetMapping("/getUnitById")
    public Map<String, Object> getUnitById(MaintainTaskDTO searchDTO) {
        final String methodName = "MaintainTaskController:getUnitById";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        List<Map<String,Object>> result = maintainTaskService.getUnitById(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    /**
     * 根据部件ID查询任务子表
     */
    @GetMapping("/getTaskItemById")
    public Map<String, Object> getTaskItemById(MaintainTaskDTO searchDTO) {
        final String methodName = "MaintainTaskController:getTaskItemById";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        List<MaintainTaskItemPO> result = maintainTaskService.getTaskItemById(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    /**
     * 保存
     */
    @PostMapping("/save")
    @Log(title = "APP点检任务保存", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> save(@RequestBody List<MaintainTaskItemPO> list) {
        final String methodName = "MaintainTaskController:save";
        LOGGER.enter(methodName + "[start]", "list:" + list);

        maintainTaskService.save(list);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }


}
