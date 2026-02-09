package com.yy.ppm.equipment.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseOutDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseOutSearchDTO;
import com.yy.ppm.equipment.service.EMaterialWarehouseOutAppService;
import com.yy.ppm.equipment.service.EMaterialWarehouseOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 物资出库Controller
 * @author system
 */
@Validated
@RestController
@RequestMapping("/api/v1/internal/materialWarehouseOutApp")
public class EMaterialWarehouseOutAppController {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(EMaterialWarehouseOutAppController.class);

    @Autowired
    private EMaterialWarehouseOutAppService service;

    /**
     * 查询物资出库列表（分页）
     */
    @GetMapping("/list")
    public Map<String, Object> getList(EMaterialWarehouseOutSearchDTO searchDTO) {
        final String methodName = "EMaterialWarehouseOutController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EMaterialWarehouseOutDTO> result = service.getList(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询物资出库
     */
    @GetMapping("/getById")
    public Map<String, Object> getById(@RequestParam("id") Long id) {
        final String methodName = "EMaterialWarehouseOutController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        EMaterialWarehouseOutDTO result = service.getById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增物资出库
     */
    @PostMapping("/save")
    public Map<String, Object> add(@RequestBody EMaterialWarehouseOutDTO dto) {
        final String methodName = "EMaterialWarehouseOutController:add";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 修改物资出库
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody EMaterialWarehouseOutDTO dto) {
        final String methodName = "EMaterialWarehouseOutController:update";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 删除物资出库
     */
    @PostMapping("/delete")
    public Map<String, Object> delete(@RequestBody EMaterialWarehouseOutDTO dto) {
        final String methodName = "EMaterialWarehouseOutController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + dto.getId());
        service.deleteById(dto.getId());
        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 确认物资出库
     */
    @PutMapping("/confirm/{id}")
    public Map<String, Object> confirm(@PathVariable("id") Long id) {
        final String methodName = "EMaterialWarehouseOutController:confirm";
        LOGGER.enter(methodName + "[start]", "id:" + id);
        service.confirm(id);
        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("确认成功").toResult();
    }
}

