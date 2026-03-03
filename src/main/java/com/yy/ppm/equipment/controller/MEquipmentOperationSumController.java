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
import com.yy.ppm.equipment.service.MEquipmentOperationSumService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 设备运行管理Controller
 * @author system
 */
@RestController
@RequestMapping("/api/internal/MEquipmentOperationSum")
public class MEquipmentOperationSumController {
    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(MEquipmentOperationSumController.class);

    @Resource
    private MEquipmentOperationSumService mEquipmentOperationSumService;

    /**
     * 查询点检记录（分页）
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(MEquipmentOperationDTO searchDTO, PageParameter parameter) {
        final String methodName = "MEquipmentOperationController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<MEquipmentOperationDTO> result = mEquipmentOperationSumService.getList(searchDTO,parameter);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }


}
