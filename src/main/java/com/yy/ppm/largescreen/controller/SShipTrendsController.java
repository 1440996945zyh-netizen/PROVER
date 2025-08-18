package com.yy.ppm.largescreen.controller;

import cn.hutool.core.io.IORuntimeException;
import com.yy.common.enums.Response;
import com.yy.common.excel.export.utils.ResponseUtils;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.largescreen.bean.dto.SShipTrendsDTO;
import com.yy.ppm.largescreen.bean.dto.SShipTrendsSearchDTO;
import com.yy.ppm.largescreen.service.SShipTrendsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName (SShipTrends)Controller
 * @Description
 * @createTime 2024年03月15日 09:35:00
 */
@RestController
@RequestMapping("/api/v1/internal/sShipTrends")
public class SShipTrendsController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(SShipTrendsController.class);

    @Autowired
    private SShipTrendsService sShipTrendsService;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getPageList")
    public Map<String, Object> getPageList(SShipTrendsSearchDTO searchDTO) {
        final String methodName = "SShipTrendsController:getPageList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<SShipTrendsDTO> pages = sShipTrendsService.getPageList(searchDTO);

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
        final String methodName = "SShipTrendsController:getDetail";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        SShipTrendsDTO result = sShipTrendsService.getDetail(id);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询单条记录
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/detailListByCondition")
    public Map<String, Object> detailListByCondition(SShipTrendsSearchDTO searchDTO) {
        final String methodName = "SShipTrendsController:getDetailByCondition";
        LOGGER.enter(methodName + "[start]", "sShipTrendsDTO:" + searchDTO);

        List<SShipTrendsDTO> result = sShipTrendsService.getListByCondition(searchDTO);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }


    /**
     * 新建
     *
     * @param sShipTrendsDTO
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody SShipTrendsDTO sShipTrendsDTO) {
        final String methodName = "SShipTrendsController:add";
        LOGGER.enter(methodName + "[start]", "sShipTrendsDTO:" + sShipTrendsDTO);

        boolean flag = sShipTrendsService.doSave(sShipTrendsDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 新建（批量）
     *
     * @param sShipTrendsDTOS
     * @return
     */
    @PostMapping("/addList")
    public Map<String, Object> addList(@RequestBody List<SShipTrendsDTO> sShipTrendsDTOS) {
        final String methodName = "SShipTrendsController:addList";
        LOGGER.enter(methodName + "[start]", "sShipTrendsDTO:" + sShipTrendsDTOS);

        Map<String, Object> resultMap = sShipTrendsService.doListSave(sShipTrendsDTOS);

        LOGGER.exit(methodName);
        boolean flag = (boolean) resultMap.get("flag");
        String msg = (String) resultMap.get("msg");

        return flag ? Response.SUCCESS.newBuilder().out(msg).toResult(true) :
                Response.FAIL.newBuilder().out(msg).toResult(false);

    }

    /**
     * 修改
     *
     * @param sShipTrendsDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody SShipTrendsDTO sShipTrendsDTO) {
        final String methodName = "SShipTrendsController:update";
        LOGGER.enter(methodName + "[start]", "sShipTrendsDTO:" + sShipTrendsDTO);

        boolean flag = sShipTrendsService.doSave(sShipTrendsDTO);

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
        final String methodName = "SShipTrendsController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = sShipTrendsService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 删除
     *
     * @param sShipTrendsDTO
     * @return
     */
    @DeleteMapping("/deleteByCondition")
    public Map<String, Object> deleteByCondition(SShipTrendsDTO sShipTrendsDTO) {
        final String methodName = "SShipTrendsController:deleteByCondition";
        LOGGER.enter(methodName + "[start]", "根据条件删除");

        boolean flag = sShipTrendsService.deleteByCondition(sShipTrendsDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 船舶动态导出
     *
     * @param
     * @param response
     * @return
     */
    @GetMapping("/exportExcel")
    public void exportExcel(SShipTrendsSearchDTO searchDTO, HttpServletResponse response) {
        ResponseUtils.compliantWithExcel(response, "在港车辆");
        try {
            byte[] bytes = sShipTrendsService.exportExcel(searchDTO);
            try {
                response.getOutputStream().write(bytes);
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        } catch (Exception e) {
            ResponseUtils.resetCompliant(response);
            throw e;
        }
    }

    @GetMapping("/downTemplate")
    public Map<String, Object> downTemplate( HttpServletResponse response) {
        sShipTrendsService.exportTemplate(response);
        return Response.SUCCESS.newBuilder().out("导出成功").toResult();
    }
    /**
     * 批量导入
     * @param file
     * @return
     */
    @PostMapping("/importList")
    public Map<String, Object> importList( MultipartFile file) {
        boolean flag = sShipTrendsService.importList(file);
        return Response.SUCCESS.newBuilder().out(flag ? "批量添加成功" : "批量添加失败").toResult(flag);
    }
}

