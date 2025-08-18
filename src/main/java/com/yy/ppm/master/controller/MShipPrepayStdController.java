package com.yy.ppm.master.controller;

import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.master.service.MShipPrepayStdService;
import com.yy.ppm.master.bean.dto.MShipPrepayStdDTO;
import com.yy.ppm.master.bean.dto.MShipPrepayStdSearchDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 船舶预缴标准, (MShipPrepayStd)Controller
 * @Description
 * @createTime 2023年10月23日 15:50:00
 */
@RestController
@RequestMapping("/api/v1/internal/mShipPrepayStd")
public class MShipPrepayStdController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(MShipPrepayStdController.class);

    @Autowired
    private MShipPrepayStdService mShipPrepayStdService;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(MShipPrepayStdSearchDTO searchDTO) {
        final String methodName = "MShipPrepayStdController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<MShipPrepayStdDTO> pages = mShipPrepayStdService.getList(searchDTO);

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
        final String methodName = "MShipPrepayStdController:getDetail";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        MShipPrepayStdDTO result = mShipPrepayStdService.getDetail(id);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     *
     * @param mShipPrepayStdDTO
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody MShipPrepayStdDTO mShipPrepayStdDTO) {
        final String methodName = "MShipPrepayStdController:add";
        LOGGER.enter(methodName + "[start]", "mShipPrepayStdDTO:" + mShipPrepayStdDTO);

        boolean flag = mShipPrepayStdService.doSave(mShipPrepayStdDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改
     *
     * @param mShipPrepayStdDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody MShipPrepayStdDTO mShipPrepayStdDTO) {
        final String methodName = "MShipPrepayStdController:update";
        LOGGER.enter(methodName + "[start]", "mShipPrepayStdDTO:" + mShipPrepayStdDTO);

        boolean flag = mShipPrepayStdService.doSave(mShipPrepayStdDTO);

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
        final String methodName = "MShipPrepayStdController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = mShipPrepayStdService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

}

