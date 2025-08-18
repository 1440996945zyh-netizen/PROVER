package com.yy.ppm.largescreen.controller;

import cn.hutool.core.io.IORuntimeException;
import com.yy.common.enums.Response;
import com.yy.common.excel.export.utils.ResponseUtils;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.largescreen.bean.dto.SPortThroighputDTO;
import com.yy.ppm.largescreen.bean.dto.SPortThroighputSearchDTO;
import com.yy.ppm.largescreen.service.SPortThroighputService;
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
 * @ClassName 港区吞吐量表(SPortThroighput)Controller
 * @Description
 * @createTime 2024年03月15日 09:24:00
 */
@RestController
@RequestMapping("/api/v1/internal/sPortThroighput")
public class SPortThroighputController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(SPortThroighputController.class);

    @Autowired
    private SPortThroighputService sPortThroighputService;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getPageList")
    public Map<String, Object> getPageList(SPortThroighputSearchDTO searchDTO) {
        final String methodName = "SPortThroighputController:getPageList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<SPortThroighputDTO> pages = sPortThroighputService.getPageList(searchDTO);

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
        final String methodName = "SPortThroighputController:getDetail";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        SPortThroighputDTO result = sPortThroighputService.getDetail(id);

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
    public Map<String, Object> detailListByCondition(SPortThroighputSearchDTO searchDTO) {
        final String methodName = "SPortThroighputController:getDetailByCondition";
        LOGGER.enter(methodName + "[start]", "sPortThroighputDTO:" + searchDTO);

        List<SPortThroighputDTO> result = sPortThroighputService.getListByCondition(searchDTO);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }


    /**
     * 新建
     *
     * @param sPortThroighputDTO
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody SPortThroighputDTO sPortThroighputDTO) {
        final String methodName = "SPortThroighputController:add";
        LOGGER.enter(methodName + "[start]", "sPortThroighputDTO:" + sPortThroighputDTO);

        boolean flag = sPortThroighputService.doSave(sPortThroighputDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 新建（批量）
     *
     * @param sPortThroighputDTOS
     * @return
     */
    @PostMapping("/addList")
    public Map<String, Object> addList(@RequestBody List<SPortThroighputDTO> sPortThroighputDTOS) {
        final String methodName = "SPortThroighputController:addList";
        LOGGER.enter(methodName + "[start]", "sPortThroighputDTO:" + sPortThroighputDTOS);

        Map<String, Object> resultMap = sPortThroighputService.doListSave(sPortThroighputDTOS);

        LOGGER.exit(methodName);
        boolean flag = (boolean) resultMap.get("flag");
        String msg = (String) resultMap.get("msg");

        return flag ? Response.SUCCESS.newBuilder().out(msg).toResult(true) :
                Response.FAIL.newBuilder().out(msg).toResult(false);

    }

    /**
     * 修改
     *
     * @param sPortThroighputDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody SPortThroighputDTO sPortThroighputDTO) {
        final String methodName = "SPortThroighputController:update";
        LOGGER.enter(methodName + "[start]", "sPortThroighputDTO:" + sPortThroighputDTO);

        boolean flag = sPortThroighputService.doSave(sPortThroighputDTO);

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
        final String methodName = "SPortThroighputController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = sPortThroighputService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 删除
     *
     * @param sPortThroighputDTO
     * @return
     */
    @DeleteMapping("/deleteByCondition")
    public Map<String, Object> deleteByCondition(SPortThroighputDTO sPortThroighputDTO) {
        final String methodName = "SPortThroighputController:deleteByCondition";
        LOGGER.enter(methodName + "[start]", "根据条件删除");

        boolean flag = sPortThroighputService.deleteByCondition(sPortThroighputDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 港区吞吐量
     *
     * @param
     * @param response
     * @return
     */
    @GetMapping("/exportExcel")
    public void exportExcel(SPortThroighputSearchDTO searchDTO, HttpServletResponse response) {
        ResponseUtils.compliantWithExcel(response, "在港车辆");
        try {
            byte[] bytes = sPortThroighputService.exportExcel(searchDTO);
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
        sPortThroighputService.exportTemplate(response);
        return Response.SUCCESS.newBuilder().out("导出成功").toResult();
    }
    /**
     * 批量导入
     * @param file
     * @return
     */
    @PostMapping("/importList")
    public Map<String, Object> importList( MultipartFile file) {
        boolean flag = sPortThroighputService.importList(file);
        return Response.SUCCESS.newBuilder().out(flag ? "批量添加成功" : "批量添加失败").toResult(flag);
    }
}

