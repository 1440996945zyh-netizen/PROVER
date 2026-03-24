package com.yy.ppm.equipment.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMaterialWarningRecordDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarningRecordSearchDTO;
import com.yy.ppm.equipment.service.EMaterialWarningRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 预警消息
 */
@Validated
@RestController
@RequestMapping("/api/v1/internal/EMaterialWarningRecord")
public class EMaterialWarningRecordController {

    private static final MicroLogger LOGGER = new MicroLogger(EMaterialWarningRecordController.class);

    @Autowired
    private EMaterialWarningRecordService eMaterialWarningRecordService;

    /**
     * 主列表
     */
    @PostMapping("/list")
    @PreAuthorize("hasAuthority('equipment:eMaterialWarningRecord:query')")
    public Map<String, Object> getList(EMaterialWarningRecordSearchDTO searchDTO) {
        final String methodName = "EMaterialWarningRecordController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EMaterialWarningRecordDTO> result = eMaterialWarningRecordService.getList(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 详情
     */
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('equipment:eMaterialWarningRecord:query')")
    public Map<String, Object> getById(@RequestParam("id") Long id) {
        final String methodName = "EMaterialWarningRecordController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        EMaterialWarningRecordDTO result = eMaterialWarningRecordService.getById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 批量处理
     */
    @PostMapping("/handleBatch")
    @PreAuthorize("hasAuthority('equipment:eMaterialWarningRecord:handle')")
    public Map<String, Object> handleBatch(@RequestBody EMaterialWarningRecordDTO dto) {
        final String methodName = "EMaterialWarningRecordController:handleBatch";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        eMaterialWarningRecordService.handleBatch(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("处理成功").toResult();
    }
}
