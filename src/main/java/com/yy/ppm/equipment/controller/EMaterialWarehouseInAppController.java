package com.yy.ppm.equipment.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseInAcceptanceDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseInDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseInSearchDTO;
import com.yy.ppm.equipment.service.EMaterialWarehouseInAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 物资入库Controller
 * @author system
 */
@Validated
@RestController
@RequestMapping("/api/v1/internal/materialWarehouseInApp")
public class EMaterialWarehouseInAppController {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(EMaterialWarehouseInAppController.class);

    @Autowired
    private EMaterialWarehouseInAppService service;

    /**
     * 查询物资入库列表（分页）
     */
    @GetMapping("/list")
    public Map<String, Object> getList(EMaterialWarehouseInSearchDTO searchDTO) {
        final String methodName = "EMaterialWarehouseInController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EMaterialWarehouseInDTO> result = service.getList(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询物资入库
     */
    @GetMapping("/getById")
    public Map<String, Object> getById(@RequestParam("id") Long id) {
        final String methodName = "EMaterialWarehouseInController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        EMaterialWarehouseInDTO result = service.getById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增物资入库
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody EMaterialWarehouseInDTO dto) {
        final String methodName = "EMaterialWarehouseInController:add";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 修改物资入库
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody EMaterialWarehouseInDTO dto) {
        final String methodName = "EMaterialWarehouseInController:update";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 删除物资入库
     */
    @PostMapping("/delete")
    public Map<String, Object> delete(@RequestBody EMaterialWarehouseInDTO dto) {
        final String methodName = "EMaterialWarehouseInController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + dto.getId());
        service.deleteById(dto.getId());
        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 验收物资入库
     */
    @PostMapping("/acceptance")
    public Map<String, Object> acceptance(@RequestBody EMaterialWarehouseInAcceptanceDTO dto) {
        final String methodName = "EMaterialWarehouseInController:acceptance";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.acceptance(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("验收成功").toResult();
    }

    /**
     * 查询物资库存数量（按物资ID和仓库ID）
     */
    @GetMapping("/getStockQuantity")
    public Map<String, Object> getStockQuantity(@RequestParam("materialId") Long materialId,
                                                 @RequestParam(value = "warehouseId", required = false) Long warehouseId) {
        final String methodName = "EMaterialWarehouseInController:getStockQuantity";
        LOGGER.enter(methodName + "[start]", "materialId:" + materialId + ", warehouseId:" + warehouseId);

        java.math.BigDecimal stockQuantity = service.getStockQuantity(materialId, warehouseId);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(stockQuantity);
    }
}

