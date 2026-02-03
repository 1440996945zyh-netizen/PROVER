package com.yy.ppm.flowable.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.flowable.bean.dto.BpmProcessListenerDTO;
import com.yy.ppm.flowable.bean.dto.BpmProcessListenerSearchDTO;
import com.yy.ppm.flowable.service.BpmProcessListenerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/internal/bpmProcessListener")
@Validated
@Tag(name = "BPM.流程监听器")
public class BpmProcessListenerController {

    private static final MicroLogger LOGGER = new MicroLogger(BpmProcessListenerController.class);

    @Resource
    private BpmProcessListenerService bpmProcessListenerService;

    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('bpm:processListener:query')")
    @Log(OperateTypeEnum.QUERY)
    @Operation(summary = "分页查询流程监听器列表")
    public Map<String, Object> getList(@Validated BpmProcessListenerSearchDTO searchDTO) {
        final String methodName = "BpmProcessListenerController:getList";
        LOGGER.enter(methodName, "查询流程监听器列表");
        Pages<BpmProcessListenerDTO> pages = bpmProcessListenerService.getList(searchDTO);
        LOGGER.exit(methodName, "查询流程监听器列表完成");
        return Response.SUCCESS.newBuilder().toResult(pages);
    }

    @PostMapping("/insert")
    @PreAuthorize("hasAuthority('bpm:processListener:create')")
    @Log(OperateTypeEnum.INSERT)
    @Operation(summary = "新增流程监听器")
    public Map<String, Object> insert(@RequestBody BpmProcessListenerDTO dto) {
        final String methodName = "BpmProcessListenerController:insert";
        LOGGER.enter(methodName, "新增流程监听器");
        bpmProcessListenerService.insert(dto);
        LOGGER.exit(methodName, "新增完成");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('bpm:processListener:update')")
    @Log(OperateTypeEnum.UPDATE)
    @Operation(summary = "修改流程监听器")
    public Map<String, Object> update(@RequestBody BpmProcessListenerDTO dto) {
        final String methodName = "BpmProcessListenerController:update";
        LOGGER.enter(methodName, "修改流程监听器");
        bpmProcessListenerService.update(dto);
        LOGGER.exit(methodName, "修改完成");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    @DeleteMapping("/deleteById/{id}")
    @PreAuthorize("hasAuthority('bpm:processListener:delete')")
    @Log(OperateTypeEnum.DELETE)
    @Operation(summary = "根据ID删除流程监听器")
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        bpmProcessListenerService.deleteById(id);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    @GetMapping("/getDetail/{id}")
    @PreAuthorize("hasAuthority('bpm:processListener:query')")
    @Log(OperateTypeEnum.QUERY)
    @Operation(summary = "查询详情")
    public Map<String, Object> getDetail(@PathVariable("id") Long id) {
        BpmProcessListenerDTO dto = bpmProcessListenerService.getDetail(id);
        return Response.SUCCESS.newBuilder().toResult(dto);
    }
}
