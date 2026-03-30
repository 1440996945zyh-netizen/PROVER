package com.yy.ppm.equipment.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.equipment.bean.dto.MEpatrolStandardDTO;
import com.yy.ppm.equipment.service.MEpatrolStandardService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 巡检标准
 */
@RestController
@RequestMapping("/api/internal/patrolStandard")
public class MEpatrolStandardController {

    private static final MicroLogger LOGGER = new MicroLogger(MEpatrolStandardController.class);

    @Resource
    private MEpatrolStandardService patrolStandardService;

    /** 列表 */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('equipment:patrolStandard:query')")
    public Map<String, Object> getList(MEpatrolStandardDTO searchDTO, PageParameter parameter) {
        final String methodName = "PatrolStandardController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO + ", parameter:" + parameter);

        Pages<MEpatrolStandardDTO> result = patrolStandardService.getList(searchDTO, parameter);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /** 详情 */
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('equipment:patrolStandard:getById')")
    public Map<String, Object> getById(@RequestParam("id") Long id) {
        final String methodName = "PatrolStandardController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        MEpatrolStandardDTO result = patrolStandardService.getById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /** 新增 */
    @PostMapping("/add")
    @Log(value = OperateTypeEnum.INSERT, title = "新增巡检标准")
    @PreAuthorize("hasAuthority('equipment:patrolStandard:add')")
    public Map<String, Object> add(@RequestBody MEpatrolStandardDTO dto) {
        final String methodName = "PatrolStandardController:add";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        patrolStandardService.add(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /** 修改 */
    @PutMapping("/update")
    @Log(value = OperateTypeEnum.UPDATE, title = "修改巡检标准")
    @PreAuthorize("hasAuthority('equipment:patrolStandard:update')")
    public Map<String, Object> update(@RequestBody MEpatrolStandardDTO dto) {
        final String methodName = "PatrolStandardController:update";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        patrolStandardService.update(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /** 删除 */
    @DeleteMapping("/delete")
    @Log(value = OperateTypeEnum.DELETE, title = "删除巡检标准")
    @PreAuthorize("hasAuthority('equipment:patrolStandard:delete')")
    public Map<String, Object> delete(@RequestParam("id") Long id) {
        final String methodName = "PatrolStandardController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        patrolStandardService.delete(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }
}

