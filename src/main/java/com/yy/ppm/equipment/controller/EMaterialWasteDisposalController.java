package com.yy.ppm.equipment.controller;


import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.equipment.bean.dto.EMEquipRepairContractDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWasteDisposalDTO;
import com.yy.ppm.equipment.service.EMaterialWasteDisposalService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 废旧物资处置Controller
 * @author system
 */
@RestController
@RequestMapping("/api/internal/EMaterialWasteDisposal")
public class EMaterialWasteDisposalController {
    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(EMaterialWasteDisposalController.class);

    @Resource
    private EMaterialWasteDisposalService eMaterialWasteDisposalService;

    /**
     * 查询废旧物资处置（分页）
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('equipment:EMaterialWasteDisposal:query')")
    public Map<String, Object> getList(EMaterialWasteDisposalDTO searchDTO, PageParameter parameter) {
        final String methodName = "MEquipmentOperationController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EMaterialWasteDisposalDTO> result = eMaterialWasteDisposalService.getList(searchDTO,parameter);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询废旧物资处置详情
     */
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('equipment:EMaterialWasteDisposal:getById')")
    public Map<String, Object> getById(EMaterialWasteDisposalDTO searchDTO) {
        final String methodName = "MEquipmentOperationController:getById";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        EMaterialWasteDisposalDTO result = eMaterialWasteDisposalService.getById(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }


    /**
     * 新增废旧物资处置
     */
    @PostMapping("/add")
    @Log(title = "新增废旧物资", value = OperateTypeEnum.INSERT)
    @PreAuthorize("hasAuthority('equipment:EMaterialWasteDisposal:add')")
    public Map<String, Object> add(@RequestBody EMaterialWasteDisposalDTO po) {
        final String methodName = "MEquipmentOperationController:add";
        LOGGER.enter(methodName + "[start]", "po:" + po);

        eMaterialWasteDisposalService.save(po);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 新增
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('equipment:EMaterialWasteDisposal:update')")
    @Log(title = "修改维修单位", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> update(@RequestBody EMaterialWasteDisposalDTO po) {
        final String methodName = "MEquipmentOperationController:add";
        LOGGER.enter(methodName + "[start]", "po:" + po);

        eMaterialWasteDisposalService.save(po);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 删除废旧物资处置
     */
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('equipment:EMaterialWasteDisposal:delete')")
    @Log(title = "删除废旧物资", value = OperateTypeEnum.DELETE)
    public Map<String, Object> delete(@RequestParam("id") Long id) {
        final String methodName = "MEquipmentOperationController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        eMaterialWasteDisposalService.delete(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }


}
