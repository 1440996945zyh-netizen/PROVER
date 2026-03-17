package com.yy.ppm.equipment.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EquipmentIndicatorDTO;
import com.yy.ppm.equipment.bean.dto.EquipmentIndicatorSearchDTO;
import com.yy.ppm.equipment.bean.dto.EquipmentMaintenanceDTO;
import com.yy.ppm.equipment.bean.dto.EquipmentMaintenanceSearchDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentInfoDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentInfoSearchDTO;
import com.yy.ppm.equipment.service.EquipmentIndicatorService;
import com.yy.ppm.equipment.service.EquipmentMaintenanceService;
import com.yy.ppm.equipment.service.MEquipmentInfoService;
import com.yy.ppm.equipment.bean.dto.EquipmentSpareDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 设备台账信息Controller
 * @author system
 */
@Validated
@RestController
@RequestMapping("/api/v1/internal/equipmentInfo")
public class MEquipmentInfoController {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(MEquipmentInfoController.class);

    @Autowired
    private MEquipmentInfoService service;

    @Autowired
    private EquipmentIndicatorService indicatorService;

    @Autowired
    private EquipmentMaintenanceService maintenanceService;

    /**
     * 查询设备台账信息列表（分页）
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('equipment:equipmentInfo:query')")
    public Map<String, Object> getList(MEquipmentInfoSearchDTO searchDTO) {
        final String methodName = "MEquipmentInfoController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<MEquipmentInfoDTO> result = service.getList(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询设备台账信息
     */
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('equipment:equipmentInfo:query')")
    public Map<String, Object> getById(@RequestParam("id") Long id) {
        final String methodName = "MEquipmentInfoController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        MEquipmentInfoDTO result = service.getById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增设备台账信息
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('equipment:equipmentInfo:add')")
    public Map<String, Object> add(@RequestBody MEquipmentInfoDTO dto) {
        final String methodName = "MEquipmentInfoController:add";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 修改设备台账信息
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('equipment:equipmentInfo:update')")
    public Map<String, Object> update(@RequestBody MEquipmentInfoDTO dto) {
        final String methodName = "MEquipmentInfoController:update";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 删除设备台账信息
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('equipment:equipmentInfo:delete')")
    public Map<String, Object> delete(@PathVariable("id") Long id) {
        final String methodName = "MEquipmentInfoController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        service.deleteById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 修改设备基本信息
     */
    @PutMapping("/updateBasicInfo")
    @PreAuthorize("hasAuthority('equipment:equipmentInfo:update')")
    public Map<String, Object> updateBasicInfo(@RequestBody MEquipmentInfoDTO dto) {
        final String methodName = "MEquipmentInfoController:updateBasicInfo";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.updateBasicInfo(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 修改财务/供货信息
     */
    @PutMapping("/updateFinanceSupply")
    @PreAuthorize("hasAuthority('equipment:equipmentInfo:update')")
    public Map<String, Object> updateFinanceSupply(@RequestBody MEquipmentInfoDTO dto) {
        final String methodName = "MEquipmentInfoController:updateFinanceSupply";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.updateFinanceSupply(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 修改特种设备信息
     */
    @PutMapping("/updateSpecialInfo")
    @PreAuthorize("hasAuthority('equipment:equipmentInfo:update')")
    public Map<String, Object> updateSpecialInfo(@RequestBody MEquipmentInfoDTO dto) {
        final String methodName = "MEquipmentInfoController:updateSpecialInfo";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.updateSpecialInfo(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 保存设备照片
     */
    @PutMapping("/updateEquipmentImages")
    @PreAuthorize("hasAuthority('equipment:equipmentInfo:update')")
    public Map<String, Object> updateEquipmentImages(@RequestBody MEquipmentInfoDTO dto) {
        final String methodName = "MEquipmentInfoController:updateEquipmentImages";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.updateEquipmentImages(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }

    /**
     * 查询设备选择列表（用于下拉框）
     */
    @GetMapping("/getSelectList")
    @PreAuthorize("hasAuthority('equipment:equipmentInfo:query')")
    public Map<String, Object> getSelectList(@RequestParam(value = "keyword", required = false) String keyword) {
        final String methodName = "MEquipmentInfoController:getSelectList";
        LOGGER.enter(methodName + "[start]", "keyword:" + keyword);

        List<com.yy.ppm.equipment.bean.dto.EquipmentSelectDTO> result = service.getEquipmentSelectList(keyword);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询设备指标统计（按月份）
     */
    @GetMapping("/getIndicatorByMonth")
    @PreAuthorize("hasAuthority('equipment:equipmentInfo:query')")
    public Map<String, Object> getIndicatorByMonth(EquipmentIndicatorSearchDTO searchDTO) {
        final String methodName = "MEquipmentInfoController:getIndicatorByMonth";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        List<EquipmentIndicatorDTO> result = indicatorService.getIndicatorByMonth(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询设备检修历史统计（按月份）
     */
    @GetMapping("/getMaintenanceByMonth")
    @PreAuthorize("hasAuthority('equipment:equipmentInfo:query')")
    public Map<String, Object> getMaintenanceByMonth(EquipmentMaintenanceSearchDTO searchDTO) {
        final String methodName = "MEquipmentInfoController:getMaintenanceByMonth";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        List<EquipmentMaintenanceDTO> result = maintenanceService.getMaintenanceByMonth(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据设备ID查询备品备件列表
     */
    @GetMapping("/getSpareList")
    @PreAuthorize("hasAuthority('equipment:equipmentInfo:query')")
    public Map<String, Object> getSpareList(
            @RequestParam("equipId") String equipId,
            @RequestParam(value = "materialName", required = false) String materialName,
            @RequestParam(value = "warehouseName", required = false) String warehouseName
    ) {
        final String methodName = "MEquipmentInfoController:getSpareList";
        LOGGER.enter(methodName + "[start]", "equipId:" + equipId + ", materialName:" + materialName + ", warehouseName:" + warehouseName);

        List<EquipmentSpareDTO> result = service.getSpareList(equipId, materialName, warehouseName);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
}

