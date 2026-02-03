package com.yy.ppm.flowable.controller;


import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.flowable.bean.dto.BpmBusinessConfigDTO;
import com.yy.ppm.flowable.bean.dto.BpmBusinessConfigSearchDTO;
import com.yy.ppm.flowable.service.BpmBusinessConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Description BPM业务配置Controller
 */
@RestController
@RequestMapping(value = "/api/internal/bpmBusinessConfig")
@Validated
@Tag(name = "BPM.业务配置")
public class BpmBusinessConfigController {

    private static final MicroLogger LOGGER = new MicroLogger(BpmBusinessConfigController.class);

    @Resource
    private BpmBusinessConfigService bpmBusinessConfigService;
    /**
     * 分页查询列表
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('bpm:businessConfig:query')")
    @Log(OperateTypeEnum.QUERY)
    @Operation(summary = "分页查询BPM业务配置列表")
    public Map<String, Object> getList(@Validated BpmBusinessConfigSearchDTO searchDTO) {
        final String methodName = "BpmBusinessConfigController:getList";
        LOGGER.enter(methodName, "分页查询BPM业务配置列表");

        Pages<BpmBusinessConfigDTO> pages = bpmBusinessConfigService.getList(searchDTO);

        LOGGER.exit(methodName, "查询BPM业务配置列表完成");
        return Response.SUCCESS.newBuilder().toResult(pages);
    }

    /**
     * 新增
     */
    @PostMapping("/insert")
    @PreAuthorize("hasAuthority('bpm:businessConfig:insert')")
    @Log(OperateTypeEnum.INSERT)
    @Operation(summary = "新增BPM业务配置")
    public Map<String, Object> insert(@RequestBody @Validated BpmBusinessConfigDTO dto) {
        final String methodName = "BpmBusinessConfigController:insert";
        LOGGER.enter(methodName, "新增BPM业务配置");

        bpmBusinessConfigService.insert(dto);

        LOGGER.exit(methodName, "新增BPM业务配置完成");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('bpm:businessConfig:update')")
    @Log(OperateTypeEnum.UPDATE)
    @Operation(summary = "修改BPM业务配置")
    public Map<String, Object> update(@RequestBody @Validated BpmBusinessConfigDTO dto) {
        final String methodName = "BpmBusinessConfigController:update";
        LOGGER.enter(methodName, "修改BPM业务配置");

        bpmBusinessConfigService.update(dto);

        LOGGER.exit(methodName, "修改BPM业务配置完成");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 根据ID删除
     */
    @DeleteMapping("/deleteById/{id}")
    @PreAuthorize("hasAuthority('bpm:businessConfig:delete')")
    @Log(OperateTypeEnum.DELETE)
    @Operation(summary = "根据ID删除BPM业务配置")
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "BpmBusinessConfigController:deleteById";
        LOGGER.enter(methodName, "根据ID删除BPM业务配置");

        bpmBusinessConfigService.deleteById(id);

        LOGGER.exit(methodName, "删除BPM业务配置完成");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 根据ID查询详情
     */
    @GetMapping("/getDetail/{id}")
    @PreAuthorize("hasAuthority('bpm:businessConfig:query')")
    @Log(OperateTypeEnum.QUERY)
    @Operation(summary = "根据ID查询BPM业务配置详情")
    public Map<String, Object> getDetail(@PathVariable("id") Long id) {
        final String methodName = "BpmBusinessConfigController:getDetail";
        LOGGER.enter(methodName, "根据ID查询BPM业务配置详情");

        BpmBusinessConfigDTO dto = bpmBusinessConfigService.getDetail(id);

        LOGGER.exit(methodName, "查询BPM业务配置详情完成");
        return Response.SUCCESS.newBuilder().toResult(dto);
    }

    /**
     * 根据菜单和按钮获取流程定义
     */
    @GetMapping("/getProcDefId")
    @PreAuthorize("hasAuthority('bpm:businessConfig:getProcDefId')")
    @Log(OperateTypeEnum.QUERY)
    @Operation(summary = "根据ID查询BPM业务配置详情")
    public Map<String, Object> getDetail(@RequestParam("businessId") Long businessId,@RequestParam("businessTypeCode") String businessTypeCode) {
        final String methodName = "BpmBusinessConfigController:getProcDefId";
        LOGGER.enter(methodName, "根据菜单和流程业务类型获取流程定义");

        String procDefId = bpmBusinessConfigService.getProcDefId(businessId,businessTypeCode);

        LOGGER.exit(methodName, "根据菜单和流程业务类型获取流程定义");
        return Response.SUCCESS.newBuilder().toResult(procDefId);
    }
}
