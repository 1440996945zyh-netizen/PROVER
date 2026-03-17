package com.yy.ppm.equipment.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.equipment.bean.dto.ECostSettlementApplyDTO;
import com.yy.ppm.equipment.bean.dto.ECostSettlementApplySearchDTO;
import com.yy.ppm.equipment.bean.dto.EMaintInfoDTO;
import com.yy.ppm.equipment.bean.dto.EMaintInfoSearchDTO;
import com.yy.ppm.equipment.service.ECostSettlementApplyService;
import com.yy.ppm.flowable.bean.dto.BpmProcessInstanceDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 结算管理 Controller
 *
 * @author fanxianjin
 */
@Tag(name = "结算管理")
@Validated
@RestController
@RequestMapping("/api/v1/internal/costSettlementApply")
public class ECostSettlementApplyController {

    private static final MicroLogger LOGGER = new MicroLogger(ECostSettlementApplyController.class);

    @Autowired
    private ECostSettlementApplyService service;

    /**
     * 查询结算申请列表（分页）
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('equipment:costSettlement:query')")
    public Map<String, Object> getList(ECostSettlementApplySearchDTO searchDTO) {
        final String methodName = "ECostSettlementApplyController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<ECostSettlementApplyDTO> result = service.getList(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据主键查询结算申请
     */
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('equipment:costSettlement:query')")
    public Map<String, Object> getById(@RequestParam("id") Long id) {
        final String methodName = "ECostSettlementApplyController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        ECostSettlementApplyDTO result = service.getById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增结算申请
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('equipment:costSettlement:add')")
    public Map<String, Object> add(@RequestBody ECostSettlementApplyDTO dto) {
        final String methodName = "ECostSettlementApplyController:add";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.add(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }

    /**
     * 修改结算申请
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('equipment:costSettlement:update')")
    public Map<String, Object> update(@RequestBody ECostSettlementApplyDTO dto) {
        final String methodName = "ECostSettlementApplyController:update";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.update(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }

    /**
     * 删除结算申请
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('equipment:costSettlement:delete')")
    public Map<String, Object> delete(@PathVariable("id") Long id) {
        final String methodName = "ECostSettlementApplyController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        service.deleteById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 批量删除结算申请
     */
    @DeleteMapping("/deleteBatch")
    @PreAuthorize("hasAuthority('equipment:costSettlement:delete')")
    public Map<String, Object> deleteBatch(@RequestBody List<Long> ids) {
        final String methodName = "ECostSettlementApplyController:deleteBatch";
        LOGGER.enter(methodName + "[start]", "ids:" + ids);

        service.deleteByIds(ids);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 根据 维修单位 + 项目类型 查询已验收未结算工单（含分页搜索）
     */
    @GetMapping("/getAcceptedWorkOrders")
    @PreAuthorize("hasAuthority('equipment:costSettlement:query')")
    public Map<String, Object> getAcceptedWorkOrders(EMaintInfoSearchDTO searchDTO) {
        final String methodName = "ECostSettlementApplyController:getAcceptedWorkOrders";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EMaintInfoDTO> result = service.getAcceptedWorkOrders(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 结算申请提交审批
     */
    @PostMapping("/submitSettlementApply")
    @PreAuthorize("hasAuthority('bpm:equipment:controller:submitSettlementApply')")
    @Log(OperateTypeEnum.INSERT)
    public Map<String, Object> submitSettlementApply(@RequestBody BpmProcessInstanceDTO dto) {
        final String methodName = "BpmBusinessConfigController:insert";
        LOGGER.enter(methodName, "提交业务数据");

        service.submitSettlementApply(dto);

        LOGGER.exit(methodName, "新增BPM业务配置完成");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }
}
