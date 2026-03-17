package com.yy.ppm.equipment.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMaterialOutApplicationDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialOutApplicationSearchDTO;
import com.yy.ppm.equipment.service.EMaterialOutApplicationAppService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 物资出库申请Controller
 * @author system
 */
@Validated
@RestController
@RequestMapping("/api/v1/internal/materialOutApplicationApp")
public class EMaterialOutApplicationAppController {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(EMaterialOutApplicationAppController.class);

    @Autowired
    private EMaterialOutApplicationAppService service;

    /**
     * 查询物资出库申请列表（分页）
     */
    @GetMapping("/listApp")
    public Map<String, Object> getListApp(EMaterialOutApplicationSearchDTO searchDTO, PageParameter parameter) {
        final String methodName = "EMaterialOutApplicationController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EMaterialOutApplicationDTO> result = service.getList(searchDTO,parameter);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询物资出库申请
     */
    @GetMapping("/getById")
    public Map<String, Object> getById(@RequestParam("id") Long id) {
        final String methodName = "EMaterialOutApplicationController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        EMaterialOutApplicationDTO result = service.getById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增物资出库申请
     */
    @PostMapping("/save")
    public Map<String, Object> save(@RequestBody EMaterialOutApplicationDTO dto) {
        final String methodName = "EMaterialOutApplicationController:add";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("操作成功").toResult();
    }

    /**
     * 修改物资出库申请
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('equipment:materialOutApplication:update')")
    public Map<String, Object> update(@RequestBody EMaterialOutApplicationDTO dto) {
        final String methodName = "EMaterialOutApplicationController:update";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 删除物资出库申请
     */
    @PostMapping("/deleteApp")
    public Map<String, Object> deleteApp(@RequestBody EMaterialOutApplicationDTO dto) {
        final String methodName = "EMaterialOutApplicationController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + dto.getId());

        service.deleteById(dto.getId());

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 审核物资出库申请
     */
    @PostMapping("/audit")
    public Map<String, Object> audit(@RequestParam("id") Long id,
                                     @RequestParam("status") String status,
                                     @RequestParam(value = "auditRemark", required = false) String auditRemark) {
        final String methodName = "EMaterialOutApplicationController:audit";
        LOGGER.enter(methodName + "[start]", "id:" + id + ", status:" + status + ", auditRemark:" + auditRemark);

        service.audit(id, status, auditRemark);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("审核成功").toResult();
    }

    /**
     * 查询物资出库申请列表（包含明细列表和库存数量，用于出库时选择）
     */
    @GetMapping("/getListWithDetails")
    public Map<String, Object> getListWithDetails(EMaterialOutApplicationSearchDTO searchDTO) {
        final String methodName = "EMaterialOutApplicationController:getListWithDetails";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EMaterialOutApplicationDTO> result = service.getListWithDetails(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 审核物资出库申请
     */
    @PostMapping("/approval")
    public Map<String, Object> approval(@RequestBody EMaterialOutApplicationDTO dto) {

        service.audit(dto.getId(), dto.getStatus(), dto.getAuditRemark());

        return Response.SUCCESS.newBuilder().out("审核成功").toResult();
    }

    /**
     * 审核物资出库申请
     */
    @PostMapping("/reject")
    public Map<String, Object> reject(@RequestBody EMaterialOutApplicationDTO dto) {

        service.audit(dto.getId(), dto.getStatus(), dto.getAuditRemark());

        return Response.SUCCESS.newBuilder().out("驳回成功").toResult();
    }
}

