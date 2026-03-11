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


import java.util.HashMap;
import java.util.Map;
/**
 * 维修定额项目Controller
 * 功能：提供维修定额项目的增删改查接口
 * 接口前缀：/api/internal/EMMaintenanceProjectQuota
 */
@RestController
@RequestMapping("/api/internal/EMMaintenanceProjectQuota")
public class EMaintenanceProjectQuotaController {
    private static final MicroLogger LOGGER = new MicroLogger(EMaintenanceProjectQuotaController.class);
    @Resource
    private EMaintenanceProjectQuotaService service;
    /**
     * 查询维修定额项目列表（分页）
     * 前端请求示例：getList?startPage=1&pageSize=20&projectName=xxx&quotaCode=xxx
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('equipment:emmaintenanceprojectquota:query')")
    public Map<String, Object> getList(EMaintenanceProjectQuotaDTO searchDTO, PageParameter parameter) {
        final String methodName = "EMaintenanceProjectQuotaController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO + ", parameter:" + parameter);
        Pages<EMaintenanceProjectQuotaDTO> result = service.list(searchDTO, parameter);
        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    /**
     * 根据ID查询维修定额项目详情
     */
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('equipment:emmaintenanceprojectquota:getById')")
    public Map<String, Object> getById(@RequestParam("id") Long id) {
        final String methodName = "EMaintenanceProjectQuotaController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);
        EMaintenanceProjectQuotaDTO dto = service.getById(id);
        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(toViewMap(dto));
    }
    /**
     * 新增维修定额项目
     * 说明：定额编号 quotaCode 由后端自动生成（DE-YYYY-MM-DD-0001）
     */
    @PostMapping("/add")
    @Log(title = "新增维修定额项目", value = OperateTypeEnum.INSERT)
    @PreAuthorize("hasAuthority('equipment:emmaintenanceprojectquota:add')")
    public Map<String, Object> add(@RequestBody EMaintenanceProjectQuotaDTO dto) {
        final String methodName = "EMaintenanceProjectQuotaController:add";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);
        service.add(dto);
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
        final String methodName = "EMaintenanceProjectQuotaController:update";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);
        service.update(dto);
        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }
    /**
     * 删除维修定额项目（物理删除）
     */
    @DeleteMapping("/delete")
    @Log(title = "删除维修定额项目", value = OperateTypeEnum.DELETE)
    @PreAuthorize("hasAuthority('equipment:emmaintenanceprojectquota:delete')")
    public Map<String, Object> delete(@RequestParam("id") Long id) {
        final String methodName = "EMaintenanceProjectQuotaController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);
        service.delete(id);
        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }
    /**
     * DTO 转 Map（避免 Response.toResult(T) 对 Serializable 的编译限制）
     * 同时兼容前端可能使用的 quotaNo 字段
     */
    private Map<String, Object> toViewMap(EMaintenanceProjectQuotaDTO dto) {
        Map<String, Object> m = new HashMap<>();
        if (dto == null) {
            return m;
        }
        m.put("id", dto.getId());
        m.put("quotaCode", dto.getQuotaCode());
        m.put("quotaNo", dto.getQuotaCode());
        m.put("projectName", dto.getProjectName());
        m.put("projectContent", dto.getProjectContent());
        m.put("unit", dto.getUnit());
        m.put("amount", dto.getAmount());
        m.put("amountExcludingTax", dto.getAmount());
        m.put("createTime", dto.getCreateTime());
        m.put("updateTime", dto.getUpdateTime());
        return m;
    }
}
