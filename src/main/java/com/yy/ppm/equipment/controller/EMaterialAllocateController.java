package com.yy.ppm.equipment.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.equipment.bean.dto.EMaterialAllocateDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialAllocateSearchDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialStockDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialStockSearchDTO;
import com.yy.ppm.equipment.service.EMaterialAllocateService;
import com.yy.ppm.flowable.bean.dto.BpmProcessInstanceDTO;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 物资调拨Controller
 * @author system
 */
@Validated
@RestController
@RequestMapping("/api/v1/internal/materialAllocate")
public class EMaterialAllocateController {

    private static final MicroLogger LOGGER = new MicroLogger(EMaterialAllocateController.class);

    @Resource
    private EMaterialAllocateService service;

    /**
     * 查询调拨列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('equipment:materialAllocate:query')")
    public Map<String, Object> getList(EMaterialAllocateSearchDTO searchDTO) {
        final String methodName = "EMaterialAllocateController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);
        Pages<EMaterialAllocateDTO> result = service.getList(searchDTO);
        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询调拨详情
     */
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('equipment:materialAllocate:query')")
    public Map<String, Object> getById(@RequestParam("id") Long id) {
        final String methodName = "EMaterialAllocateController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);
        EMaterialAllocateDTO result = service.getById(id);
        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询待调拨物资列表
     */
    @GetMapping("/selectMaterial")
    @PreAuthorize("hasAuthority('equipment:materialAllocate:query')")
    public Map<String, Object> selectMaterial(EMaterialStockSearchDTO searchDTO) {
        final String methodName = "EMaterialAllocateController:selectMaterial";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);
        Pages<EMaterialStockDTO> result = service.selectMaterial(searchDTO);
        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增调拨单
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('equipment:materialAllocate:add')")
    @Log(title = "物资调拨新增", value = OperateTypeEnum.INSERT)
    public Map<String, Object> add(@RequestBody EMaterialAllocateDTO dto) {
        final String methodName = "EMaterialAllocateController:add";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);
        service.save(dto);
        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult(dto.getId());
    }

    /**
     * 修改调拨单
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('equipment:materialAllocate:update')")
    @Log(title = "物资调拨修改", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> update(@RequestBody EMaterialAllocateDTO dto) {
        final String methodName = "EMaterialAllocateController:update";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);
        service.save(dto);
        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 删除调拨单
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('equipment:materialAllocate:delete')")
    @Log(title = "物资调拨删除", value = OperateTypeEnum.DELETE)
    public Map<String, Object> delete(@PathVariable("id") Long id) {
        final String methodName = "EMaterialAllocateController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);
        service.deleteById(id);
        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 提交调拨审批
     */
    @PostMapping("/submitMaterialAllocate")
    @PreAuthorize("hasAuthority('bpm:equipment:controller:submitMaterialAllocate')")
    @Log(title = "物资调拨提交审批", value = OperateTypeEnum.INSERT)
    public Map<String, Object> submitMaterialAllocate(@RequestBody BpmProcessInstanceDTO dto) {
        final String methodName = "EMaterialAllocateController:submitMaterialAllocate";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);
        service.submitMaterialAllocate(dto);
        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("提交成功").toResult();
    }
}
