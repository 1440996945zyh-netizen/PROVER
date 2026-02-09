package com.yy.ppm.equipment.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.ppm.equipment.bean.dto.EMaterialCategoryDTO;
import com.yy.ppm.equipment.service.EMaterialCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 物资类别Controller
 * @author system
 */
@Validated
@RestController
@RequestMapping("/api/v1/internal/materialCategory")
public class EMaterialCategoryController {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(EMaterialCategoryController.class);

    @Autowired
    private EMaterialCategoryService service;

    /**
     * 查询物资类别树形列表
     */
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('equipment:materialCategory:query')")
    public Map<String, Object> getTree(@RequestParam(value = "categoryName", required = false) String categoryName) {
        final String methodName = "EMaterialCategoryController:getTree";
        LOGGER.enter(methodName + "[start]", "categoryName:" + categoryName);

        List<EMaterialCategoryDTO> result = service.getTreeList(categoryName);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询物资类别
     */
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('equipment:materialCategory:query')")
    public Map<String, Object> getById(@RequestParam("id") Long id) {
        final String methodName = "EMaterialCategoryController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        EMaterialCategoryDTO result = service.getById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增物资类别
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('equipment:materialCategory:add')")
    public Map<String, Object> add(@RequestBody EMaterialCategoryDTO dto) {
        final String methodName = "EMaterialCategoryController:add";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 修改物资类别
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('equipment:materialCategory:update')")
    public Map<String, Object> update(@RequestBody EMaterialCategoryDTO dto) {
        final String methodName = "EMaterialCategoryController:update";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 删除物资类别
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('equipment:materialCategory:delete')")
    public Map<String, Object> delete(@PathVariable("id") Long id) {
        final String methodName = "EMaterialCategoryController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        service.deleteById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 根据父级ID查询子级列表（用于树形表格懒加载）
     */
    @GetMapping("/getByParentId/{parentId}")
    @PreAuthorize("hasAuthority('equipment:materialCategory:query')")
    public Map<String, Object> getByParentId(@PathVariable("parentId") Long parentId) {
        final String methodName = "EMaterialCategoryController:getByParentId";
        LOGGER.enter(methodName + "[start]", "parentId:" + parentId);

        List<EMaterialCategoryDTO> result = service.getByParentId(parentId);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据级别和父级ID查询物资类别列表
     */
    @GetMapping("/getByLevelAndParent")
    @PreAuthorize("hasAuthority('equipment:materialCategory:query')")
    public Map<String, Object> getByLevelAndParent(@RequestParam("categoryLevel") Integer categoryLevel,
                                                    @RequestParam(value = "parentId", required = false) Long parentId) {
        final String methodName = "EMaterialCategoryController:getByLevelAndParent";
        LOGGER.enter(methodName + "[start]", "categoryLevel:" + categoryLevel + ", parentId:" + parentId);

        List<EMaterialCategoryDTO> result = service.getByLevelAndParent(categoryLevel, parentId);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
}

