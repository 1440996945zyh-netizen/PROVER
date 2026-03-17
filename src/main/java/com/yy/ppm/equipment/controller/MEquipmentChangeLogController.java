package com.yy.ppm.equipment.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.MEquipmentChangeLogDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentChangeLogSearchDTO;
import com.yy.ppm.equipment.service.MEquipmentChangeLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 设备变更记录Controller
 * @author system
 */
@Validated
@RestController
@RequestMapping("/api/v1/internal/equipmentChangeLog")
public class MEquipmentChangeLogController {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(MEquipmentChangeLogController.class);

    @Autowired
    private MEquipmentChangeLogService service;

    /**
     * 查询变更记录列表（分页）
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('equipment:equipmentInfo:query')")
    public Map<String, Object> getList(MEquipmentChangeLogSearchDTO searchDTO) {
        final String methodName = "MEquipmentChangeLogController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<MEquipmentChangeLogDTO> result = service.getList(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询变更记录详情
     */
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('equipment:equipmentInfo:query')")
    public Map<String, Object> getById(@RequestParam("id") Long id) {
        final String methodName = "MEquipmentChangeLogController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        MEquipmentChangeLogDTO result = service.getById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
}

