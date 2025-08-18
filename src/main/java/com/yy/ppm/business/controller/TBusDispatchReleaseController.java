package com.yy.ppm.business.controller;

import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.business.bean.dto.*;
import com.yy.ppm.business.service.TBusDispatchReleaseService;

import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 放行单表(TBusDispatchRelease)Controller
 * @Description
 * @createTime 2024年04月16日 16:03:00
 */
@RestController
@RequestMapping("/api/v1/internal/tBusDispatchRelease")
public class TBusDispatchReleaseController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TBusDispatchReleaseController.class);

    @Autowired
    private TBusDispatchReleaseService tBusDispatchReleaseService;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getPageList")
    public Map<String, Object> getPageList(TBusDispatchReleaseSearchDTO searchDTO) {
        final String methodName = "TBusDispatchReleaseController:getPageList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<Map<String,Object>> pages = tBusDispatchReleaseService.getPageList(searchDTO);

        LOGGER.exit(methodName + "result:" + pages);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 查询航次下所以的放行单
     * @param searchDTO
     * @return
     */
    @GetMapping("/detailListByCondition")
    public Map<String, Object> detailListByCondition(TBusDispatchReleaseSearchDTO searchDTO) {
        final String methodName = "TBusDispatchReleaseController:getDetailByCondition";
        LOGGER.enter(methodName + "[start]", "tBusDispatchReleaseDTO:" + searchDTO);

        List<TBusDispatchReleaseDTO> result = tBusDispatchReleaseService.getListByCondition(searchDTO);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询放行单下所有的票货
     * @param searchDTO
     * @return
     */
    @GetMapping("/cargoInfoListByCondition")
    public Map<String, Object> cargoInfoListByCondition(TBusDispatchReleaseDetailSearchDTO searchDTO) {
        final String methodName = "TBusDispatchReleaseController:getDetailByCondition";
        LOGGER.enter(methodName + "[start]", "tBusDispatchReleaseDTO:" + searchDTO);

        List<TBusDispatchReleaseDetailDTO> result = tBusDispatchReleaseService.cargoInfoListByCondition(searchDTO);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建（批量）
     * @param trustCargoDTOS
     * @return
     */
    @PostMapping("/addCargoList")
    public Map<String, Object> addCargoList(@RequestBody List<TBusTrustCargoDTO> trustCargoDTOS) {
        final String methodName = "TBusDispatchReleaseController:addList";
        LOGGER.enter(methodName + "[start]", "tBusDispatchReleaseDTO:" + trustCargoDTOS);

        tBusDispatchReleaseService.addCargoList(trustCargoDTOS);

        return  Response.SUCCESS.newBuilder().out("保存成功").toResult(true);
    }

    /**
     * 新建（批量）
     * @param trustCargoDTOS
     * @return
     */
    @PostMapping("/deleteDispatchRelease")
    public Map<String, Object> deleteDispatchRelease(@RequestBody List<TBusTrustCargoDTO> trustCargoDTOS) {
        final String methodName = "TBusDispatchReleaseController:addList";
        LOGGER.enter(methodName + "[start]", "tBusDispatchReleaseDTO:" + trustCargoDTOS);

        tBusDispatchReleaseService.deleteDispatchRelease(trustCargoDTOS);

        return  Response.SUCCESS.newBuilder().out("保存成功").toResult(true);
    }

    /**
     * 新建（批量）
     * @param tBusDispatchReleaseDTOS
     * @return
     */
    @PostMapping("/addList")
    public Map<String, Object> addList(@RequestBody List<TBusDispatchReleaseDTO> tBusDispatchReleaseDTOS) {
        final String methodName = "TBusDispatchReleaseController:addList";
        LOGGER.enter(methodName + "[start]", "tBusDispatchReleaseDTO:" + tBusDispatchReleaseDTOS);

        Map<String, Object> resultMap = tBusDispatchReleaseService.doListSave(tBusDispatchReleaseDTOS);

        LOGGER.exit(methodName);
        boolean flag = (boolean) resultMap.get("flag");
        String msg = (String) resultMap.get("msg");

        return flag ? Response.SUCCESS.newBuilder().out(msg).toResult(true) :
                Response.FAIL.newBuilder().out(msg).toResult(false);

    }













    /**
     * 查询单条记录
     *
     * @param id
     * @return
     */
    @GetMapping("/getDetail")
    public Map<String, Object> getDetail(@RequestParam("id") Long id) {
        final String methodName = "TBusDispatchReleaseController:getDetail";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        TBusDispatchReleaseDTO result = tBusDispatchReleaseService.getDetail(id);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }


    /**
     * 新建
     *
     * @param tBusDispatchReleaseDTO
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody TBusDispatchReleaseDTO tBusDispatchReleaseDTO) {
        final String methodName = "TBusDispatchReleaseController:add";
        LOGGER.enter(methodName + "[start]", "tBusDispatchReleaseDTO:" + tBusDispatchReleaseDTO);

        boolean flag = tBusDispatchReleaseService.doSave(tBusDispatchReleaseDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }



    /**
     * 修改
     *
     * @param tBusDispatchReleaseDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody TBusDispatchReleaseDTO tBusDispatchReleaseDTO) {
        final String methodName = "TBusDispatchReleaseController:update";
        LOGGER.enter(methodName + "[start]", "tBusDispatchReleaseDTO:" + tBusDispatchReleaseDTO);

        boolean flag = tBusDispatchReleaseService.doSave(tBusDispatchReleaseDTO);

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
        final String methodName = "TBusDispatchReleaseController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tBusDispatchReleaseService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 删除
     *
     * @param tBusDispatchReleaseDTO
     * @return
     */
    @DeleteMapping("/deleteByCondition")
    public Map<String, Object> deleteByCondition(TBusDispatchReleaseDTO tBusDispatchReleaseDTO) {
        final String methodName = "TBusDispatchReleaseController:deleteByCondition";
        LOGGER.enter(methodName + "[start]", "根据条件删除");

        boolean flag = tBusDispatchReleaseService.deleteByCondition(tBusDispatchReleaseDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }


}

