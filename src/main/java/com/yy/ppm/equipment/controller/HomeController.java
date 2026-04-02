package com.yy.ppm.equipment.controller;


import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.ppm.equipment.bean.dto.EMaterialWarningRecordDTO;
import com.yy.ppm.equipment.bean.dto.HomeDTO;
import com.yy.ppm.equipment.service.HomeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 首页Controller
 * @author system
 */
@RestController
@RequestMapping("/api/internal/home")
public class HomeController {
    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(HomeController.class);

    @Resource
    private HomeService homeService;

    /**
     * 首页获取数据
     */
    @GetMapping("/getHomeMap")
    public Map<String, Object> getHomeMap() {
        final String methodName = "MEquipmentOperationController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:");

        Map<String, Object> homeMap= homeService.getList();

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(homeMap);
    }



    /**
     * 首页获取工单趋势
     */
    @GetMapping("/getMaintInfo")
    public Map<String, Object> getMaintInfo(HomeDTO homeDTO) {
        final String methodName = "MEquipmentOperationController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:");

        List<HomeDTO>homeMap= homeService.getMainInfo(homeDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(homeMap);
    }


    /**
     * 首页获取预警信息
     */
    @GetMapping("/getWarningRecord")
    public Map<String, Object> getWarningRecord() {
        final String methodName = "MEquipmentOperationController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:");

        List<EMaterialWarningRecordDTO>list= homeService.getWarningRecord();

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }



}
