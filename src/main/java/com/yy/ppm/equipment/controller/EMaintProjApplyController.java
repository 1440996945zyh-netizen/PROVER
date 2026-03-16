package com.yy.ppm.equipment.controller;


import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.equipment.bean.dto.EMEquipRepairContractDTO;
import com.yy.ppm.equipment.bean.dto.EMaintProjApplyDTO;
import com.yy.ppm.equipment.service.EMEquipRepairContractService;
import com.yy.ppm.equipment.service.EMaintProjApplyService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 维修项目申请定额Controller
 * @author system
 */
@RestController
@RequestMapping("/api/internal/EMaintProjApply")
public class EMaintProjApplyController {
    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(EMaintProjApplyController.class);

    @Resource
    private EMaintProjApplyService eMaintProjApplyService;

    /**
     * 维修项目申请定额（分页）
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('equipment:emaintprojapply:query')")
    public Map<String, Object> getList(EMaintProjApplyDTO searchDTO, PageParameter parameter) {
        final String methodName = "MEquipmentOperationController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EMaintProjApplyDTO> result = eMaintProjApplyService.getList(searchDTO,parameter);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询维修项目申请定额
     */
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('equipment:emaintprojapply:getById')")
    public Map<String, Object> getById(EMaintProjApplyDTO searchDTO) {
        final String methodName = "MEquipmentOperationController:getById";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        EMaintProjApplyDTO result = eMaintProjApplyService.getById(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }


    /**
     * 新增维修项目申请定额
     */
    @PostMapping("/add")
    @Log(title = "新增维修项目申请定额", value = OperateTypeEnum.INSERT)
    @PreAuthorize("hasAuthority('equipment:emaintprojapply:add')")
    public Map<String, Object> add(@RequestBody EMaintProjApplyDTO po) {
        final String methodName = "MEquipmentOperationController:add";
        LOGGER.enter(methodName + "[start]", "po:" + po);

        eMaintProjApplyService.save(po);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }


    /**
     * 修改
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('equipment:emequiprepaircontract:update')")
    @Log(title = "修改维修项目申请定额", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> update(@RequestBody EMaintProjApplyDTO po) {
        final String methodName = "MEquipmentOperationController:add";
        LOGGER.enter(methodName + "[start]", "po:" + po);

        eMaintProjApplyService.save(po);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }


    /**
     * 作废维修项目申请定额
     */
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('equipment:emaintprojapply:delete')")
    @Log(title = "作废维修项目申请定额", value = OperateTypeEnum.DELETE)
    public Map<String, Object> delete(@RequestParam("id") Long id) {
        final String methodName = "MEquipmentOperationController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        eMaintProjApplyService.delete(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("作废成功").toResult();
    }
    /**
     * 删除维修项目申请定额
     */
    @DeleteMapping("/deleteProJect")
    @PreAuthorize("hasAuthority('equipment:emaintprojapply:deleteProJect')")
    @Log(title = "删除维修项目申请定额", value = OperateTypeEnum.DELETE)
    public Map<String, Object> deleteProJect(@RequestParam("id") Long id) {
        final String methodName = "MEquipmentOperationController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        eMaintProjApplyService.deleteProJect(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

}
