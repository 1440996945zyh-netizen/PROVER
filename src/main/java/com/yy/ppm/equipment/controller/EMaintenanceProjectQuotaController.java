package com.yy.ppm.equipment.controller;


import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.equipment.bean.dto.EMaintenanceProjectQuotaDTO;
import com.yy.ppm.equipment.service.EMaintenanceProjectQuotaService;

import jakarta.annotation.Resource;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

/**
 * 维修定额项目 Controller
 *
 * 前端接口入口
 */
@RestController
@RequestMapping("/api/internal/EMMaintenanceProjectQuota")
public class EMaintenanceProjectQuotaController {

    /** 日志组件 */
    private static final MicroLogger LOGGER = new MicroLogger(EMaintenanceProjectQuotaController.class);

    @Resource
    private EMaintenanceProjectQuotaService service;

    /**
     * 查询维修定额项目（分页）
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('equipment:emmaintenanceprojectquota:query')")
    public Map<String, Object> getList(EMaintenanceProjectQuotaDTO searchDTO, PageParameter parameter) {
        final String methodName = "EMMaintenanceProjectQuotaController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EMaintenanceProjectQuotaDTO> result = service.getList(searchDTO, parameter);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询维修定额项目
     */
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('equipment:emmaintenanceprojectquota:getById')")
    public Map<String, Object> getById(@RequestParam("id") Long id) {
        final String methodName = "EMMaintenanceProjectQuotaController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        EMaintenanceProjectQuotaDTO result = service.getById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增维修定额项目
     */
    @PostMapping("/add")
    @Log(title = "新增维修定额项目", value = OperateTypeEnum.INSERT)
    @PreAuthorize("hasAuthority('equipment:emmaintenanceprojectquota:add')")
    public Map<String, Object> add(@RequestBody EMaintenanceProjectQuotaDTO dto) {
        final String methodName = "EMMaintenanceProjectQuotaController:add";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 修改维修定额项目
     */
    @PutMapping("/update")
    @Log(title = "修改维修定额项目", value = OperateTypeEnum.UPDATE)
    @PreAuthorize("hasAuthority('equipment:emmaintenanceprojectquota:update')")
    public Map<String, Object> update(@RequestBody EMaintenanceProjectQuotaDTO dto) {
        final String methodName = "EMMaintenanceProjectQuotaController:update";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 删除维修定额项目
     */
    @DeleteMapping("/delete")
    @Log(title = "删除维修定额项目", value = OperateTypeEnum.DELETE)
    @PreAuthorize("hasAuthority('equipment:emmaintenanceprojectquota:delete')")
    public Map<String, Object> delete(@RequestParam("id") Long id) {
        final String methodName = "EMMaintenanceProjectQuotaController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        service.delete(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }
}

