package com.yy.ppm.largescreen.controller;

import cn.hutool.core.io.IORuntimeException;
import com.yy.common.excel.export.utils.ResponseUtils;
import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.largescreen.bean.dto.SInportCarSearchDTO;
import com.yy.ppm.largescreen.service.SPortStorageService;
import com.yy.ppm.largescreen.bean.dto.SPortStorageDTO;
import com.yy.ppm.largescreen.bean.dto.SPortStorageSearchDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.List;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName (SPortStorage)Controller
 * @Description
 * @createTime 2024年03月14日 23:13:00
 */
@RestController
@RequestMapping("/api/v1/internal/sPortStorage")
public class SPortStorageController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(SPortStorageController.class);

    @Autowired
    private SPortStorageService sPortStorageService;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getPageList")
    public Map<String, Object> getPageList(SPortStorageSearchDTO searchDTO) {
        final String methodName = "SPortStorageController:getPageList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<SPortStorageDTO> pages = sPortStorageService.getPageList(searchDTO);

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
        final String methodName = "SPortStorageController:getDetail";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        SPortStorageDTO result = sPortStorageService.getDetail(id);

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
    public Map<String, Object> detailListByCondition(SPortStorageSearchDTO searchDTO) {
        final String methodName = "SPortStorageController:getDetailByCondition";
        LOGGER.enter(methodName + "[start]", "sPortStorageDTO:" + searchDTO);

        List<SPortStorageDTO> result = sPortStorageService.getListByCondition(searchDTO);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }


    /**
     * 新建
     *
     * @param sPortStorageDTO
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody SPortStorageDTO sPortStorageDTO) {
        final String methodName = "SPortStorageController:add";
        LOGGER.enter(methodName + "[start]", "sPortStorageDTO:" + sPortStorageDTO);

        boolean flag = sPortStorageService.doSave(sPortStorageDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 新建（批量）
     *
     * @param sPortStorageDTOS
     * @return
     */
    @PostMapping("/addList")
    public Map<String, Object> addList(@RequestBody List<SPortStorageDTO> sPortStorageDTOS) {
        final String methodName = "SPortStorageController:addList";
        LOGGER.enter(methodName + "[start]", "sPortStorageDTO:" + sPortStorageDTOS);

        Map<String, Object> resultMap = sPortStorageService.doListSave(sPortStorageDTOS);

        LOGGER.exit(methodName);
        boolean flag = (boolean) resultMap.get("flag");
        String msg = (String) resultMap.get("msg");

        return flag ? Response.SUCCESS.newBuilder().out(msg).toResult(true) :
                Response.FAIL.newBuilder().out(msg).toResult(false);

    }

    /**
     * 修改
     *
     * @param sPortStorageDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody SPortStorageDTO sPortStorageDTO) {
        final String methodName = "SPortStorageController:update";
        LOGGER.enter(methodName + "[start]", "sPortStorageDTO:" + sPortStorageDTO);

        boolean flag = sPortStorageService.doSave(sPortStorageDTO);

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
        final String methodName = "SPortStorageController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = sPortStorageService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 删除
     *
     * @param sPortStorageDTO
     * @return
     */
    @DeleteMapping("/deleteByCondition")
    public Map<String, Object> deleteByCondition(SPortStorageDTO sPortStorageDTO) {
        final String methodName = "SPortStorageController:deleteByCondition";
        LOGGER.enter(methodName + "[start]", "根据条件删除");

        boolean flag = sPortStorageService.deleteByCondition(sPortStorageDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 港存导出
     *
     * @param
     * @param response
     * @return
     */
    @GetMapping("/exportExcel")
    public void exportExcel(SPortStorageSearchDTO searchDTO, HttpServletResponse response) {
        ResponseUtils.compliantWithExcel(response, "港存");
        try {
            byte[] bytes = sPortStorageService.exportExcel(searchDTO);
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
        sPortStorageService.exportTemplate(response);
        return Response.SUCCESS.newBuilder().out("导出成功").toResult();
    }
    /**
     * 批量导入
     * @param file
     * @return
     */
    @PostMapping("/importList")
    public Map<String, Object> importList( MultipartFile file) {
        boolean flag = sPortStorageService.importList(file);
        return Response.SUCCESS.newBuilder().out(flag ? "批量添加成功" : "批量添加失败").toResult(flag);
    }
}

