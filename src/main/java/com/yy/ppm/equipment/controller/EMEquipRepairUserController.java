package com.yy.ppm.equipment.controller;


import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.equipment.bean.dto.EMEquipRepairContractDTO;
import com.yy.ppm.equipment.bean.dto.EMEquipRepairUserDTO;
import com.yy.ppm.equipment.service.EMEquipRepairContractService;
import com.yy.ppm.equipment.service.EMEquipRepairUserService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 维修人员Controller
 * @author system
 */
@RestController
@RequestMapping("/api/internal/emequiprepairuser")
public class EMEquipRepairUserController {
    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(EMEquipRepairUserController.class);

    @Resource
    private EMEquipRepairUserService emEquipRepairUserService;

    /**
     * 查询维修人员（分页）
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('equipment:emequiprepairuser:query')")
    public Map<String, Object> getList(EMEquipRepairUserDTO searchDTO, PageParameter parameter) {
        final String methodName = "MEquipmentOperationController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EMEquipRepairUserDTO> result = emEquipRepairUserService.getList(searchDTO,parameter);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询维修人员
     */
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('equipment:emequiprepairuser:getById')")
    public Map<String, Object> getById(EMEquipRepairUserDTO searchDTO) {
        final String methodName = "MEquipmentOperationController:getById";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        EMEquipRepairUserDTO result = emEquipRepairUserService.getById(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增维修人员
     */
    @PostMapping("/add")
    @Log(title = "新增维修人员", value = OperateTypeEnum.INSERT)
    @PreAuthorize("hasAuthority('equipment:emequiprepairuser:add')")
    public Map<String, Object> add(@RequestBody EMEquipRepairUserDTO po) {
        final String methodName = "MEquipmentOperationController:add";
        LOGGER.enter(methodName + "[start]", "po:" + po);

        emEquipRepairUserService.save(po);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 新增
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('equipment:emequiprepairuser:update')")
    @Log(title = "修改维修人员", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> update(@RequestBody EMEquipRepairUserDTO po) {
        final String methodName = "MEquipmentOperationController:add";
        LOGGER.enter(methodName + "[start]", "po:" + po);

        emEquipRepairUserService.save(po);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 删除维修人员
     */
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('equipment:emequiprepairuser:delete')")
    @Log(title = "删除维修人员", value = OperateTypeEnum.DELETE)
    public Map<String, Object> delete(@RequestParam("id") Long id) {
        final String methodName = "MEquipmentOperationController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        emEquipRepairUserService.delete(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }
}
