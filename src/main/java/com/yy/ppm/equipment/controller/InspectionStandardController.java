package com.yy.ppm.equipment.controller;


import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.equipment.bean.dto.InspectionStandardDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentTypeDTO;
import com.yy.ppm.equipment.bean.po.InspectionStandardPO;
import com.yy.ppm.equipment.service.InspectionStandardService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/internal/inspectionStandard")
public class InspectionStandardController {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(InspectionStandardController.class);

    @Resource
    private InspectionStandardService inspectionStandardService;

    /**
     * 根据ID查询
     */
    @GetMapping("/queryByUnitId")
    public Map<String, Object> queryById(InspectionStandardDTO inspectionStandardDTO) {
        final String methodName = "MEquipmentTypeController:getTree";
        LOGGER.enter(methodName + "[start]", "inspectionStandardDTO:" + inspectionStandardDTO);

        List<InspectionStandardPO> result = inspectionStandardService.queryByUnitId(inspectionStandardDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增
     */
    @PostMapping("/save")
    @Log(value= OperateTypeEnum.INSERT, title="保存点检标准")
    public Map<String, Object> add(@RequestBody InspectionStandardDTO dto) {
        final String methodName = "InspectionStandardController:save";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        inspectionStandardService.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }

    /**
     * 查询标准
     */
    @GetMapping("/queryAll")
    public Map<String, Object> queryAll(InspectionStandardDTO inspectionStandardDTO, PageParameter parameter) {
        final String methodName = "MEquipmentTypeController:getTree";
        LOGGER.enter(methodName + "[start]", "inspectionStandardDTO:" + inspectionStandardDTO);

        Pages<InspectionStandardPO> result = inspectionStandardService.queryAll(inspectionStandardDTO,parameter);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
}
