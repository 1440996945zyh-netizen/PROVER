package com.yy.ppm.produce.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.framework.annotation.Log;
import com.yy.ppm.business.bean.dto.TBusVehicleTransferDTO;
import com.yy.ppm.business.bean.po.TBusTrustTradeReservatCarPO;
import com.yy.ppm.produce.bean.dto.TWeightPlanItemDTO;
import com.yy.ppm.produce.bean.dto.TWeightRecordDTO;
import com.yy.ppm.produce.service.TWeightPlanService;
import com.yy.ppm.produce.bean.dto.TWeightPlanDTO;
import com.yy.ppm.produce.bean.dto.TWeightPlanSearchDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 杂项过磅计划表(TWeightPlan)Controller
 * @Description
 * @createTime 2023年12月05日 08:39:00
 */
@RestController
@RequestMapping("/api/v1/internal/tWeightPlan")
public class TWeightPlanController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TWeightPlanController.class);

    @Autowired
    private TWeightPlanService tWeightPlanService;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(TWeightPlanSearchDTO searchDTO) {
        final String methodName = "TWeightPlanController:getList";

        Pages<TWeightPlanDTO> pages = tWeightPlanService.getList(searchDTO);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 查询单条记录
     *
     * @param id
     * @return
     */
    @GetMapping("/getDetail")
    public Map<String, Object> getDetail(@RequestParam("id") Long id) {
        final String methodName = "TWeightPlanController:getDetail";

        TWeightPlanDTO result = tWeightPlanService.getDetail(id);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     *
     * @param tWeightPlanDTO
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody TWeightPlanDTO tWeightPlanDTO) {
        final String methodName = "TWeightPlanController:add";
        LOGGER.enter(methodName + "[start]", "tWeightPlanDTO:" + tWeightPlanDTO);

        boolean flag = tWeightPlanService.doSave(tWeightPlanDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改
     *
     * @param tWeightPlanDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody TWeightPlanDTO tWeightPlanDTO) {
        final String methodName = "TWeightPlanController:update";
        LOGGER.enter(methodName + "[start]", "tWeightPlanDTO:" + tWeightPlanDTO);

        boolean flag = tWeightPlanService.doSave(tWeightPlanDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "TWeightPlanController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tWeightPlanService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 修改主界面状态
     *
     * @param tWeightPlanDTO
     * @return
     */
    @PutMapping("/changeMainStatus")
    public Map<String, Object> changeMainStatus(@RequestBody TWeightPlanDTO tWeightPlanDTO) {
        final String methodName = "TWeightPlanController:changeMainStatus";
        LOGGER.enter(methodName + "[start]", "tWeightPlanDTO:" + tWeightPlanDTO);

        int count = tWeightPlanService.changeMainStatus(tWeightPlanDTO);

        LOGGER.exit(methodName + "result:" + count);

        return Response.SUCCESS.newBuilder().out("修改成功").toResult(count);
    }
    /**
     * 查询过磅信息
     * @param planNo
     * @return
     */
    @GetMapping("/getSundryList")
    public Map<String, Object> getSundryList(String planNo) {
        final String methodName = "TWeightPlanController:getSundryList";
        LOGGER.enter(methodName + "[start]", "planNo:" + planNo);

        List<TWeightRecordDTO> resultList = tWeightPlanService.getSundryList(planNo);

        LOGGER.exit(methodName + "[end]", "result:" + resultList);
        return Response.SUCCESS.newBuilder().toResult(resultList);
    }

    /**
     * 审核
     *
     * @param tWeightPlanDTO
     * @return
     */
    @PostMapping("/examine")
    public Map<String, Object> examine(@RequestBody TWeightPlanDTO tWeightPlanDTO) {
        final String methodName = "TWeightPlanController:examine";
        LOGGER.enter(methodName + "[start]", "tWeightPlanDTO:" + tWeightPlanDTO);

        boolean flag = tWeightPlanService.examine(tWeightPlanDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().toResult(flag);
    }

    @PostMapping("/parseCars")
    public Map<String, Object> parseCars(MultipartFile file) {
        List<TWeightPlanItemDTO> result = tWeightPlanService.parseCars(file);
        return Response.SUCCESS.newBuilder().toResult(result);
    }


}

