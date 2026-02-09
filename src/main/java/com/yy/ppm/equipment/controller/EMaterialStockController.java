package com.yy.ppm.equipment.controller;

import cn.hutool.core.io.IORuntimeException;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.common.excel.export.utils.ResponseUtils;
import com.yy.ppm.equipment.bean.dto.EMaterialStockDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialStockDetailDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialStockFlowDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialStockSearchDTO;
import com.yy.ppm.equipment.service.EMaterialStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 物资库存Controller
 * @author system
 */
@Validated
@RestController
@RequestMapping("/api/v1/internal/materialStock")
public class EMaterialStockController {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(EMaterialStockController.class);

    @Autowired
    private EMaterialStockService service;

    /**
     * 查询物资库存列表（分页）
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('equipment:materialStock:query')")
    public Map<String, Object> getList(EMaterialStockSearchDTO searchDTO) {
        final String methodName = "EMaterialStockController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EMaterialStockDTO> result = service.getList(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询物资库存明细列表（根据仓库ID和物资ID）
     */
    @GetMapping("/detailList")
    @PreAuthorize("hasAuthority('equipment:materialStock:query')")
    public Map<String, Object> getStockDetailList(
            @RequestParam("warehouseId") Long warehouseId,
            @RequestParam("materialId") Long materialId,
            @RequestParam(value = "warehouseInTimeStart", required = false) String warehouseInTimeStart,
            @RequestParam(value = "warehouseInTimeEnd", required = false) String warehouseInTimeEnd) {
        final String methodName = "EMaterialStockController:getStockDetailList";
        LOGGER.enter(methodName + "[start]", "warehouseId:" + warehouseId + ", materialId:" + materialId + ", warehouseInTimeStart:" + warehouseInTimeStart + ", warehouseInTimeEnd:" + warehouseInTimeEnd);

        java.util.List<EMaterialStockDetailDTO> result = service.getStockDetailList(warehouseId, materialId, warehouseInTimeStart, warehouseInTimeEnd);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询物资库存流水列表（根据仓库ID和物资ID）
     */
    @GetMapping("/flowList")
    @PreAuthorize("hasAuthority('equipment:materialStock:query')")
    public Map<String, Object> getStockFlowList(
            @RequestParam("warehouseId") Long warehouseId,
            @RequestParam("materialId") Long materialId,
            @RequestParam(value = "warehouseInTimeStart", required = false) String warehouseInTimeStart,
            @RequestParam(value = "warehouseInTimeEnd", required = false) String warehouseInTimeEnd) {
        final String methodName = "EMaterialStockController:getStockFlowList";
        LOGGER.enter(methodName + "[start]", "warehouseId:" + warehouseId + ", materialId:" + materialId + ", warehouseInTimeStart:" + warehouseInTimeStart + ", warehouseInTimeEnd:" + warehouseInTimeEnd);

        java.util.List<EMaterialStockFlowDTO> result = service.getStockFlowList(warehouseId, materialId, warehouseInTimeStart, warehouseInTimeEnd);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 导出物资库存列表
     */
    @GetMapping("/pageExport")
    @PreAuthorize("hasAuthority('equipment:materialStock:record')")
    public void pageExport(EMaterialStockSearchDTO searchDTO, HttpServletResponse response) {
        final String methodName = "EMaterialStockController:pageExport";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        try {
            byte[] bytes = service.pageExport(searchDTO);
            try {
                response.getOutputStream().write(bytes);
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        } catch (Exception e) {
            ResponseUtils.resetCompliant(response);
            throw e;
        }

        LOGGER.exit(methodName + "[end]");
    }
}

