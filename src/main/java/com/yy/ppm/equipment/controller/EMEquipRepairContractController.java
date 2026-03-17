package com.yy.ppm.equipment.controller;


import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.equipment.bean.dto.EMEquipRepairContractDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentOperationDTO;
import com.yy.ppm.equipment.bean.po.MEquipmentOperationPO;
import com.yy.ppm.equipment.service.EMEquipRepairContractService;
import com.yy.ppm.equipment.service.MEquipmentOperationService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 维修单位Controller
 * @author system
 */
@RestController
@RequestMapping("/api/internal/EMEquipRepairContract")
public class EMEquipRepairContractController {
    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(EMEquipRepairContractController.class);

    @Resource
    private EMEquipRepairContractService emEquipRepairContractService;

    /**
     * 查询维修单位（分页）
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('equipment:emequiprepaircontract:query')")
    public Map<String, Object> getList(EMEquipRepairContractDTO searchDTO, PageParameter parameter) {
        final String methodName = "MEquipmentOperationController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EMEquipRepairContractDTO> result = emEquipRepairContractService.getList(searchDTO,parameter);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询维修单位
     */
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('equipment:emequiprepaircontract:getById')")
    public Map<String, Object> getById(EMEquipRepairContractDTO searchDTO) {
        final String methodName = "MEquipmentOperationController:getById";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        EMEquipRepairContractDTO result = emEquipRepairContractService.getById(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询维修单位
     */
    @GetMapping("/getOutTypeNum")
    @PreAuthorize("hasAuthority('equipment:emequiprepaircontract:getOutTypeNum')")
    public Map<String, Object> getOutTypeNum() {
        final String methodName = "MEquipmentOperationController:getById";
        LOGGER.enter(methodName + "[start]", "searchDTO:");

        EMEquipRepairContractDTO result = emEquipRepairContractService.getOutTypeNum();

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 维修单位名称
     */
    @GetMapping("/queryUnitName")
    @PreAuthorize("hasAuthority('equipment:emequiprepaircontract:queryUnitName')")
    public Map<String, Object> queryUnitName(EMEquipRepairContractDTO searchDTO) {
        final String methodName = "MEquipmentOperationController:getById";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        List<EMEquipRepairContractDTO> result = emEquipRepairContractService.queryUnitName(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增维修单位
     */
    @PostMapping("/add")
    @Log(title = "新增维修单位", value = OperateTypeEnum.INSERT)
    @PreAuthorize("hasAuthority('equipment:emequiprepaircontract:add')")
    public Map<String, Object> add(@RequestBody EMEquipRepairContractDTO po) {
        final String methodName = "MEquipmentOperationController:add";
        LOGGER.enter(methodName + "[start]", "po:" + po);

        emEquipRepairContractService.save(po);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 新增
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('equipment:emequiprepaircontract:update')")
    @Log(title = "修改维修单位", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> update(@RequestBody EMEquipRepairContractDTO po) {
        final String methodName = "MEquipmentOperationController:add";
        LOGGER.enter(methodName + "[start]", "po:" + po);

        emEquipRepairContractService.save(po);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 删除维修单位
     */
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('equipment:emequiprepaircontract:delete')")
    @Log(title = "删除维修单位", value = OperateTypeEnum.DELETE)
    public Map<String, Object> delete(@RequestParam("id") Long id) {
        final String methodName = "MEquipmentOperationController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        emEquipRepairContractService.delete(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 根据设备ID和outType查询维修单位列表
     */
    @GetMapping("/getRepairContractByEquipId")
    @PreAuthorize("hasAuthority('equipment:emequiprepaircontract:query')")
    public Map<String, Object> getRepairContractByEquipId(@RequestParam("equipId") Long equipId,
                                                          @RequestParam("outType") String outType) {
        final String methodName = "EMEquipRepairContractController:getRepairContractByEquipId";
        LOGGER.enter(methodName + "[start]", "equipId:" + equipId + ", outType:" + outType);

        List<EMEquipRepairContractDTO> result = emEquipRepairContractService.getRepairContractByEquipId(equipId, outType);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
}
