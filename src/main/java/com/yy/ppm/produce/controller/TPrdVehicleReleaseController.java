package com.yy.ppm.produce.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.produce.bean.dto.GroupQueryDTO;
import com.yy.ppm.produce.bean.dto.TPrdVehicleReleaseDTO;
import com.yy.ppm.produce.bean.po.TPrdGroupPO;
import com.yy.ppm.produce.bean.po.TPrdVehicleReleasePO;
import com.yy.ppm.produce.service.TPrdVehicleReleaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/interface/vehicleRelease")
public class TPrdVehicleReleaseController {
    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TPrdVehicleReleaseController.class);

    @Autowired
    private TPrdVehicleReleaseService tPrdVehicleReleaseService;

    /**
     * 分组列表
     *
     * @param query
     * @param parameter
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(TPrdVehicleReleaseDTO query, PageParameter parameter) {
        Pages<TPrdVehicleReleasePO> result = tPrdVehicleReleaseService.getList(query, parameter);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    @GetMapping("/getSubList")
    public Map<String, Object> getSubList(TPrdVehicleReleaseDTO query, PageParameter parameter) {
        Pages<TPrdVehicleReleasePO> result = tPrdVehicleReleaseService.getSubList(query, parameter);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    @GetMapping("/auditStatus/{id}")
    public Map<String, Object> auditStstus(@PathVariable("id") Long id) {
        final String methodName = "TPrdVehicleReleaseController:auditStstus";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tPrdVehicleReleaseService.auditStatusById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "放行成功" : "放行失败").toResult();
    }

    @GetMapping("/auditRevokeStatus/{id}")
    public Map<String, Object> auditRevokeStstus(@PathVariable("id") Long id) {
        final String methodName = "TPrdVehicleReleaseController:auditRevokeStstus";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tPrdVehicleReleaseService.auditRevokeStatusById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "撤销放行成功" : "撤销放行失败").toResult();
    }
}
