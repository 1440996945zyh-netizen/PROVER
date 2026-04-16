package com.yy.ppm.equipment.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMaterialSupplierDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialSupplierSearchDTO;
import com.yy.ppm.equipment.service.EMaterialSupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 供应商Controller
 */
@Validated
@RestController
@RequestMapping("/api/v1/internal/materialSupplier")
public class EMaterialSupplierController {

    private static final MicroLogger LOGGER = new MicroLogger(EMaterialSupplierController.class);

    @Autowired
    private EMaterialSupplierService service;

    /**
     * 查询供应商列表
     */
    @GetMapping("/list")
        @PreAuthorize("hasAuthority('equipment:materialSupplier:query')")
    public Map<String, Object> getList(EMaterialSupplierSearchDTO searchDTO) {
        final String methodName = "EMaterialSupplierController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EMaterialSupplierDTO> result = service.getList(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询供应商
     */
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('equipment:materialSupplier:query')")
    public Map<String, Object> getById(@RequestParam("id") Long id) {
        final String methodName = "EMaterialSupplierController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        EMaterialSupplierDTO result = service.getById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增供应商
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('equipment:materialSupplier:add')")
    public Map<String, Object> add(@RequestBody EMaterialSupplierDTO dto) {
        final String methodName = "EMaterialSupplierController:add";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 修改供应商
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('equipment:materialSupplier:update')")
    public Map<String, Object> update(@RequestBody EMaterialSupplierDTO dto) {
        final String methodName = "EMaterialSupplierController:update";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 删除供应商
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('equipment:materialSupplier:delete')")
    public Map<String, Object> delete(@PathVariable("id") Long id) {
        final String methodName = "EMaterialSupplierController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        service.deleteById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }
}
