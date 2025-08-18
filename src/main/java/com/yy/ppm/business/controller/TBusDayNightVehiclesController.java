package com.yy.ppm.business.controller;

import com.yy.common.log.MicroLogger;
import com.yy.ppm.business.service.TBusDayNightVehiclesService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

/**
 * @ClassName 集疏港昼夜计划派车(TBusDayNightVehicles)Controller
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2024年1月25日 8:31:00
 */
@RestController
@RequestMapping("/api/v1/internal/TBusDayNightVehicles")
public class TBusDayNightVehiclesController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TBusDayNightVehiclesController.class);

    @Resource
    private TBusDayNightVehiclesService tBusDayNightVehiclesService;

}
