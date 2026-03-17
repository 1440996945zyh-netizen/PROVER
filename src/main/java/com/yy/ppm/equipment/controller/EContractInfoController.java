package com.yy.ppm.equipment.controller;


import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.equipment.bean.dto.EContractInfoDTO;
import com.yy.ppm.equipment.bean.dto.EMEquipRepairContractDTO;
import com.yy.ppm.equipment.service.EContractInfoService;
import com.yy.ppm.equipment.service.EMEquipRepairContractService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 合同信息Controller
 * @author system
 */
@RestController
@RequestMapping("/api/internal/EContractInfoContract")
public class EContractInfoController {
    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(EContractInfoController.class);

    @Resource
    private EContractInfoService eContractInfoService;

    /**
     * 查询合同信息（分页）
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('equipment:econtractinfocontract:query')")
    public Map<String, Object> getList(EContractInfoDTO searchDTO, PageParameter parameter) {
        final String methodName = "MEquipmentOperationController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EContractInfoDTO> result = eContractInfoService.getList(searchDTO,parameter);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询合同信息
     */
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('equipment:econtractinfocontract:getById')")
    public Map<String, Object> getById(EContractInfoDTO searchDTO) {
        final String methodName = "MEquipmentOperationController:getById";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        EContractInfoDTO result = eContractInfoService.getById(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }


    /**
     * 新增合同信息
     */
    @PostMapping("/add")
    @Log(title = "新增合同信息", value = OperateTypeEnum.INSERT)
    @PreAuthorize("hasAuthority('equipment:econtractinfocontract:add')")
    public Map<String, Object> add(@RequestBody EContractInfoDTO po) {
        final String methodName = "MEquipmentOperationController:add";
        LOGGER.enter(methodName + "[start]", "po:" + po);

        eContractInfoService.save(po);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 新增
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('equipment:econtractinfocontract:update')")
    @Log(title = "修改合同信息", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> update(@RequestBody EContractInfoDTO po) {
        final String methodName = "MEquipmentOperationController:add";
        LOGGER.enter(methodName + "[start]", "po:" + po);

        eContractInfoService.save(po);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 删除合同信息
     */
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('equipment:econtractinfocontract:delete')")
    @Log(title = "删除合同信息", value = OperateTypeEnum.DELETE)
    public Map<String, Object> delete(@RequestParam("id") Long id) {
        final String methodName = "MEquipmentOperationController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        eContractInfoService.delete(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }


}
