package com.yy.ppm.produce.controller;

import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.produce.service.THqDataService;
import com.yy.ppm.produce.bean.dto.THqDataDTO;
import com.yy.ppm.produce.bean.dto.THqDataSearchDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 海清数据补录表(THqData)Controller
 * @Description
 * @createTime 2025年04月24日 17:23:00
 */
@RestController
@RequestMapping("/api/v1/internal/tHqData")
public class THqDataController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(THqDataController.class);

    @Autowired
    private THqDataService tHqDataService;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(THqDataSearchDTO searchDTO) {
        final String methodName = "THqDataController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<THqDataDTO> pages = tHqDataService.getList(searchDTO);

        LOGGER.exit(methodName + "result:" + pages);

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
        final String methodName = "THqDataController:getDetail";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        THqDataDTO result = tHqDataService.getDetail(id);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    /**
     * 查询单条记录
     * @param
     * @return
     */
    @PostMapping("/getHqByCargoInfoId")
    public Map<String, Object> getHqByCargoInfoId(@RequestBody List<Long> ids) {

        List<Map<String,String>> result = tHqDataService.getHqByCargoInfoId(ids);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     * @param tHqDataDTO
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody THqDataDTO tHqDataDTO) {
        final String methodName = "THqDataController:add";
        LOGGER.enter(methodName + "[start]", "tHqDataDTO:" + tHqDataDTO);

        boolean flag = tHqDataService.doSave(tHqDataDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 新建
     * @param list
     * @return
     */
    @PostMapping("/addList")
    public Map<String, Object> addList(@RequestBody List<THqDataDTO> list) {

        boolean flag = tHqDataService.listSave(list);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改
     *
     * @param tHqDataDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody THqDataDTO tHqDataDTO) {
        final String methodName = "THqDataController:update";
        LOGGER.enter(methodName + "[start]", "tHqDataDTO:" + tHqDataDTO);

        boolean flag = tHqDataService.doSave(tHqDataDTO);

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
        final String methodName = "THqDataController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tHqDataService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

}

