package com.yy.ppm.equipment.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseSearchDTO;
import com.yy.ppm.equipment.service.EMaterialWarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 物资仓库Controller
 * @author system
 */
@Validated
@RestController
@RequestMapping("/api/v1/internal/materialWarehouse")
public class EMaterialWarehouseController {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(EMaterialWarehouseController.class);

    @Autowired
    private EMaterialWarehouseService service;

    /**
     * 查询物资仓库列表（分页）
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('equipment:materialWarehouse:query')")
    public Map<String, Object> getList(EMaterialWarehouseSearchDTO searchDTO) {
        final String methodName = "EMaterialWarehouseController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EMaterialWarehouseDTO> result = service.getList(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询物资仓库
     */
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('equipment:materialWarehouse:query')")
    public Map<String, Object> getById(@RequestParam("id") Long id) {
        final String methodName = "EMaterialWarehouseController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        EMaterialWarehouseDTO result = service.getById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增物资仓库
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('equipment:materialWarehouse:add')")
    public Map<String, Object> add(@RequestBody EMaterialWarehouseDTO dto) {
        final String methodName = "EMaterialWarehouseController:add";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 修改物资仓库
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('equipment:materialWarehouse:update')")
    public Map<String, Object> update(@RequestBody EMaterialWarehouseDTO dto) {
        final String methodName = "EMaterialWarehouseController:update";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 删除物资仓库
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('equipment:materialWarehouse:delete')")
    public Map<String, Object> delete(@PathVariable("id") Long id) {
        final String methodName = "EMaterialWarehouseController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        service.deleteById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 查询物资仓库列表（不分页，用于下拉框）
     */
    @GetMapping("/listForSelect")
    @PreAuthorize("hasAuthority('equipment:materialWarehouse:query')")
    public Map<String, Object> getListForSelect() {
        final String methodName = "EMaterialWarehouseController:getListForSelect";
        LOGGER.enter(methodName + "[start]");

        java.util.List<EMaterialWarehouseDTO> result = service.getListForSelect();

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
}

