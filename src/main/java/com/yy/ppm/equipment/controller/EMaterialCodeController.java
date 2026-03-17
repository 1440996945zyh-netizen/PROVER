package com.yy.ppm.equipment.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMaterialCodeDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialCodeSearchDTO;
import com.yy.ppm.equipment.service.EMaterialCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 物资代码Controller
 * @author system
 */
@Validated
@RestController
@RequestMapping("/api/v1/internal/materialCode")
public class EMaterialCodeController {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(EMaterialCodeController.class);

    @Autowired
    private EMaterialCodeService service;

    /**
     * 查询物资代码列表（分页）
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('equipment:materialCode:query')")
    public Map<String, Object> getList(EMaterialCodeSearchDTO searchDTO) {
        final String methodName = "EMaterialCodeController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EMaterialCodeDTO> result = service.getList(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询物资代码
     */
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('equipment:materialCode:query')")
    public Map<String, Object> getById(@RequestParam("id") Long id) {
        final String methodName = "EMaterialCodeController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        EMaterialCodeDTO result = service.getById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增物资代码
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('equipment:materialCode:add')")
    public Map<String, Object> add(@RequestBody EMaterialCodeDTO dto) {
        final String methodName = "EMaterialCodeController:add";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 修改物资代码
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('equipment:materialCode:update')")
    public Map<String, Object> update(@RequestBody EMaterialCodeDTO dto) {
        final String methodName = "EMaterialCodeController:update";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 删除物资代码
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('equipment:materialCode:delete')")
    public Map<String, Object> delete(@PathVariable("id") Long id) {
        final String methodName = "EMaterialCodeController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        service.deleteById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 查询所有物资代码列表（无分页，用于下拉选择）
     */
    @GetMapping("/getAllList")
    @PreAuthorize("hasAuthority('equipment:materialCode:query')")
    public Map<String, Object> getAllList() {
        final String methodName = "EMaterialCodeController:getAllList";
        LOGGER.enter(methodName + "[start]");

        List<EMaterialCodeDTO> result = service.getAllList();

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
}

