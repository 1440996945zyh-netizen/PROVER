package com.yy.ppm.dispatch.controller;

import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.dispatch.service.TBusTrustLocationService;
import com.yy.ppm.dispatch.bean.dto.TBusTrustLocationDTO;
import com.yy.ppm.dispatch.bean.dto.TBusTrustLocationSearchDTO;

import com.yy.ppm.produce.bean.dto.TPrdWorkPlanDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

/**
 * @ClassName 集疏港作业通知单位置表，传输渤海通使用(TBusTrustLocation)Controller
 * @author makejava
 * @version 1.0.0
 * @Description
 * @createTime 2023年09月27日 14:34:00
 */
@RestController
@RequestMapping("/api/v1/internal/tBusTrustLocation")
public class TBusTrustLocationController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(TBusTrustLocationController.class);

    @Autowired
    private TBusTrustLocationService tBusTrustLocationService;

    /**
     * 场地安排
     * @param dto
     * @return
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('produce:warehousePlan:arrange')")
    public Map<String, Object> updateMass(@RequestBody TBusTrustLocationDTO dto) {
//        final String methodName = "HouseYardDispatchController:updateMass";
//        LOGGER.enter(methodName + "[start]", "TBusTrustLocationDTO:" +  dto);

        boolean flag = tBusTrustLocationService.update(dto);

//        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "场地安排成功" : "场地安排失败").toResult();
    }

    /**
     * 查询指令下所有的可用状态
     * @param dto
     * @return
     */
    @GetMapping("/getMassIdsWithTrustId")
    public Map<String, Object> getMassIdsWithTrustId(TBusTrustLocationDTO dto) {
//        final String methodName = "HouseYardDispatchController:updateMass";

        List<Map<String, Object>> result = tBusTrustLocationService.getMassIdsWithTrustId(dto);

        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 查询单条记录
     * @param trustId
     * @return
     */
    @GetMapping("/getDetail")
    public Map<String, Object> getDetail(Long trustId) {
//        final String methodName = "TBusTrustLocationController:getDetail";
//		LOGGER.enter(methodName + "[start]", "trustId:" + trustId);
    
        TBusTrustLocationDTO result = tBusTrustLocationService.getDetail(trustId);
        
//        LOGGER.exit( methodName + "result:" + result);
        
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
	

}

