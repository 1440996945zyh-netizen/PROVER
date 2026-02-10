package com.yy.ppm.example.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.example.bean.dto.BpmApplicationExampleDTO;
import com.yy.ppm.example.bean.dto.BpmApplicationExampleSearchDTO;
import com.yy.ppm.example.service.BpmApplicationExampleService;
import com.yy.ppm.flowable.bean.dto.BpmBusinessConfigDTO;
import com.yy.ppm.flowable.bean.dto.BpmProcessInstanceDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Description BPM应用示例Controller
 */
@RestController
@RequestMapping(value = "/api/internal/bpmApplicationExample")
@Validated
@Tag(name = "BPM.应用示例")
public class BpmApplicationExampleController {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(BpmApplicationExampleController.class);

    @Resource
    private BpmApplicationExampleService bpmApplicationExampleService;

    /**
     * 新增
     */
    @PostMapping("/insert")
    @PreAuthorize("hasAuthority('bpm:application:example:insert')")
    @Log(OperateTypeEnum.INSERT)
    @Operation(summary = "新增BPM应用示例")
    public Map<String, Object> insert(@RequestBody @Validated BpmApplicationExampleDTO dto) {
        final String methodName = "BpmApplicationExampleController:insert";
        LOGGER.enter(methodName, "新增BPM应用示例[start]");
        dto.setApprovalStatus("0");
        bpmApplicationExampleService.insert(dto);

        LOGGER.exit(methodName, "新增BPM应用示例[end]");
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('bpm:application:example:update')")
    @Log(OperateTypeEnum.UPDATE)
    @Operation(summary = "修改BPM应用示例")
    public Map<String, Object> update(@RequestBody @Validated BpmApplicationExampleDTO dto) {
        final String methodName = "BpmApplicationExampleController:update";
        LOGGER.enter(methodName, "修改BPM应用示例[start]");

        bpmApplicationExampleService.update(dto);

        LOGGER.exit(methodName, "修改BPM应用示例[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 根据ID删除
     */
    @DeleteMapping("/deleteById/{id}")
    @PreAuthorize("hasAuthority('bpm:application:example:delete')")
    @Log(OperateTypeEnum.DELETE)
    @Operation(summary = "根据ID删除BPM应用示例")
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "BpmApplicationExampleController:deleteById";
        LOGGER.enter(methodName, "根据ID删除BPM应用示例[start]");

        bpmApplicationExampleService.deleteById(id);

        LOGGER.exit(methodName, "根据ID删除BPM应用示例[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 根据ID查询详情
     */
    @GetMapping("/getDetail/{id}")
    @PreAuthorize("hasAuthority('bpm:application:example:query')")
    @Log(OperateTypeEnum.QUERY)
    @Operation(summary = "根据ID查询BPM应用示例详情")
    public Map<String, Object> getDetail(@PathVariable("id") Long id) {
        final String methodName = "BpmApplicationExampleController:getDetail";
        LOGGER.enter(methodName, "根据ID查询BPM应用示例详情[start]");

        BpmApplicationExampleDTO dto = bpmApplicationExampleService.getDetail(id);

        LOGGER.exit(methodName, "根据ID查询BPM应用示例详情[end]");
        return Response.SUCCESS.newBuilder().toResult(dto);
    }

    /**
     * 分页查询列表
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('bpm:application:example:query')")
    @Log(OperateTypeEnum.QUERY)
    @Operation(summary = "分页查询BPM应用示例列表")
    public Map<String, Object> getList(@Validated BpmApplicationExampleSearchDTO searchDTO) {
        final String methodName = "BpmApplicationExampleController:getList";
        LOGGER.enter(methodName, "分页查询BPM应用示例列表[start]");

        Pages<BpmApplicationExampleDTO> pages = bpmApplicationExampleService.getList(searchDTO);

        LOGGER.exit(methodName, "分页查询BPM应用示例列表[end]");
        return Response.SUCCESS.newBuilder().toResult(pages);
    }

    /**
     * 查询所有数据
     */
    @GetMapping("/getAllList")
    @PreAuthorize("hasAuthority('bpm:application:example:query')")
    @Log(OperateTypeEnum.QUERY)
    @Operation(summary = "查询所有BPM应用示例数据")
    public Map<String, Object> getAllList() {
        final String methodName = "BpmApplicationExampleController:getAllList";
        LOGGER.enter(methodName, "查询所有BPM应用示例数据[start]");

        List<BpmApplicationExampleDTO> list = bpmApplicationExampleService.getAllList();

        LOGGER.exit(methodName, "查询所有BPM应用示例数据[end]");
        return Response.SUCCESS.newBuilder().toResult(list);
    }

    /**
     * 提交耗材审批
     */
    @PostMapping("/submitConsumablesPayment")
    @PreAuthorize("hasAuthority('bpm:application:example:consumablespayment')")
    @Log(OperateTypeEnum.INSERT)
    public Map<String, Object> submitCommercial(@RequestBody BpmProcessInstanceDTO dto) {
        final String methodName = "BpmBusinessConfigController:insert";
        LOGGER.enter(methodName, "提交业务数据");

        bpmApplicationExampleService.submit(dto);

        LOGGER.exit(methodName, "新增BPM业务配置完成");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 提交商用审批
     */
    @PostMapping("/submitCommercialPayment")
    @PreAuthorize("hasAuthority('bpm:application:example:commercialpayment')")
    @Log(OperateTypeEnum.INSERT)
    public Map<String, Object> submitConsumables(@RequestBody BpmProcessInstanceDTO dto) {
        final String methodName = "BpmBusinessConfigController:insert";
        LOGGER.enter(methodName, "提交业务数据");

        bpmApplicationExampleService.submit(dto);

        LOGGER.exit(methodName, "新增BPM业务配置完成");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }
    /**
     * 提交统一付款审批
     */
    @PostMapping("/submitUnificationPayment")
    @PreAuthorize("hasAuthority('bpm:application:example:unificationpayment')")
    @Log(OperateTypeEnum.INSERT)
    public Map<String, Object> submitUnification(@RequestBody BpmProcessInstanceDTO dto) {
        final String methodName = "BpmBusinessConfigController:insert";
        LOGGER.enter(methodName, "提交业务数据");

        bpmApplicationExampleService.submit(dto);

        LOGGER.exit(methodName, "新增BPM业务配置完成");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }
}
