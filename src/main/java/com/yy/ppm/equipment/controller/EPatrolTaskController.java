package com.yy.ppm.equipment.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EPatrolTaskSearchDTO;
import com.yy.ppm.equipment.bean.po.EPatrolTaskPO;
import com.yy.ppm.equipment.bean.po.EPatrolTaskSubPO;
import com.yy.ppm.equipment.service.EPatrolTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

/**
 * 巡检任务 Controller
 *
 * @author system
 */
@RestController
@RequestMapping("/api/v1/internal/EPatrolTask")
public class EPatrolTaskController {

    private static final MicroLogger LOGGER = new MicroLogger(EPatrolTaskController.class);

    @Autowired
    private EPatrolTaskService service;

    /**
     * 查询巡检任务列表 (PC端分页)
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('equipment:patrolTask:query')")
    public Map<String, Object> getList(EPatrolTaskSearchDTO searchDTO, PageParameter parameter) {
        final String methodName = "EPatrolTaskController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EPatrolTaskPO> result = service.getTaskList(searchDTO, parameter);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据任务ID查询巡检任务详情 (子表列表 - 分页)
     */
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('equipment:patrolTask:query')")
    public Map<String, Object> getById(EPatrolTaskSearchDTO searchDTO, PageParameter parameter) {
        final String methodName = "EPatrolTaskController:getById";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EPatrolTaskSubPO> result = service.getSubTaskPage(searchDTO, parameter);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询巡检任务APP（分页）
     */
    @GetMapping("/getListAPP")
    public Map<String, Object> getListAPP(EPatrolTaskSearchDTO searchDTO, PageParameter parameter) {
        final String methodName = "EPatrolTaskController:getListAPP";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EPatrolTaskPO> result = service.getListAPP(searchDTO, parameter);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据任务ID查询设备
     */
    @GetMapping("/getEquipmentById")
    public Map<String, Object> getEquipmentById(EPatrolTaskSearchDTO searchDTO) {
        final String methodName = "EPatrolTaskController:getEquipmentById";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        List<Map<String, Object>> result = service.getEquipmentById(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据任务ID和设备ID查询巡检任务子表
     */
    @GetMapping("/getTaskItemById")
    public Map<String, Object> getTaskItemById(EPatrolTaskSearchDTO searchDTO) {
        final String methodName = "EPatrolTaskController:getTaskItemById";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        List<EPatrolTaskSubPO> result = service.getTaskItemById(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @com.yy.framework.annotation.Log(title = "APP巡检任务保存", value = com.yy.common.enums.OperateTypeEnum.UPDATE)
    public Map<String, Object> save(@org.springframework.web.bind.annotation.RequestBody List<EPatrolTaskSubPO> list) {
        final String methodName = "EPatrolTaskController:save";
        LOGGER.enter(methodName + "[start]", "list:" + list);

        service.save(list);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }
}
