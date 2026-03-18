package com.yy.ppm.equipment.controller;

import cn.hutool.core.io.IORuntimeException;
import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.common.excel.export.utils.ResponseUtils;
import com.yy.ppm.equipment.bean.dto.EEquipScrapDTO;
import com.yy.ppm.equipment.bean.dto.EEquipScrapSearchDTO;
import com.yy.ppm.equipment.bean.dto.ScrapEquipDTO;
import com.yy.ppm.equipment.service.EEquipScrapService;
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
 * @Author: fanxianjin
 * @Desc: 设备报废Controller
 * @Date: 2026/2/28 14:28
 */
@Validated
@RestController
@RequestMapping("/api/v1/internal/equipScrap")
public class EEquipScrapController {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(EEquipScrapController.class);

    @Autowired
    private EEquipScrapService scrapService;


    /**
     * 查询设备报废列表（分页）
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('equipment:equipScrap:query')")
    public Map<String, Object> getList(EEquipScrapSearchDTO searchDTO) {
        final String methodName = "EEquipScrapController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EEquipScrapDTO> page = scrapService.getList(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(page);
    }

    /**
     * 查询可报废设备列表（分页）
     */
    @GetMapping("/selectEquip")
    @PreAuthorize("hasAuthority('equipment:equipScrap:query')")
    public Map<String, Object> selectEquip(EEquipScrapSearchDTO.EquipSelectSearchDTO searchDTO) {
        final String methodName = "EEquipScrapController:selectEquip";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<ScrapEquipDTO> page = scrapService.scrapEquipList(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(page);
    }

    /**
     * 根据ID查询设备报废详情
     */
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('equipment:equipScrap:query')")
    public Map<String, Object> getById(@RequestParam("id") Long id) {
        final String methodName = "EEquipScrapController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        EEquipScrapDTO dto = scrapService.getById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(dto);
    }


    /**
     * 查询设备报废详情（含设备列表）
     */
    @GetMapping("/getDetail/{orderId}")
    @PreAuthorize("hasAuthority('equipment:equipScrap:query')")
    public Map<String, Object> getDetail(@PathVariable("orderId") Long orderId) {
        final String methodName = "EEquipScrapController:getDetail";
        LOGGER.enter(methodName + "[start]", "orderId:" + orderId);

        EEquipScrapDTO dto = scrapService.getDetailByOrderId(orderId);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(dto);
    }

    /**
     * 创建设备报废申请
     */
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('equipment:equipScrap:add')")
    @Log(title = "创建设备报废申请", value = OperateTypeEnum.INSERT)
    public Map<String, Object> create(@RequestBody @Validated EEquipScrapDTO dto) {
        final String methodName = "EEquipScrapController:create";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        int count = scrapService.create(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out(count > 0 ? "创建成功" : "创建失败").toResult(count);
    }

    /**
     * 确认设备报废
     */
    @PostMapping("/confirm")
    @PreAuthorize("hasAuthority('equipment:equipScrap:approve')")
    @Log(title = "确认设备报废", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> confirm(@RequestParam("id") Long id, @RequestParam(value = "flowId", required = false) String flowId) {
        final String methodName = "EEquipScrapController:confirm";
        LOGGER.enter(methodName + "[start]", "id:" + id + ", flowId:" + flowId);

        int count = scrapService.confirm(id, flowId);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out(count > 0 ? "确认成功" : "确认失败").toResult(count);
    }

    /**
     * 导出设备报废列表
     */
    @GetMapping("/export")
    @PreAuthorize("hasAuthority('equipment:equipScrap:export')")
    @Log(title = "导出设备报废列表", value = OperateTypeEnum.EXPORT)
    public void export(EEquipScrapSearchDTO searchDTO, HttpServletResponse response) {
        final String methodName = "EEquipScrapController:export";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        try {
            byte[] bytes = scrapService.exportList(searchDTO);
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
     * 设备报废提交审批
     */
    @PostMapping("/submitEquipScrap")
    @PreAuthorize("hasAuthority('bpm:equipment:controller:submitEquipScrap')")
    @Log(OperateTypeEnum.INSERT)
    public Map<String, Object> submitEquipScrap(@RequestBody BpmProcessInstanceDTO dto) {
        final String methodName = "BpmBusinessConfigController:insert";
        LOGGER.enter(methodName, "提交业务数据");

        scrapService.submitEquipScrap(dto);

        LOGGER.exit(methodName, "新增BPM业务配置完成");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 删除设备报废申请
     * @param id
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('equipment:equipScrap:delete')")
    @Log(title = "删除设备报废申请", value = OperateTypeEnum.DELETE)
    public Map<String, Object> delete(@PathVariable("id") Long id) {
        final String methodName = "EEquipScrapController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);
        int result = scrapService.deleteById(id);
        LOGGER.exit(methodName + "result:" + result);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult(result);
    }

    /**
     * 批量删除设备报废申请
     * @param ids
     */
    @DeleteMapping("/deleteBatch")
    @PreAuthorize("hasAuthority('equipment:equipScrap:delete')")
    @Log(title = "批量删除设备报废申请", value = OperateTypeEnum.DELETE)
    public Map<String, Object> deleteBatch(@RequestBody List<Long> ids) {
        final String methodName = "EEquipScrapController:deleteBatch";
        LOGGER.enter(methodName + "[start]", "ids:" + ids);
        int result = scrapService.deleteByIds(ids);
        LOGGER.exit(methodName + "result:" + result);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult(result);
    }

}
