package com.yy.ppm.machine.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.framework.annotation.Log;
import com.yy.ppm.machine.bean.dto.*;
import com.yy.ppm.machine.bean.po.TMacWorkNowPO;
import com.yy.ppm.machine.service.TMacTerminalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 车载终端基础信息
 * @author zcc
 * @Date 2023/09/06
 */
@RestController
@RequestMapping("/api/v1/external/TMacDyTally")
@Validated
public class TMacDyTallyController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TMacDyTallyController.class);

    @Autowired
    private TMacTerminalService tMacTerminalService;


    /**
     * 根据设备信息查询作业指令
     * @param imei
     * @return
     */
    @GetMapping("/getDyPlanByCondition")
    public Map<String, Object> getDyWorkPlanByCondition(String imei, String workDate, String classCode) {
        List<Map<String,Object>> resultList = tMacTerminalService.getDyPlanByCondition(imei, workDate, classCode);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(resultList);
    }

    /**
     * 根据设备信息查询作业指令
     * @param imei
     * @return
     */
    @GetMapping("/getPickUpFrom")
    public Map<String, Object> getPickUpFrom(String imei, Long workPlanId) {
        Map<String,Object> resultList = tMacTerminalService.getPickUpFrom(imei, workPlanId);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(resultList);
    }

    /**
     * 根据设备信息查询作业指令
     * @param cargoInfoId
     * @return
     */
    @GetMapping("/getPortStorage")
    public Map<String, Object> getPortStorage(String cargoInfoId,String workPlanId) {
        Map<String,Object> resultList = tMacTerminalService.getPortStorage(cargoInfoId,workPlanId);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(resultList);
    }

    /**
     * 根据设备信息查询作业指令
     * @param workPlanId
     * @return
     */
    @GetMapping("/getPlanLocation")
    public Map<String, Object> getPlanLocation(String cargoInfoId,String workPlanId) {
        Map<String,Object> resultList = tMacTerminalService.getPlanLocation(cargoInfoId,workPlanId);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(resultList);
    }

    /**
     * 根据设备信息查询作业指令
     * @return
     */
    @GetMapping("/getTallyByPlanId")
    public Map<String, Object> getTallyByPlanId(String workDate,String classCode,String trustId,String imei) {
        Map<String,Object> resultList = tMacTerminalService.getTallyByPlanId(workDate,classCode,trustId,imei);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(resultList);
    }

    /**
     * 根据设备信息查询作业指令
     * @return
     */
    @GetMapping("/tallyListDelete")
    public Map<String, Object> tallyListDelete(String tallyId) {
        boolean flag = tMacTerminalService.tallyListDelete(tallyId);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult(flag);
    }

}
