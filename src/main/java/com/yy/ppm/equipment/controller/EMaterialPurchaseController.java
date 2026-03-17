package com.yy.ppm.equipment.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMaterialPurchaseDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialPurchaseSearchDTO;
import com.yy.ppm.equipment.service.EMaterialPurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 物资采购Controller
 * @author system
 */
@Validated
@RestController
@RequestMapping("/api/v1/internal/materialPurchase")
public class EMaterialPurchaseController {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(EMaterialPurchaseController.class);

    @Autowired
    private EMaterialPurchaseService service;

    /**
     * 查询物资采购列表（分页）
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('equipment:materialPurchase:query')")
    public Map<String, Object> getList(EMaterialPurchaseSearchDTO searchDTO) {
        final String methodName = "EMaterialPurchaseController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EMaterialPurchaseDTO> result = service.getList(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询物资采购
     */
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('equipment:materialPurchase:query')")
    public Map<String, Object> getById(@RequestParam("id") Long id) {
        final String methodName = "EMaterialPurchaseController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        EMaterialPurchaseDTO result = service.getById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增物资采购
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('equipment:materialPurchase:add')")
    public Map<String, Object> add(@RequestBody EMaterialPurchaseDTO dto) {
        final String methodName = "EMaterialPurchaseController:add";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 修改物资采购
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('equipment:materialPurchase:update')")
    public Map<String, Object> update(@RequestBody EMaterialPurchaseDTO dto) {
        final String methodName = "EMaterialPurchaseController:update";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 删除物资采购
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('equipment:materialPurchase:delete')")
    public Map<String, Object> delete(@PathVariable("id") Long id) {
        final String methodName = "EMaterialPurchaseController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);
        service.deleteById(id);
        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 标记采购失败
     */
    @PutMapping("/markAsFailed")
    @PreAuthorize("hasAuthority('equipment:materialPurchase:update')")
    public Map<String, Object> markAsFailed(@RequestParam("id") Long id, @RequestParam("failureReason") String failureReason) {
        final String methodName = "EMaterialPurchaseController:markAsFailed";
        LOGGER.enter(methodName + "[start]", "id:" + id + ", failureReason:" + failureReason);

        service.markAsFailed(id, failureReason);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("操作成功").toResult();
    }

    /**
     * 审核物资采购
     */
    @PostMapping("/approve")
    @PreAuthorize("hasAuthority('equipment:materialPurchase:approve')")
    public Map<String, Object> approve(@RequestParam("id") Long id,
                                       @RequestParam("status") Integer status,
                                       @RequestParam(value = "approvalRemark", required = false) String approvalRemark) {
        final String methodName = "EMaterialPurchaseController:approve";
        LOGGER.enter(methodName + "[start]", "id:" + id + ", status:" + status + ", approvalRemark:" + approvalRemark);

        service.approve(id, status, approvalRemark);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("审核成功").toResult();
    }
}

