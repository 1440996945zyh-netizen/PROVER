package com.yy.ppm.business.controller;

import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.business.service.TBusOrderService;
import com.yy.ppm.business.bean.dto.TBusOrderDTO;
import com.yy.ppm.business.bean.dto.TBusOrderSearchDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 委托单主表(TBusOrder)Controller
 * @Description
 * @createTime 2024年10月23日 09:01:00
 */
@RestController
@RequestMapping("/api/v1/internal/tBusOrder")
public class TBusOrderController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TBusOrderController.class);

    @Autowired
    private TBusOrderService tBusOrderService;

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(TBusOrderSearchDTO searchDTO) {
        final String methodName = "TBusOrderController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);
        Pages<TBusOrderDTO> pages = tBusOrderService.getList(searchDTO);
        LOGGER.exit(methodName + "result:" + pages);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }


    /**
     * 修改
     * @param tBusOrderDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody TBusOrderDTO tBusOrderDTO) {
        final String methodName = "TBusOrderController:update";
        LOGGER.enter(methodName + "[start]", "tBusOrderDTO:" + tBusOrderDTO);
        boolean flag = tBusOrderService.updateStatus(tBusOrderDTO);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }





    /**
     * 查询单条记录
     *
     * @param id
     * @return
     */
    @GetMapping("/getDetail")
    public Map<String, Object> getDetail(Long id) {
        final String methodName = "TBusOrderController:getDetail";
        LOGGER.enter(methodName + "[start]", "id:" + id);
        Map<String,Object> result = tBusOrderService.getDetail(id);
        LOGGER.exit(methodName + "result:" + result);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     *
     * @param tBusOrderDTO
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody TBusOrderDTO tBusOrderDTO) {
        final String methodName = "TBusOrderController:add";
        LOGGER.enter(methodName + "[start]", "tBusOrderDTO:" + tBusOrderDTO);

        boolean flag = tBusOrderService.doSave(tBusOrderDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "TBusOrderController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tBusOrderService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

}

