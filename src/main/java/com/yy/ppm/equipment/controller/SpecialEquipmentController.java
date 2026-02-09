package com.yy.ppm.equipment.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.SpecialEquipmentDTO;
import com.yy.ppm.equipment.bean.dto.SpecialEquipmentSearchDTO;
import com.yy.ppm.equipment.service.SpecialEquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 特种设备查询Controller
 * @author system
 */
@Validated
@RestController
@RequestMapping("/api/v1/internal/specialEquipment")
public class SpecialEquipmentController {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(SpecialEquipmentController.class);

    @Autowired
    private SpecialEquipmentService service;

    /**
     * 查询特种设备列表（分页）
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('equipment:specialEquipment:query')")
    public Map<String, Object> getList(SpecialEquipmentSearchDTO searchDTO) {
        final String methodName = "SpecialEquipmentController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<SpecialEquipmentDTO> result = service.getList(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
}

