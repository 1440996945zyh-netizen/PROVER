package com.yy.ppm.machine.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.framework.annotation.Log;
import com.yy.ppm.machine.service.MLocationService;
import com.yy.ppm.machine.bean.dto.MLocationDTO;
import com.yy.ppm.machine.bean.dto.MLocationSearchDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 实时车辆表(MLocation)Controller
 * @Description
 * @createTime 2023年10月25日 10:21:00
 */
@RestController
@RequestMapping("/api/v1/internal/mLocation")
public class MLocationController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(MLocationController.class);

    @Autowired
    private MLocationService mLocationService;


    /**
     * 查询单条记录
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/listByCondition")
//    @Log(title = "实时车辆表查询", value = OperateTypeEnum.QUERY)
    public Map<String, Object> listByCondition(MLocationSearchDTO searchDTO) {
        final String methodName = "MLocationController:listByCondition";

        List<MLocationDTO> result = mLocationService.getListByCondition(searchDTO);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     *
     * @param mLocationDTO
     * @return
     */
    @PostMapping("/add")
//    @Log(title = "实时车辆表新增", value = OperateTypeEnum.INSERT)
    public Map<String, Object> add(@RequestBody MLocationDTO mLocationDTO) {
        final String methodName = "MLocationController:add";

        boolean flag = mLocationService.doSave(mLocationDTO);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }



}

