package com.yy.ppm.equipment.controller;

import cn.hutool.core.io.IORuntimeException;
import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.common.excel.export.utils.ResponseUtils;
import com.yy.ppm.equipment.bean.dto.EEquipAllocateDTO;
import com.yy.ppm.equipment.bean.dto.EEquipAllocateSearchDTO;
import com.yy.ppm.equipment.bean.dto.AllocateEquipDTO;
import com.yy.ppm.equipment.service.EEquipAllocateService;
import com.yy.ppm.flowable.bean.dto.BpmProcessInstanceDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 设备调拨Controller
 * @author system
 */
@Validated
@RestController
@RequestMapping("/api/v1/internal/equipAllocate")
public class EEquipAllocateController {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(EEquipAllocateController.class);

    @Autowired
    private EEquipAllocateService allocateService;


    /**
     * 查询设备调拨列表（分页）
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('equipment:equipAllocate:query')")
    public Map<String, Object> getList(EEquipAllocateSearchDTO searchDTO) {
        final String methodName = "EEquipAllocateController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EEquipAllocateDTO> page = allocateService.getList(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(page);
    }

    /**
     * 查询可调拨设备列表（分页）
     */
    @GetMapping("/selectEquip")
    @PreAuthorize("hasAuthority('equipment:equipAllocate:query')")
    public Map<String, Object> selectEquip(EEquipAllocateSearchDTO.EquipSelectSearchDTO searchDTO) {
        final String methodName = "EEquipAllocateController:selectEquip";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<AllocateEquipDTO> page = allocateService.allocateEquipList(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(page);
    }

    /**
     * 根据ID查询设备调拨详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('equipment:equipAllocate:query')")
    public Map<String, Object> getById(@PathVariable("id") Long id) {
        final String methodName = "EEquipAllocateController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        EEquipAllocateDTO dto = allocateService.getDetailByOrderId(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(dto);
    }

    /**
     * 创建设备调拨申请
     */
    @PostMapping
    @PreAuthorize("hasAuthority('equipment:equipAllocate:add')")
    @Log(title = "创建设备调拨申请", value = OperateTypeEnum.INSERT)
    public Map<String, Object> create(@RequestBody @Validated EEquipAllocateDTO dto) {
        final String methodName = "EEquipAllocateController:create";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        int count = allocateService.create(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out(count > 0 ? "创建成功" : "创建失败").toResult(count);
    }

    /**
     * 确认设备调拨
     */
    @PostMapping("/confirm")
    @PreAuthorize("hasAuthority('equipment:equipAllocate:approve')")
    @Log(title = "确认设备调拨", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> confirm(@RequestParam("id") Long id, @RequestParam(value = "flowId", required = false) String flowId) {
        final String methodName = "EEquipAllocateController:confirm";
        LOGGER.enter(methodName + "[start]", "id:" + id + ", flowId:" + flowId);

        int count = allocateService.confirm(id, flowId);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out(count > 0 ? "确认成功" : "确认失败").toResult(count);
    }

    /**
     * 导出设备调拨列表
     */
    @GetMapping("/export")
    @PreAuthorize("hasAuthority('equipment:equipAllocate:export')")
    @Log(title = "导出设备调拨列表", value = OperateTypeEnum.EXPORT)
    public void export(EEquipAllocateSearchDTO searchDTO, HttpServletResponse response) {
        final String methodName = "EEquipAllocateController:export";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        try {
            byte[] bytes = allocateService.exportList(searchDTO);
            try {
                response.getOutputStream().write(bytes);
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        } catch (Exception e) {
            ResponseUtils.resetCompliant(response);
            throw e;
        }

        LOGGER.exit(methodName + "[end]");
    }

    /**
     * 设备调拨提交审批
     */
    @PostMapping("/submitEquipAllocate")
    @PreAuthorize("hasAuthority('bpm:equipment:controller:submitEquipAllocate')")
    @Log(OperateTypeEnum.INSERT)
    public Map<String, Object> submitEquipAllocate(@RequestBody BpmProcessInstanceDTO dto) {
        final String methodName = "BpmBusinessConfigController:insert";
        LOGGER.enter(methodName, "提交业务数据");

        allocateService.submitEquipAllocate(dto);

        LOGGER.exit(methodName, "新增BPM业务配置完成");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 删除设备调拨申请
     * @param id
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('equipment:equipAllocate:delete')")
    @Log(title = "删除设备调拨申请", value = OperateTypeEnum.DELETE)
    public Map<String, Object> delete(@PathVariable("id") Long id) {
        final String methodName = "EEquipAllocateController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);
        int result = allocateService.deleteById(id);
        LOGGER.exit(methodName + "result:" + result);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult(result);
    }

    /**
     * 批量删除设备调拨申请
     * @param ids
     */
    @DeleteMapping("/deleteBatch")
    @PreAuthorize("hasAuthority('equipment:equipAllocate:delete')")
    @Log(title = "批量删除设备调拨申请", value = OperateTypeEnum.DELETE)
    public Map<String, Object> deleteBatch(@RequestBody List<Long> ids) {
        final String methodName = "EEquipAllocateController:deleteBatch";
        LOGGER.enter(methodName + "[start]", "ids:" + ids);
        int result = allocateService.deleteByIds(ids);
        LOGGER.exit(methodName + "result:" + result);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult(result);
    }

}
