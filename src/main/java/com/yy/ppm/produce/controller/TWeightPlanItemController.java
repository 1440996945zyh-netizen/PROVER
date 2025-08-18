package com.yy.ppm.produce.controller;

import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.produce.bean.dto.TWeightPlanDTO;
import com.yy.ppm.produce.service.TWeightPlanItemService;
import com.yy.ppm.produce.bean.dto.TWeightPlanItemDTO;
import com.yy.ppm.produce.bean.dto.TWeightPlanItemSearchDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName (TWeightPlanItem)Controller
 * @Description
 * @createTime 2023年12月05日 08:39:00
 */
@RestController
@RequestMapping("/api/v1/internal/tWeightPlanItem")
public class TWeightPlanItemController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TWeightPlanItemController.class);

    @Autowired
    private TWeightPlanItemService tWeightPlanItemService;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(TWeightPlanItemSearchDTO searchDTO) {
        final String methodName = "TWeightPlanItemController:getList";

        Pages<TWeightPlanItemDTO> pages = tWeightPlanItemService.getList(searchDTO);

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
        final String methodName = "TWeightPlanItemController:getDetail";

        TWeightPlanItemDTO result = tWeightPlanItemService.getDetail(id);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     *
     * @param tWeightPlanItemDTO
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody TWeightPlanItemDTO tWeightPlanItemDTO) {
        final String methodName = "TWeightPlanItemController:add";
        LOGGER.enter(methodName + "[start]", "tWeightPlanItemDTO:" + tWeightPlanItemDTO);

        boolean flag = tWeightPlanItemService.doSave(tWeightPlanItemDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改
     *
     * @param tWeightPlanItemDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody TWeightPlanItemDTO tWeightPlanItemDTO) {
        final String methodName = "TWeightPlanItemController:update";
        LOGGER.enter(methodName + "[start]", "tWeightPlanItemDTO:" + tWeightPlanItemDTO);

        boolean flag = tWeightPlanItemService.doSave(tWeightPlanItemDTO);

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
        final String methodName = "TWeightPlanItemController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tWeightPlanItemService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 修改子界面状态
     *
     * @param tWeightPlanItemDTO
     * @return
     */
    @PutMapping("/changeChildStatus")
    public Map<String, Object> changeChildStatus(@RequestBody TWeightPlanItemDTO tWeightPlanItemDTO) {
        final String methodName = "TWeightPlanItemController:changeChildStatus";
        LOGGER.enter(methodName + "[start]", "tWeightPlanDTO:" + tWeightPlanItemDTO);

        int count = tWeightPlanItemService.changeChildStatus(tWeightPlanItemDTO);

        LOGGER.exit(methodName + "result:" + count);

        return Response.SUCCESS.newBuilder().out("修改成功").toResult(count);
    }
}

