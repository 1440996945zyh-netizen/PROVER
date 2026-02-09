package com.yy.ppm.equipment.controller;


import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.equipment.bean.dto.MaintainPlanDTO;
import com.yy.ppm.equipment.bean.po.MEquipmentInfoPO;
import com.yy.ppm.equipment.bean.po.MaintainPlanPO;
import com.yy.ppm.equipment.service.MaintainPlanService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/internal/maintainPlan")
public class MaintainPlanController {
    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(MaintainPlanController.class);

    @Resource
    private MaintainPlanService maintainPlanService;

    /**
     * 查询点检计划（分页）
     */
    @GetMapping("/queryAll")
    public Map<String, Object> queryAll(MaintainPlanDTO maintainPlanDTO, PageParameter parameter) {
        final String methodName = "MaintainPlanController:queryAll";
        LOGGER.enter(methodName + "[start]", "maintainPlanDTO:" + maintainPlanDTO);

        Pages<MaintainPlanPO> result = maintainPlanService.queryAll(maintainPlanDTO,parameter);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询物资仓库
     */
    @GetMapping("/getById")
    public Map<String, Object> getById(@RequestParam("id") Long id) {
        final String methodName = "MaintainPlanController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        MaintainPlanPO result = maintainPlanService.getById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增点检计划
     */
    @PostMapping("/add")
    @Log(title = "新增点检计划", value = OperateTypeEnum.INSERT)
    public Map<String, Object> add(@RequestBody MaintainPlanPO dto) {
        final String methodName = "MaintainPlanController:add";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        maintainPlanService.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 修改点检计划
     */
    @PutMapping("/update")
    @Log(title = "修改点检计划", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> update(@RequestBody MaintainPlanPO dto) {
        final String methodName = "MaintainPlanController:update";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        maintainPlanService.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 删除点检计划
     */
    @DeleteMapping("/delete")
    @Log(title = "删除点检计划", value = OperateTypeEnum.DELETE)
    public Map<String, Object> delete(Long id) {
        final String methodName = "MaintainPlanController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        maintainPlanService.deleteById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 根据设备小类查询设备列表
     */
    @GetMapping("/getEquipListById")
    public Map<String, Object> getEquipListById(@RequestParam("id") Long id) {
        final String methodName = "MaintainPlanController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        List<MEquipmentInfoPO> result = maintainPlanService.getEquipListById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }



    /**
     * 提报润滑保养计划
     */
    @GetMapping("/report")
    @Log(title = "提报润滑保养计划", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> report(MaintainPlanPO dto) {
        final String methodName = "MaintainPlanController:report";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        maintainPlanService.report(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("提报成功").toResult();
    }

}
