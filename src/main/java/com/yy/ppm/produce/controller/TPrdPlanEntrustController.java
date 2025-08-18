package com.yy.ppm.produce.controller;

import com.yy.common.enums.Response;
import com.yy.common.page.Pages;
import com.yy.ppm.produce.bean.dto.TPrdPlanEntrustResultDTO;
import com.yy.ppm.produce.bean.dto.TPrdPlanEntrustSearchDTO;
import com.yy.ppm.produce.bean.dto.TPrdVehicleReservationDTO;
import com.yy.ppm.produce.bean.dto.TPrdVehicleReservationSearchDTO;
import com.yy.ppm.produce.service.TPrdPlanEntrustService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/interface/tPrdPlanEntrust")
public class TPrdPlanEntrustController {

    @Resource
    private TPrdPlanEntrustService tBusPlanEntrustService;

    /**
     * 获取列表
     */
    @GetMapping("/getList")
    public Map<String, Object> list(TPrdPlanEntrustSearchDTO searchDTO) {
        Pages<TPrdPlanEntrustResultDTO> pages = tBusPlanEntrustService.getPage(searchDTO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 获取车辆预约列表
     */
    @GetMapping("/getVehicleList")
    public Map<String, Object> list(TPrdVehicleReservationSearchDTO searchDTO) {
        Pages<TPrdVehicleReservationDTO> pages = tBusPlanEntrustService.getVehicleList(searchDTO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }
}
