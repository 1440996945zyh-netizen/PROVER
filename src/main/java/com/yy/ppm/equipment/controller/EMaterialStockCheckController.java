package com.yy.ppm.equipment.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.*;
import com.yy.ppm.equipment.service.EMaterialStockCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 物资库存盘点Controller
 * @author system
 */
@Validated
@RestController
@RequestMapping("/api/v1/internal/materialStockCheck")
public class EMaterialStockCheckController {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(EMaterialStockCheckController.class);

    @Autowired
    private EMaterialStockCheckService service;

    /**
     * 创建盘点单（整个仓库）
     */
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('equipment:materialStockCheck:create')")
    public Map<String, Object> createCheck(
            @RequestParam("warehouseId") Long warehouseId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkDate,
            @RequestParam(value = "remark", required = false) String remark) {
        final String methodName = "EMaterialStockCheckController:createCheck";
        LOGGER.enter(methodName + "[start]", "warehouseId:" + warehouseId + ", checkDate:" + checkDate);

        EMaterialStockCheckDTO result = service.createCheck(warehouseId, checkDate, remark);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("创建成功").toResult(result);
    }

    /**
     * 创建盘点单（单个物资）
     */
    @PostMapping("/createForMaterial")
    @PreAuthorize("hasAuthority('equipment:materialStockCheck:create')")
    public Map<String, Object> createCheckForMaterial(
            @RequestParam("warehouseId") Long warehouseId,
            @RequestParam("materialId") Long materialId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkDate,
            @RequestParam(value = "remark", required = false) String remark) {
        final String methodName = "EMaterialStockCheckController:createCheckForMaterial";
        LOGGER.enter(methodName + "[start]", "warehouseId:" + warehouseId + ", materialId:" + materialId + ", checkDate:" + checkDate);

        EMaterialStockCheckDTO result = service.createCheckForMaterial(warehouseId, materialId, checkDate, remark, null);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("创建成功").toResult(result);
    }

    /**
     * 查询盘点单列表（分页）
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('equipment:materialStockCheck:query')")
    public Map<String, Object> getCheckList(EMaterialStockCheckSearchDTO searchDTO) {
        final String methodName = "EMaterialStockCheckController:getCheckList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EMaterialStockCheckDTO> result = service.getCheckList(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询盘点单（包含明细）
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('equipment:materialStockCheck:query')")
    public Map<String, Object> getCheckById(@PathVariable("id") Long id) {
        final String methodName = "EMaterialStockCheckController:getCheckById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        EMaterialStockCheckDTO result = service.getCheckById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询盘点明细列表（根据盘点单ID）
     */
    @GetMapping("/detail/{checkId}")
    @PreAuthorize("hasAuthority('equipment:materialStockCheck:query')")
    public Map<String, Object> getCheckDetailList(@PathVariable("checkId") Long checkId) {
        final String methodName = "EMaterialStockCheckController:getCheckDetailList";
        LOGGER.enter(methodName + "[start]", "checkId:" + checkId);

        List<EMaterialStockCheckDetailDTO> result = service.getCheckDetailList(checkId);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 更新盘点数量（单条明细）
     */
    @PutMapping("/updateQuantity")
    @PreAuthorize("hasAuthority('equipment:materialStockCheck:edit')")
    public Map<String, Object> updateCheckQuantity(@RequestBody EMaterialStockCheckUpdateDTO updateDTO) {
        final String methodName = "EMaterialStockCheckController:updateCheckQuantity";
        LOGGER.enter(methodName + "[start]", "checkId:" + updateDTO.getCheckId());

        service.updateCheckQuantity(updateDTO.getCheckId(), updateDTO.getDetailList());

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("更新成功").toResult();
    }

    /**
     * 完成盘点
     */
    @PostMapping("/complete")
    @PreAuthorize("hasAuthority('equipment:materialStockCheck:complete')")
    public Map<String, Object> completeCheck(@RequestParam("checkId") Long checkId) {
        final String methodName = "EMaterialStockCheckController:completeCheck";
        LOGGER.enter(methodName + "[start]", "checkId:" + checkId);

        service.completeCheck(checkId);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("完成盘点成功").toResult();
    }

    /**
     * 盘点调整
     */
    @PostMapping("/adjust")
    @PreAuthorize("hasAuthority('equipment:materialStockCheck:adjust')")
    public Map<String, Object> adjustCheck(
            @RequestParam("checkId") Long checkId,
            @RequestParam(value = "remark", required = false) String remark) {
        final String methodName = "EMaterialStockCheckController:adjustCheck";
        LOGGER.enter(methodName + "[start]", "checkId:" + checkId + ", remark:" + remark);

        service.adjustCheck(checkId, remark);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("盘点调整成功").toResult();
    }

    /**
     * 删除盘点单
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('equipment:materialStockCheck:delete')")
    public Map<String, Object> deleteCheck(@PathVariable("id") Long id) {
        final String methodName = "EMaterialStockCheckController:deleteCheck";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        service.deleteCheck(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 快速盘点（创建盘点单、保存盘点数量、完成盘点并生成出入库单）
     */
    @PostMapping("/quickCheck")
    @PreAuthorize("hasAuthority('equipment:materialStockCheck:create')")
    public Map<String, Object> quickCheck(
            @RequestParam("warehouseId") Long warehouseId,
            @RequestParam("materialId") Long materialId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkDate,
            @RequestParam("checkQuantity") java.math.BigDecimal checkQuantity,
            @RequestParam(value = "remark", required = false) String remark) {
        final String methodName = "EMaterialStockCheckController:quickCheck";
        LOGGER.enter(methodName + "[start]", "warehouseId:" + warehouseId + ", materialId:" + materialId + ", checkDate:" + checkDate + ", checkQuantity:" + checkQuantity);
        service.quickCheck(warehouseId, materialId, checkDate, checkQuantity, remark);
        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("盘点成功").toResult();
    }

    /**
     * 根据仓库创建盘点单（查询该仓库所有入库明细，不合并）
     */
    @PostMapping("/createByWarehouse")
    @PreAuthorize("hasAuthority('equipment:materialStockCheck:create')")
    public Map<String, Object> createCheckByWarehouse(
            @RequestParam("warehouseId") Long warehouseId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkStartDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkEndDate,
            @RequestParam(value = "checkTitle", required = false) String checkTitle,
            @RequestParam(value = "remark", required = false) String remark) {
        final String methodName = "EMaterialStockCheckController:createCheckByWarehouse";
        LOGGER.enter(methodName + "[start]", "warehouseId:" + warehouseId + ", checkStartDate:" + checkStartDate + ", checkEndDate:" + checkEndDate);
        EMaterialStockCheckDTO result = service.createCheckByWarehouse(warehouseId, checkStartDate, checkEndDate, checkTitle, remark);
        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("创建成功").toResult(result);
    }

    /**
     * 保存盘点单（新增或修改）
     */
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('equipment:materialStockCheck:add') or hasAuthority('equipment:materialStockCheck:update')")
    public Map<String, Object> saveCheck(@RequestBody EMaterialStockCheckDTO dto) {
        final String methodName = "EMaterialStockCheckController:saveCheck";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);
        service.saveCheck(dto);
        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }

    /**
     * 根据仓库ID查询所有入库明细（用于盘点，不合并）
     */
    @GetMapping("/inDetails/{warehouseId}")
    @PreAuthorize("hasAuthority('equipment:materialStockCheck:query')")
    public Map<String, Object> getInDetailsByWarehouseId(@PathVariable("warehouseId") Long warehouseId) {
        final String methodName = "EMaterialStockCheckController:getInDetailsByWarehouseId";
        LOGGER.enter(methodName + "[start]", "warehouseId:" + warehouseId);
        List<EMaterialWarehouseInDetailDTO> result = service.getInDetailsByWarehouseId(warehouseId);
        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询盘点明细列表（包含入库明细和入库主表信息）- 分页
     */
    @GetMapping("/detailWithInInfo")
    @PreAuthorize("hasAuthority('equipment:materialStockCheck:query')")
    public Map<String, Object> getCheckDetailListWithInInfo(EMaterialStockCheckDetailSearchDTO searchDTO) {
        final String methodName = "EMaterialStockCheckController:getCheckDetailListWithInInfo";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);
        Pages<EMaterialStockCheckDetailWithInDTO> result = service.getCheckDetailListWithInInfo(searchDTO);
        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 撤销盘点明细
     */
    @PutMapping("/cancelDetail")
    @PreAuthorize("hasAuthority('equipment:materialStockCheck:edit')")
    public Map<String, Object> cancelCheckDetail(@RequestBody Map<String, Object> params) {
        final String methodName = "EMaterialStockCheckController:cancelCheckDetail";
        Long checkId = params.get("checkId") != null ? Long.valueOf(params.get("checkId").toString()) : null;
        Long detailId = params.get("detailId") != null ? Long.valueOf(params.get("detailId").toString()) : null;
        LOGGER.enter(methodName + "[start]", "checkId:" + checkId + ", detailId:" + detailId);

        service.cancelCheckDetail(checkId, detailId);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("撤销成功").toResult();
    }

    /**
     * 审核盘点明细（单个明细审核）
     */
    @PutMapping("/audit")
    @PreAuthorize("hasAuthority('equipment:materialStockCheck:audit')")
    public Map<String, Object> auditCheckDetail(@RequestBody Map<String, Object> params) {
        final String methodName = "EMaterialStockCheckController:auditCheckDetail";
        Long checkId = params.get("checkId") != null ? Long.valueOf(params.get("checkId").toString()) : null;
        Long detailId = params.get("detailId") != null ? Long.valueOf(params.get("detailId").toString()) : null;
        LOGGER.enter(methodName + "[start]", "checkId:" + checkId + ", detailId:" + detailId);

        service.auditCheckDetail(checkId, detailId);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("审核成功").toResult();
    }

    /**
     * 无差异操作（批量）
     */
    @PutMapping("/noDifference")
    @PreAuthorize("hasAuthority('equipment:materialStockCheck:edit')")
    public Map<String, Object> noDifference(@RequestBody EMaterialStockCheckUpdateDTO updateDTO) {
        final String methodName = "EMaterialStockCheckController:noDifference";
        LOGGER.enter(methodName + "[start]", "checkId:" + updateDTO.getCheckId());

        service.noDifference(updateDTO.getCheckId(), updateDTO.getDetailList());

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("操作成功").toResult();
    }
}

