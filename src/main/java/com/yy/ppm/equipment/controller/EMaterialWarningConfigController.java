package com.yy.ppm.equipment.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.*;

import com.yy.ppm.equipment.bean.po.EMaterialWarningConfigPO;
import com.yy.ppm.equipment.service.EMaterialWarningConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author FanQi
 * @data 2026/3/20 10:39
 * @version 1.0
 * @Description 物资预警配置
 */

@Validated
@RestController
@RequestMapping("/api/v1/internal/EMaterialWarningConfig")
public class EMaterialWarningConfigController {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(EMaterialWarningConfigController.class);

    @Autowired
    private EMaterialWarningConfigService eMaterialWarningConfigService;


    /**
     * 主列表查询物资预警配置
     */
    @PostMapping("/list")
    @PreAuthorize("hasAuthority('equipment:eMaterialWarningConfig:query')")
    public Map<String, Object> getList(EMaterialWarningConfigSearchDTO searchDTO) {
        final String methodName = "EMaterialWarningConfigController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EMaterialWarningConfigDTO> result = eMaterialWarningConfigService.getList(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询物资预警配置
     */
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('equipment:eMaterialWarningConfig:query')")
    public Map<String, Object> getById(@RequestParam("id") Long id) {
        final String methodName = "EMaterialWarningConfigController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        EMaterialWarningConfigDTO result = eMaterialWarningConfigService.getById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }



    /**
     * 新增/修改资预警配置(id是否为null判断)
     */
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('equipment:eMaterialWarningConfig:save')")
    public Map<String, Object> add(@RequestBody EMaterialWarningConfigPO po) {
        final String methodName = "EMaterialWarningConfigController:save";
        LOGGER.enter(methodName + "[start]", "po:" + po);

        eMaterialWarningConfigService.save(po);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 修改状态
     */
    @PostMapping("/updateStatus")
    @PreAuthorize("hasAuthority('equipment:eMaterialWarningConfig:updateStatus')")
    public Map<String, Object> updateStatus(@RequestBody EMaterialWarningConfigPO po) {
        final String methodName = "EMaterialWarningConfigController:updateStatus";
        LOGGER.enter(methodName + "[start]", "po:" + po);

        eMaterialWarningConfigService.updateStatus(po);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 删除资预警配置
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('equipment:eMaterialWarningConfig:delete')")
    public Map<String, Object> delete(@PathVariable("id") Long id) {
        final String methodName = "EMaterialWarningConfigController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        eMaterialWarningConfigService.deleteById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }


}
