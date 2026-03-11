package com.yy.ppm.equipment.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.*;
import com.yy.ppm.equipment.service.EMaintInfoService;
import com.yy.ppm.equipment.service.MEquipmentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 设备维修派工信息 Controller
 *
 * @author system
 */
@Validated
@RestController
@RequestMapping("/api/v1/internal/maintInfo")
public class EMaintInfoController {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(EMaintInfoController.class);

    @Autowired
    private EMaintInfoService service;

    @Autowired
    private MEquipmentTypeService equipmentTypeService;

    /**
     * 查询设备维修信息列表（分页）
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('equipment:maintInfo:query')")
    public Map<String, Object> getList(EMaintInfoSearchDTO searchDTO) {
        final String methodName = "EMaintInfoController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EMaintInfoDTO> result = service.getList(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询设备维修提报信息列表（分页）
     */
    @GetMapping("/listReport")
    @PreAuthorize("hasAuthority('equipment:maintInfo:query')")
    public Map<String, Object> listReport(EMaintInfoSearchDTO searchDTO) {
        final String methodName = "EMaintInfoController:listReport";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EMaintInfoDTO> result = service.listReport(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询设备维修派工信息列表（分页）
     */
    @GetMapping("/listWork")
    @PreAuthorize("hasAuthority('equipment:maintInfo:query')")
    public Map<String, Object> listWork(EMaintInfoSearchDTO searchDTO) {
        final String methodName = "EMaintInfoController:listWork";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<EMaintInfoDTO> result = service.listWork(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据主键查询设备维修派工信息
     */
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('equipment:maintInfo:query')")
    public Map<String, Object> getById(@RequestParam("id") Long id) {
        final String methodName = "EMaintInfoController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        EMaintInfoDTO result = service.getById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增设备维修派工信息
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('equipment:maintInfo:add')")
    public Map<String, Object> add(@RequestBody EMaintInfoDTO dto) {
        final String methodName = "EMaintInfoController:add";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 修改设备维修派工信息
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('equipment:maintInfo:update')")
    public Map<String, Object> update(@RequestBody EMaintInfoDTO dto) {
        final String methodName = "EMaintInfoController:update";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 删除设备维修派工信息
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('equipment:maintInfo:delete')")
    public Map<String, Object> delete(@PathVariable("id") Long id) {
        final String methodName = "EMaintInfoController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        service.deleteById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 批量删除设备维修派工信息
     */
    @DeleteMapping("/deleteBatch")
    @PreAuthorize("hasAuthority('equipment:maintInfo:delete')")
    public Map<String, Object> deleteBatch(@RequestBody List<Long> ids) {
        final String methodName = "EMaintInfoController:deleteBatch";
        LOGGER.enter(methodName + "[start]", "ids:" + ids);

        service.deleteByIds(ids);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("批量删除成功").toResult();
    }

    /**
     * 更新派工信息，仅更新派工相关字段
     */
    @PutMapping("/updateDispatch")
    @PreAuthorize("hasAuthority('equipment:maintInfo:dispatch')")
    public Map<String, Object> updateDispatch(@RequestBody EMaintInfoDTO dto) {
        final String methodName = "EMaintInfoController:updateDispatch";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.updateDispatch(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("派工成功").toResult();
    }

    /**
     * 批量作废工单
     */
    @PutMapping("/cancel")
    @PreAuthorize("hasAuthority('equipment:maintInfo:cancel')")
    public Map<String, Object> cancelWorkOrder(@RequestBody EMaintInfoCancelDTO cancelDTO) {
        final String methodName = "EMaintInfoController:cancelWorkOrder";
        LOGGER.enter(methodName + "[start]", "cancelDTO:" + cancelDTO);

        service.cancelWorkOrder(cancelDTO.getIds(), cancelDTO.getCancelRemark());

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("作废成功").toResult();
    }

    /**
     * 开始维修
     */
    @PutMapping("/startMaintenance")
    @PreAuthorize("hasAuthority('equipment:maintInfo:startMaint')")
    public Map<String, Object> startMaintenance(@RequestBody EMaintInfoDTO dto) {
        final String methodName = "EMaintInfoController:startMaintenance";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.startMaintenance(dto.getId(), dto.getMaintStartTime());

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("开始维修成功").toResult();
    }

    /**
     * 结束维修
     */
    @PutMapping("/endMaintenance")
    @PreAuthorize("hasAuthority('equipment:maintInfo:endMaint')")
    public Map<String, Object> endMaintenance(@RequestBody EMaintInfoDTO dto) {
        final String methodName = "EMaintInfoController:endMaintenance";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.endMaintenance(dto.getId(), dto.getMaintEndTime(), dto.getFaultImageIds(), dto.getMaintRemark(),
                dto.getPartReplaceList(), dto.getHourFeedbackList());

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("结束维修成功").toResult();
    }

    /**
     * 根据设备ID查询可用的出库单和申领单明细
     */
    @GetMapping("/getAvailableDetailsByEquipId")
    @PreAuthorize("hasAuthority('equipment:maintInfo:query')")
    public Map<String, Object> getAvailableDetailsByEquipId(@RequestParam("equipId") Long equipId) {
        final String methodName = "EMaintInfoController:getAvailableDetailsByEquipId";
        LOGGER.enter(methodName + "[start]", "equipId:" + equipId);

        List<EMaintPartReplaceQueryDTO> result = service.getAvailableDetailsByEquipId(equipId);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据承修单位ID查询维修人员下拉列表
     */
    @GetMapping("/getRepairUserListByMaintOrgId")
    public Map<String, Object> getRepairUserListByMaintOrgId(@RequestParam("maintOrgId") Long maintOrgId) {
        final String methodName = "EMaintInfoController:getRepairUserListByMaintOrgId";
        LOGGER.enter(methodName + "[start]", "maintOrgId:" + maintOrgId);

        List<EMaintRepairUserOptionDTO> result = service.getRepairUserListByMaintOrgId(maintOrgId);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据维修信息ID查询配件更换列表
     */
    @GetMapping("/getPartReplaceListByMaintInfoId")
    @PreAuthorize("hasAuthority('equipment:maintInfo:query')")
    public Map<String, Object> getPartReplaceListByMaintInfoId(@RequestParam("maintInfoId") Long maintInfoId) {
        final String methodName = "EMaintInfoController:getPartReplaceListByMaintInfoId";
        LOGGER.enter(methodName + "[start]", "maintInfoId:" + maintInfoId);

        List<EMaintPartReplaceDTO> result = service.getPartReplaceListByMaintInfoId(maintInfoId);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 验收处理
     */
    @PutMapping("/acceptMaintenance")
    @PreAuthorize("hasAuthority('equipment:maintInfo:accept')")
    public Map<String, Object> acceptMaintenance(@RequestBody EMaintInfoDTO dto) {
        final String methodName = "EMaintInfoController:acceptMaintenance";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.acceptMaintenance(dto.getId(), dto.getIsAccepted(), dto.getReturnStatus(), dto.getStatus(), dto.getAcceptanceRemark());

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("验收成功").toResult();
    }

    /**
     * 根据设备小类ID查询部位部件树
     */
    @GetMapping("/getPartsTreeBySmallCategoryId")
    @PreAuthorize("hasAuthority('equipment:maintInfo:query')")
    public Map<String, Object> getPartsTreeBySmallCategoryId(@RequestParam("smallCategoryId") Long smallCategoryId) {
        final String methodName = "EMaintInfoController:getPartsTreeBySmallCategoryId";
        LOGGER.enter(methodName + "[start]", "smallCategoryId:" + smallCategoryId);

        List<MEquipmentTypeDTO> allTree = equipmentTypeService.partsTree(new MEquipmentTypeDTO());
        List<MEquipmentTypeDTO> result = allTree.stream()
                .filter(item -> smallCategoryId.equals(item.getId()))
                .findFirst()
                .map(MEquipmentTypeDTO::getChildren)
                .orElseGet(java.util.ArrayList::new);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据申请单号查询维修项目申请表获取维修单位
     */
    @GetMapping("/getMaintProjApplyByAppNumber")
    public Map<String, Object> getMaintProjApplyByAppNumber(@RequestParam("appNumber") String appNumber) {
        final String methodName = "EMaintInfoController:getMaintProjApplyByAppNumber";
        LOGGER.enter(methodName + "[start]", "appNumber:" + appNumber);

        EMaintProjApplyDTO result = service.getMaintProjApplyByAppNumber(appNumber);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
}
