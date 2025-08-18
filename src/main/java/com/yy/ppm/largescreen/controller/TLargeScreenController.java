package com.yy.ppm.largescreen.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.ppm.largescreen.service.TLargeScreenService;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 大屏展示
 * @author zcc
 * @date 2024/02/29
 */
@RestController
@RequestMapping("/api/v1/external/tLargeScreen")
@Validated
@Tag(name = "大屏展示")
public class TLargeScreenController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TLargeScreenController.class);

    @Autowired
    private TLargeScreenService tLargeScreenService;

    /**
     * 查询吞吐量信息（散杂货）
     * @param portCode
     * @return
     */
    @GetMapping("/getThroughputSInfo")
    @Validated
    public Map<String, Object> getThroughputSInfo(String portCode) {
        final String methodName = "TLargeScreenController:getThroughputSInfo";
        LOGGER.enter(methodName + "[start]", "portCode:" + portCode);

        Map<String, Object> result = tLargeScreenService.getThroughputSInfo(portCode);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询吞吐量信息（集装箱）
     * @param portCode
     * @return
     */
    @GetMapping("/getThroughputJInfo")
    @Validated
    public Map<String, Object> getThroughputJInfo(String portCode) {
        final String methodName = "TLargeScreenController:getThroughputJInfo";
        LOGGER.enter(methodName + "[start]", "portCode:" + portCode);

        Map<String, Object> result = tLargeScreenService.getThroughputJInfo(portCode);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询散杂货吞吐量列表
     * @param portCode
     * @param dataType{1: 月份 2: 日期}
     * @return
     */
    @GetMapping("/getThroughputSList")
    @Validated
    public Map<String, Object> getThroughputSList(String portCode, String dataType) {
        final String methodName = "TLargeScreenController:getThroughputSList";
        LOGGER.enter(methodName + "[start]", "portCode:" + portCode + " dataType:" + dataType);

        List<Map<String, Object>> resultList = tLargeScreenService.getThroughputSList(portCode, dataType);

        LOGGER.exit(methodName + "result:" + resultList);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(resultList);
    }

    /**
     * 查询集装箱吞吐量列表
     * @param portCode
     * @param dataType{1: 月份 2: 日期}
     * @return
     */
    @GetMapping("/getThroughputJList")
    @Validated
    public Map<String, Object> getThroughputJList(String portCode, String dataType) {
        final String methodName = "TLargeScreenController:getThroughputJList";
        LOGGER.enter(methodName + "[start]", "portCode:" + portCode + " dataType:" + dataType);

        List<Map<String, Object>> resultList = tLargeScreenService.getThroughputJList(portCode, dataType);

        LOGGER.exit(methodName + "result:" + resultList);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(resultList);
    }

    /**
     * 查询在港车辆数量、在港时长
     * @param portCode
     * @return
     */
    @GetMapping("/getInPortCarNumAndDuration")
    @Validated
    public Map<String, Object> getInPortCarNumAndDuration(String portCode) {
        final String methodName = "TLargeScreenController:getInPortCarNumAndDuration";
        LOGGER.enter(methodName + "[start]", "portCode:" + portCode);

        Map<String, Object> result = tLargeScreenService.getInPortCarNumAndDuration(portCode);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询散杂货港货信息（饼状图）
     * @return
     */
    @GetMapping("/getPortStorage")
    @Validated
    public Map<String, Object> getPortStorage(String portCode) {
        final String methodName = "TLargeScreenController:getPortStorage";
        LOGGER.enter(methodName + "[start]");

        Map<String, Object> result = tLargeScreenService.getPortStorage(portCode);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询船舶计划
     * @param portCode
     * @return
     */
    @GetMapping("/getShipPlanList")
    @Validated
    public Map<String, Object> getShipPlanList(String portCode) {
        final String methodName = "TLargeScreenController:getShipPlanList";
        LOGGER.enter(methodName + "[start]", "portCode:" + portCode);

        List<Map<String, Object>> result = tLargeScreenService.getShipPlan(portCode);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询船舶动态
     * @param portCode
     * @return
     */
    @GetMapping("/getShipDynamicsList")
    @Validated
    public Map<String, Object> getShipDynamicsList(String portCode) {
        final String methodName = "TLargeScreenController:getShipDynamicsList";
        LOGGER.enter(methodName + "[start]", "portCode:" + portCode);

        List<Map<String, Object>> result = tLargeScreenService.getShipDynamics(portCode);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
}
