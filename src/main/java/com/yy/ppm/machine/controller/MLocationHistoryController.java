package com.yy.ppm.machine.controller;

import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.machine.service.MLocationHistoryService;
import com.yy.ppm.machine.bean.dto.MLocationHistoryDTO;
import com.yy.ppm.machine.bean.dto.MLocationHistorySearchDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 车辆历史表(MLocationHistory)Controller
 * @Description
 * @createTime 2023年10月25日 10:46:00
 */
@RestController
@RequestMapping("/api/v1/internal/mLocationHistory")
public class MLocationHistoryController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(MLocationHistoryController.class);

    @Autowired
    private MLocationHistoryService mLocationHistoryService;

    /**
     * 查询单条记录
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/listByCondition")
    public Map<String, Object> listByCondition(MLocationHistorySearchDTO searchDTO) {
        final String methodName = "MLocationHistoryController:listByCondition";

        List<MLocationHistoryDTO> result = mLocationHistoryService.getListByCondition(searchDTO);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询单条记录
     * @param macId
     * @return
     */
    @GetMapping("/getDetail")
    public Map<String, Object> getDetail(@RequestParam("id") String macId) {
        final String methodName = "MLocationHistoryController:getDetail";

        MLocationHistoryDTO result = mLocationHistoryService.getDetail(macId);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    /**
     * 新建
     *
     * @param mLocationHistoryDTO
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody MLocationHistoryDTO mLocationHistoryDTO) {
        final String methodName = "MLocationHistoryController:add";

        boolean flag = mLocationHistoryService.doSave(mLocationHistoryDTO);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }



}

