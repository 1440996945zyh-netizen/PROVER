package com.yy.ppm.produce.controller;

import cn.hutool.core.io.IORuntimeException;
import com.yy.common.enums.Response;
import com.yy.common.excel.export.utils.ResponseUtils;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.produce.bean.dto.TPrdHqInOutwareDTO;
import com.yy.ppm.produce.bean.dto.TPrdHqInOutwareSearchDTO;
import com.yy.ppm.produce.service.TPrdHqInOutwareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/internal/inOutwareHq")
public class TPrdHqInOutwareController {
    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TPrdHqInOutwareController.class);

    @Autowired
    private TPrdHqInOutwareService tPrdHqInOutwareService;


    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(TPrdHqInOutwareSearchDTO searchDTO) {
        Pages<TPrdHqInOutwareDTO> pages = tPrdHqInOutwareService.getList(searchDTO);
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
        TPrdHqInOutwareDTO result = tPrdHqInOutwareService.getDetail(id);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

/*    *//**
     * 新建
     *
     * @param tPrdHqInOutwareDTO
     * @return
     *//*
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody TPrdHqInOutwareDTO tPrdHqInOutwareDTO) {
        final String methodName = "MHqStorageStackController:add";
        LOGGER.enter(methodName + "[start]", "mMachineDTO:" + tPrdHqInOutwareDTO);

        boolean flag = tPrdHqInOutwareService.doSave(tPrdHqInOutwareDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }*/

    /**
     * 修改
     *
     * @param tPrdHqInOutwareDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody TPrdHqInOutwareDTO tPrdHqInOutwareDTO) {
        final String methodName = "MHqStorageStackController:update";
        LOGGER.enter(methodName + "[start]", "mMachineDTO:" + tPrdHqInOutwareDTO);

        boolean flag = tPrdHqInOutwareService.doSave(tPrdHqInOutwareDTO);

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
        final String methodName = "MHqStorageStackController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tPrdHqInOutwareService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 导出
     *
     * @param
     * @param response
     * @return
     */
    @GetMapping("/exportExcel")
    public void exportExcel(TPrdHqInOutwareSearchDTO searchDTO, HttpServletResponse response) {
        ResponseUtils.compliantWithExcel(response, "海清出入库");
        try {
            byte[] bytes = tPrdHqInOutwareService.exportExcel(searchDTO);
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
}
