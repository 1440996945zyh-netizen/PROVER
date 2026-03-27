package com.yy.ppm.equipment.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.equipment.bean.dto.InspectionRouteDTO;
import com.yy.ppm.equipment.service.InspectionRouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/internal/inspectionRoute")
@Validated
@Tag(name = "设备.巡检路线")
public class InspectionRouteController {

    private static final MicroLogger LOGGER = new MicroLogger(InspectionRouteController.class);

    @Resource
    private InspectionRouteService inspectionRouteService;

    /**
     * 新增巡检路线
     */
    @PostMapping("/insert")
    @PreAuthorize("hasAuthority('equipment:inspectionRoute:insert')")
    @Log(OperateTypeEnum.INSERT)
    @Operation(summary = "新增巡检路线")
    public Map<String, Object> insert(@RequestBody @Validated InspectionRouteDTO dto) {
        final String methodName = "InspectionRouteController:insert";
        LOGGER.enter(methodName, "新增巡检路线[start]");

        inspectionRouteService.insert(dto);

        LOGGER.exit(methodName, "新增巡检路线[end]");
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }

    /**
     * 修改巡检路线
     */
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('equipment:inspectionRoute:update')")
    @Log(OperateTypeEnum.UPDATE)
    @Operation(summary = "修改巡检路线")
    public Map<String, Object> update(@RequestBody @Validated InspectionRouteDTO dto) {
        final String methodName = "InspectionRouteController:update";
        LOGGER.enter(methodName, "修改巡检路线[start]");

        inspectionRouteService.update(dto);

        LOGGER.exit(methodName, "修改巡检路线[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 根据ID删除巡检路线
     */
    @DeleteMapping("/deleteById/{id}")
    @PreAuthorize("hasAuthority('equipment:inspectionRoute:delete')")
    @Log(OperateTypeEnum.DELETE)
    @Operation(summary = "根据ID删除巡检路线")
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "InspectionRouteController:deleteById";
        LOGGER.enter(methodName, "根据ID删除巡检路线[start]");

        inspectionRouteService.deleteById(id);

        LOGGER.exit(methodName, "根据ID删除巡检路线[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 根据ID查询详情
     */
    @GetMapping("/getDetail/{id}")
    @PreAuthorize("hasAuthority('equipment:inspectionRoute:query')")
    @Log(OperateTypeEnum.QUERY)
    @Operation(summary = "根据ID查询巡检路线详情")
    public Map<String, Object> getDetail(@PathVariable("id") Long id) {
        final String methodName = "InspectionRouteController:getDetail";
        LOGGER.enter(methodName, "根据ID查询巡检路线详情[start]");

        InspectionRouteDTO dto = inspectionRouteService.getDetail(id);

        LOGGER.exit(methodName, "根据ID查询巡检路线详情[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(dto);
    }

    /**
     * 分页查询列表
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('equipment:inspectionRoute:query')")
    @Log(OperateTypeEnum.QUERY)
    @Operation(summary = "分页查询巡检路线列表")
    public Map<String, Object> getList(InspectionRouteDTO dto, PageParameter parameter) {
        final String methodName = "InspectionRouteController:getList";
        LOGGER.enter(methodName, "分页查询巡检路线列表[start]");

        Pages<InspectionRouteDTO> pages = inspectionRouteService.getList(dto, parameter);

        LOGGER.exit(methodName, "分页查询巡检路线列表[end]");
        return Response.SUCCESS.newBuilder().toResult(pages);
    }

    /**
     * 查询所有数据（不分页）
     */
    @GetMapping("/getAllList")
    @PreAuthorize("hasAuthority('equipment:inspectionRoute:query')")
    @Log(OperateTypeEnum.QUERY)
    @Operation(summary = "查询所有巡检路线数据")
    public Map<String, Object> getAllList() {
        final String methodName = "InspectionRouteController:getAllList";
        LOGGER.enter(methodName, "查询所有巡检路线数据[start]");

        List<InspectionRouteDTO> list = inspectionRouteService.getAllList();

        LOGGER.exit(methodName, "查询所有巡检路线数据[end]");
        return Response.SUCCESS.newBuilder().toResult(list);
    }
}