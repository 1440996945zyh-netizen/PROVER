package com.yy.ppm.equipment.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.equipment.bean.dto.*;
import com.yy.ppm.equipment.service.EMaterialApplicationService;
import com.yy.ppm.flowable.bean.dto.BpmProcessInstanceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 物资申报Controller
 * @author system
 */
@Validated
@RestController
@RequestMapping("/api/v1/internal/materialApplication")
public class EMaterialApplicationController {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(EMaterialApplicationController.class);

    @Autowired
    private EMaterialApplicationService service;

    /**
     * 查询物资申报列表（分页）
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('equipment:materialApplication:query')")
    public Map<String, Object> getList(EMaterialApplicationSearchDTO searchDTO) {
        final String methodName = "EMaterialApplicationController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EMaterialApplicationDTO> result = service.getList(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询物资申报
     */
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('equipment:materialApplication:query')")
    public Map<String, Object> getById(@RequestParam("id") Long id) {
        final String methodName = "EMaterialApplicationController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        EMaterialApplicationDTO result = service.getById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增物资申报
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('equipment:materialApplication:add')")
    public Map<String, Object> add(@RequestBody EMaterialApplicationDTO dto) {
        final String methodName = "EMaterialApplicationController:add";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 修改物资申报
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('equipment:materialApplication:update')")
    public Map<String, Object> update(@RequestBody EMaterialApplicationDTO dto) {
        final String methodName = "EMaterialApplicationController:update";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 删除物资申报
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('equipment:materialApplication:delete')")
    public Map<String, Object> delete(@PathVariable("id") Long id) {
        final String methodName = "EMaterialApplicationController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        service.deleteById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 审批物资申报
     */
    @PostMapping("/approve")
    @PreAuthorize("hasAuthority('equipment:materialApplication:approve')")
    public Map<String, Object> approve(@RequestParam("id") Long id,
                                        @RequestParam("status") String status,
                                        @RequestParam(value = "approvalRemark", required = false) String approvalRemark) {
        final String methodName = "EMaterialApplicationController:approve";
        LOGGER.enter(methodName + "[start]", "id:" + id + ", status:" + status + ", approvalRemark:" + approvalRemark);

        service.approve(id, status, approvalRemark);

        String statusName = "3".equals(status) ? "审批通过" : "驳回";
        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out(statusName + "成功").toResult();
    }

    /**
     * 查询申报物资明细列表（用于采购时选择，关联物资申报表，只查询已审批通过的）
     */
    @GetMapping("/getDetailListForPurchase")
    @PreAuthorize("hasAuthority('equipment:materialApplication:query')")
    public Map<String, Object> getDetailListForPurchase(EMaterialApplicationDetailSearchDTO searchDTO) {
        final String methodName = "EMaterialApplicationController:getDetailListForPurchase";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EMaterialApplicationDetailDTO> result = service.getDetailListForPurchase(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询物资申报明细关联采购明细列表（用于入库时选择）
     */
    @GetMapping("/getDetailListForWarehouseIn")
    @PreAuthorize("hasAuthority('equipment:materialWarehouseIn:query')")
    public Map<String, Object> getDetailListForWarehouseIn(EMaterialApplicationDetailForWarehouseInSearchDTO searchDTO) {
        final String methodName = "EMaterialApplicationController:getDetailListForWarehouseIn";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EMaterialApplicationDetailForWarehouseInDTO> result = service.getDetailListForWarehouseIn(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询物资申报主表列表（包含明细列表，用于出库申请时选择）
     */
    @GetMapping("/getListWithDetails")
    @PreAuthorize("hasAuthority('equipment:materialApplication:query')")
    public Map<String, Object> getListWithDetails(EMaterialApplicationSearchDTO searchDTO) {
        final String methodName = "EMaterialApplicationController:getListWithDetails";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EMaterialApplicationDTO> result = service.getListWithDetails(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }


    /**
     * 提交审批
     */
    @PostMapping("/materialApplicationStart")
    @PreAuthorize("hasAuthority('bpm:equipment:controller:materialApplicationStart')")
    @Log(OperateTypeEnum.INSERT)
    public Map<String, Object> submitConsumables(@RequestBody BpmProcessInstanceDTO dto) {
        final String methodName = "BpmBusinessConfigController:insert";
        LOGGER.enter(methodName, "提交业务数据");

        service.submit(dto);

        LOGGER.exit(methodName, "新增BPM业务配置完成");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }
}

