package com.yy.ppm.equipment.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.MEquipmentModificationDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentModificationSearchDTO;
import com.yy.ppm.equipment.service.MEquipmentModificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 设备改造记录Controller
 * @author system
 */
@Validated
@RestController
@RequestMapping("/api/v1/internal/equipmentModification")
public class MEquipmentModificationController {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(MEquipmentModificationController.class);

    @Autowired
    private MEquipmentModificationService service;

    /**
     * 查询设备改造记录列表（分页）
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('equipment:equipmentInfo:query')")
    public Map<String, Object> getList(MEquipmentModificationSearchDTO searchDTO) {
        final String methodName = "MEquipmentModificationController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<MEquipmentModificationDTO> result = service.getList(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询设备改造记录
     */
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('equipment:equipmentInfo:query')")
    public Map<String, Object> getById(@RequestParam("id") Long id) {
        final String methodName = "MEquipmentModificationController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        MEquipmentModificationDTO result = service.getById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增设备改造记录
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('equipment:equipmentInfo:add')")
    public Map<String, Object> add(@RequestBody MEquipmentModificationDTO dto) {
        final String methodName = "MEquipmentModificationController:add";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.add(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 修改设备改造记录
     */
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('equipment:equipmentInfo:update')")
    public Map<String, Object> update(@RequestBody MEquipmentModificationDTO dto) {
        final String methodName = "MEquipmentModificationController:update";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.update(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 删除设备改造记录
     */
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('equipment:equipmentInfo:remove')")
    public Map<String, Object> delete(@RequestParam("id") Long id) {
        final String methodName = "MEquipmentModificationController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        service.delete(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 批量删除设备改造记录
     */
    @PostMapping("/deleteBatch")
    @PreAuthorize("hasAuthority('equipment:equipmentInfo:remove')")
    public Map<String, Object> deleteBatch(@RequestBody List<Long> ids) {
        final String methodName = "MEquipmentModificationController:deleteBatch";
        LOGGER.enter(methodName + "[start]", "ids:" + ids);

        service.deleteBatch(ids);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }
}

