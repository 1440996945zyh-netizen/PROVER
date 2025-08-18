package com.yy.ppm.largescreen.controller;

import cn.hutool.core.io.IORuntimeException;
import com.yy.common.excel.export.utils.ResponseUtils;
import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.largescreen.service.SInportCarService;
import com.yy.ppm.largescreen.bean.dto.SInportCarDTO;
import com.yy.ppm.largescreen.bean.dto.SInportCarSearchDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.List;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 在港车辆表(SInportCar)Controller
 * @Description
 * @createTime 2024年03月14日 10:42:00
 */
@RestController
@RequestMapping("/api/v1/internal/sInportCar")
public class SInportCarController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(SInportCarController.class);

    @Autowired
    private SInportCarService sInportCarService;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getPageList")
    public Map<String, Object> getPageList(SInportCarSearchDTO searchDTO) {
        final String methodName = "SInportCarController:getPageList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<SInportCarDTO> pages = sInportCarService.getPageList(searchDTO);

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
        final String methodName = "SInportCarController:getDetail";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        SInportCarDTO result = sInportCarService.getDetail(id);

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
    public Map<String, Object> detailListByCondition(SInportCarSearchDTO searchDTO) {
        final String methodName = "SInportCarController:getDetailByCondition";
        LOGGER.enter(methodName + "[start]", "sInportCarDTO:" + searchDTO);

        List<SInportCarDTO> result = sInportCarService.getListByCondition(searchDTO);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }


    /**
     * 新建
     *
     * @param sInportCarDTO
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody SInportCarDTO sInportCarDTO) {
        final String methodName = "SInportCarController:add";
        LOGGER.enter(methodName + "[start]", "sInportCarDTO:" + sInportCarDTO);

        boolean flag = sInportCarService.doSave(sInportCarDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 新建（批量）
     *
     * @param sInportCarDTOS
     * @return
     */
    @PostMapping("/addList")
    public Map<String, Object> addList(@RequestBody List<SInportCarDTO> sInportCarDTOS) {
        final String methodName = "SInportCarController:addList";
        LOGGER.enter(methodName + "[start]", "sInportCarDTO:" + sInportCarDTOS);

        Map<String, Object> resultMap = sInportCarService.doListSave(sInportCarDTOS);

        LOGGER.exit(methodName);
        boolean flag = (boolean) resultMap.get("flag");
        String msg = (String) resultMap.get("msg");

        return flag ? Response.SUCCESS.newBuilder().out(msg).toResult(true) :
                Response.FAIL.newBuilder().out(msg).toResult(false);

    }

    /**
     * 修改
     *
     * @param sInportCarDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody SInportCarDTO sInportCarDTO) {
        final String methodName = "SInportCarController:update";
        LOGGER.enter(methodName + "[start]", "sInportCarDTO:" + sInportCarDTO);

        boolean flag = sInportCarService.doSave(sInportCarDTO);

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
        final String methodName = "SInportCarController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = sInportCarService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 删除
     *
     * @param sInportCarDTO
     * @return
     */
    @DeleteMapping("/deleteByCondition")
    public Map<String, Object> deleteByCondition(SInportCarDTO sInportCarDTO) {
        final String methodName = "SInportCarController:deleteByCondition";
        LOGGER.enter(methodName + "[start]", "根据条件删除");

        boolean flag = sInportCarService.deleteByCondition(sInportCarDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 在港车辆导出
     *
     * @param
     * @param response
     * @return
     */
    @GetMapping("/exportExcel")
    public void exportExcel(SInportCarSearchDTO searchDTO, HttpServletResponse response) {
        ResponseUtils.compliantWithExcel(response, "在港车辆");
        try {
            byte[] bytes = sInportCarService.exportExcel(searchDTO);
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
        sInportCarService.exportTemplate(response);
        return Response.SUCCESS.newBuilder().out("导出成功").toResult();
    }

    /**
     * 批量导入
     * @param file
     * @return
     */
    @PostMapping("/importList")
    public Map<String, Object> importList( MultipartFile file) {
        boolean flag = sInportCarService.importList(file);
        return Response.SUCCESS.newBuilder().out(flag ? "批量添加成功" : "批量添加失败").toResult(flag);
    }
}

