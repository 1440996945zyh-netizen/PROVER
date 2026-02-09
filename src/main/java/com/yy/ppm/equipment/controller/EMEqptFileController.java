package com.yy.ppm.equipment.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMEqptFileDTO;
import com.yy.ppm.equipment.bean.dto.EMEqptFileSearchDTO;
import com.yy.ppm.equipment.service.EMEqptFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 设备资料文件Controller
 * @author system
 */
@Validated
@RestController
@RequestMapping("/api/v1/internal/eqptFile")
public class EMEqptFileController {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(EMEqptFileController.class);

    @Autowired
    private EMEqptFileService service;

    /**
     * 查询设备资料文件列表（分页）
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('equipment:eqptFile:query')")
    public Map<String, Object> getList(EMEqptFileSearchDTO searchDTO) {
        final String methodName = "EMEqptFileController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EMEqptFileDTO> result = service.getList(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询设备资料文件
     */
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('equipment:eqptFile:query')")
    public Map<String, Object> getById(@RequestParam("id") Long id) {
        final String methodName = "EMEqptFileController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        EMEqptFileDTO result = service.getById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增设备资料文件
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('equipment:eqptFile:add')")
    public Map<String, Object> add(@RequestBody EMEqptFileDTO dto) {
        final String methodName = "EMEqptFileController:add";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 修改设备资料文件
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('equipment:eqptFile:update')")
    public Map<String, Object> update(@RequestBody EMEqptFileDTO dto) {
        final String methodName = "EMEqptFileController:update";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 删除设备资料文件
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('equipment:eqptFile:delete')")
    public Map<String, Object> delete(@PathVariable("id") Long id) {
        final String methodName = "EMEqptFileController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        service.deleteById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }
}

