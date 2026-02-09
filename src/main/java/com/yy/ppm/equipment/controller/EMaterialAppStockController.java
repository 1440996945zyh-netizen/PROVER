package com.yy.ppm.equipment.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMaterialStockDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialStockDetailDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialStockFlowDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialStockSearchDTO;
import com.yy.ppm.equipment.service.EMaterialAppStockService;
import com.yy.ppm.equipment.service.EMaterialStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 物资库存Controller
 * @author system
 */
@Validated
@RestController
@RequestMapping("/api/v1/internal/materialStockApp")
public class EMaterialAppStockController {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(EMaterialAppStockController.class);

    @Autowired
    private EMaterialAppStockService service;

    /**
     * 查询物资库存列表（分页）
     */
    @GetMapping("/listApp")
    public Map<String, Object> getList(EMaterialStockSearchDTO searchDTO) {
        final String methodName = "EMaterialStockController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        List<EMaterialStockDTO> result = service.getList(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询物资库存明细列表（根据仓库ID和物资ID）
     */
    @GetMapping("/detailList")
    public Map<String, Object> getStockDetailList(
            @RequestParam("warehouseId") Long warehouseId,
            @RequestParam("materialId") Long materialId,
            @RequestParam(value = "warehouseInTimeStart", required = false) String warehouseInTimeStart,
            @RequestParam(value = "warehouseInTimeEnd", required = false) String warehouseInTimeEnd) {
        final String methodName = "EMaterialStockController:getStockDetailList";
        LOGGER.enter(methodName + "[start]", "warehouseId:" + warehouseId + ", materialId:" + materialId + ", warehouseInTimeStart:" + warehouseInTimeStart + ", warehouseInTimeEnd:" + warehouseInTimeEnd);

        List<EMaterialStockDetailDTO> result = service.getStockDetailList(warehouseId, materialId, warehouseInTimeStart, warehouseInTimeEnd);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询物资库存流水列表（根据仓库ID和物资ID）
     */
    @GetMapping("/flowList")
    public Map<String, Object> getStockFlowList(
            @RequestParam("warehouseId") Long warehouseId,
            @RequestParam("materialId") Long materialId,
            @RequestParam(value = "warehouseInTimeStart", required = false) String warehouseInTimeStart,
            @RequestParam(value = "warehouseInTimeEnd", required = false) String warehouseInTimeEnd) {
        final String methodName = "EMaterialStockController:getStockFlowList";
        LOGGER.enter(methodName + "[start]", "warehouseId:" + warehouseId + ", materialId:" + materialId + ", warehouseInTimeStart:" + warehouseInTimeStart + ", warehouseInTimeEnd:" + warehouseInTimeEnd);

        List<EMaterialStockFlowDTO> result = service.getStockFlowList(warehouseId, materialId, warehouseInTimeStart, warehouseInTimeEnd);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
}

