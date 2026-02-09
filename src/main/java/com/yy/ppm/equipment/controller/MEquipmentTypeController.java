package com.yy.ppm.equipment.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.ppm.equipment.bean.dto.EquipmentTypePathDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentTypeDTO;
import com.yy.ppm.equipment.service.MEquipmentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 设备类型分类Controller
 * @author system
 */
@Validated
@RestController
@RequestMapping("/api/v1/internal/equipmentType")
public class MEquipmentTypeController {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(MEquipmentTypeController.class);

    @Autowired
    private MEquipmentTypeService service;

    /**
     * 查询设备类型分类树形列表
     */
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('equipment:equipmentType:query')")
    public Map<String, Object> getTree(@RequestParam(value = "typeName", required = false) String typeName) {
        final String methodName = "MEquipmentTypeController:getTree";
        LOGGER.enter(methodName + "[start]", "typeName:" + typeName);

        List<MEquipmentTypeDTO> result = service.getTreeList(typeName);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    /**
     * 查询设备类型分类树形列表
     */
    @GetMapping("/partsTree")
    @PreAuthorize("hasAuthority('equipment:equipmentType:query')")
    public Map<String, Object> partsTree(MEquipmentTypeDTO mEquipmentTypeDTO) {
        final String methodName = "MEquipmentTypeController:getTree";
        LOGGER.enter(methodName + "[start]");

        List<MEquipmentTypeDTO> result = service.partsTree(mEquipmentTypeDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询设备类型分类
     */
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('equipment:equipmentType:query')")
    public Map<String, Object> getById(@RequestParam("id") Long id) {
        final String methodName = "MEquipmentTypeController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        MEquipmentTypeDTO result = service.getById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增设备类型分类
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('equipment:equipmentType:add')")
    public Map<String, Object> add(@RequestBody MEquipmentTypeDTO dto) {
        final String methodName = "MEquipmentTypeController:add";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 新增设备类型分类
     */
    @PostMapping("/addParts")
    @PreAuthorize("hasAuthority('equipment:equipmentType:add')")
    public Map<String, Object> addParts(@RequestBody MEquipmentTypeDTO dto) {
        final String methodName = "MEquipmentTypeController:add";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.addParts(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 修改设备类型分类
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('equipment:equipmentType:update')")
    public Map<String, Object> update(@RequestBody MEquipmentTypeDTO dto) {
        final String methodName = "MEquipmentTypeController:update";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 修改设备类型分类
     */
    @PutMapping("/updateParts")
    @PreAuthorize("hasAuthority('equipment:equipmentType:update')")
    public Map<String, Object> updateParts(@RequestBody MEquipmentTypeDTO dto) {
        final String methodName = "MEquipmentTypeController:update";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.addParts(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 删除设备类型分类
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('equipment:equipmentType:delete')")
    public Map<String, Object> delete(@PathVariable("id") Long id) {
        final String methodName = "MEquipmentTypeController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        service.deleteById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 根据父级ID查询子级列表（用于树形表格懒加载）
     */
    @GetMapping("/getByParentId/{parentId}")
    @PreAuthorize("hasAuthority('equipment:equipmentType:query')")
    public Map<String, Object> getByParentId(@PathVariable("parentId") Long parentId) {
        final String methodName = "MEquipmentTypeController:getByParentId";
        LOGGER.enter(methodName + "[start]", "parentId:" + parentId);

        List<MEquipmentTypeDTO> result = service.getByParentId(parentId);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 获取树形选择器数据（用于下拉选择父级）
     */
    @GetMapping("/getSelectTree")
    @PreAuthorize("hasAuthority('equipment:equipmentType:query')")
    public Map<String, Object> getSelectTree() {
        final String methodName = "MEquipmentTypeController:getSelectTree";
        LOGGER.enter(methodName + "[start]");

        List<MEquipmentTypeDTO> result = service.getTreeList(null);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据级别和父级ID查询设备类型列表
     */
    @GetMapping("/getByLevelAndParent")
    @PreAuthorize("hasAuthority('equipment:equipmentType:query')")
    public Map<String, Object> getByLevelAndParent(@RequestParam("categoryLevel") Integer categoryLevel,
                                                    @RequestParam(value = "parentId", required = false) Long parentId) {
        final String methodName = "MEquipmentTypeController:getByLevelAndParent";
        LOGGER.enter(methodName + "[start]", "categoryLevel:" + categoryLevel + ", parentId:" + parentId);

        List<MEquipmentTypeDTO> result = service.getByLevelAndParent(categoryLevel, parentId);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据小类ID获取完整路径（大类、中类、小类）
     */
    @GetMapping("/getPathBySmallCategoryId")
    @PreAuthorize("hasAuthority('equipment:equipmentType:query')")
    public Map<String, Object> getPathBySmallCategoryId(@RequestParam("smallCategoryId") Long smallCategoryId) {
        final String methodName = "MEquipmentTypeController:getPathBySmallCategoryId";
        LOGGER.enter(methodName + "[start]", "smallCategoryId:" + smallCategoryId);

        EquipmentTypePathDTO result = service.getPathBySmallCategoryId(smallCategoryId);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
}

