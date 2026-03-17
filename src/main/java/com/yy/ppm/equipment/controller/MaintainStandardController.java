package com.yy.ppm.equipment.controller;


import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.equipment.bean.dto.InspectionStandardDTO;
import com.yy.ppm.equipment.bean.dto.MaintainStandardDTO;
import com.yy.ppm.equipment.bean.po.InspectionStandardPO;
import com.yy.ppm.equipment.bean.po.MaintainStandardPO;
import com.yy.ppm.equipment.service.InspectionStandardService;
import com.yy.ppm.equipment.service.MaintainStandardService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/internal/maintainStandard")
public class MaintainStandardController {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(MaintainStandardController.class);

    @Resource
    private MaintainStandardService maintainStandardService;

    /**
     * 根据ID查询
     */
    @GetMapping("/queryByUnitId")
    public Map<String, Object> queryById(MaintainStandardDTO maintainStandardDTO) {
        final String methodName = "MaintainStandardController:getTree";
        LOGGER.enter(methodName + "[start]", "maintainStandardDTO:" + maintainStandardDTO);

        List<MaintainStandardPO> result = maintainStandardService.queryByUnitId(maintainStandardDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增
     */
    @PostMapping("/save")
    @Log(value= OperateTypeEnum.INSERT, title="保存润滑保养标准")
    public Map<String, Object> add(@RequestBody MaintainStandardDTO dto) {
        final String methodName = "MaintainStandardController:save";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        maintainStandardService.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }

    /**
     * 查询标准
     */
    @GetMapping("/queryAll")
    public Map<String, Object> queryAll(MaintainStandardDTO maintainStandardDTO, PageParameter parameter) {
        final String methodName = "MaintainStandardController:getTree";
        LOGGER.enter(methodName + "[start]", "maintainStandardDTO:" + maintainStandardDTO);

        Pages<MaintainStandardPO> result = maintainStandardService.queryAll(maintainStandardDTO,parameter);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

}
