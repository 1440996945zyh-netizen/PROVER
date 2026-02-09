package com.yy.ppm.equipment.controller;


import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.equipment.bean.dto.MEquipmentOperationDTO;
import com.yy.ppm.equipment.bean.po.MEquipmentOperationPO;
import com.yy.ppm.equipment.service.MEquipmentOperationService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 设备运行管理Controller
 * @author system
 */
@RestController
@RequestMapping("/api/internal/MEquipmentOperation")
public class MEquipmentOperationController {
    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(MEquipmentOperationController.class);

    @Resource
    private MEquipmentOperationService mEquipmentOperationService;

    /**
     * 查询点检记录（分页）
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(MEquipmentOperationDTO searchDTO, PageParameter parameter) {
        final String methodName = "MEquipmentOperationController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<MEquipmentOperationPO> result = mEquipmentOperationService.getList(searchDTO,parameter);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询设备运行信息
     */
    @GetMapping("/getById")
    public Map<String, Object> getById(MEquipmentOperationDTO searchDTO) {
        final String methodName = "MEquipmentOperationController:getById";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        MEquipmentOperationPO result = mEquipmentOperationService.getById(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增
     */
    @PostMapping("/add")
    @Log(title = "新增设备运行记录", value = OperateTypeEnum.INSERT)
    public Map<String, Object> add(@RequestBody MEquipmentOperationPO po) {
        final String methodName = "MEquipmentOperationController:add";
        LOGGER.enter(methodName + "[start]", "po:" + po);

        mEquipmentOperationService.save(po);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 新增
     */
    @PutMapping("/update")
    @Log(title = "修改设备运行记录", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> update(@RequestBody MEquipmentOperationPO po) {
        final String methodName = "MEquipmentOperationController:add";
        LOGGER.enter(methodName + "[start]", "po:" + po);

        mEquipmentOperationService.save(po);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 删除设备运行记录
     */
    @DeleteMapping("/delete")
    @Log(title = "删除设备运行记录", value = OperateTypeEnum.DELETE)
    public Map<String, Object> delete(@RequestParam("id") Long id) {
        final String methodName = "MEquipmentOperationController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        mEquipmentOperationService.delete(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }
}
