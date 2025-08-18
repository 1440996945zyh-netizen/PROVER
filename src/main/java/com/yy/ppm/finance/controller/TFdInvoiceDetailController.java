package com.yy.ppm.finance.controller;

import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.finance.service.TFdInvoiceDetailService;
import com.yy.ppm.finance.bean.dto.TFdInvoiceDetailDTO;
import com.yy.ppm.finance.bean.dto.TFdInvoiceDetailSearchDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 发票子表(TFdInvoiceDetail)Controller
 * @Description
 * @createTime 2023年09月15日 20:22:00
 */
@RestController
@RequestMapping("/api/v1/internal/tFdInvoiceDetail")
public class TFdInvoiceDetailController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TFdInvoiceDetailController.class);

    @Autowired
    private TFdInvoiceDetailService tFdInvoiceDetailService;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(TFdInvoiceDetailSearchDTO searchDTO) {

        Pages<TFdInvoiceDetailDTO> pages = tFdInvoiceDetailService.getList(searchDTO);


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

        TFdInvoiceDetailDTO result = tFdInvoiceDetailService.getDetail(id);


        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     *
     * @param tFdInvoiceDetailDTO
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody TFdInvoiceDetailDTO tFdInvoiceDetailDTO) {
        final String methodName = "TFdInvoiceDetailController:add";
        LOGGER.enter(methodName + "[start]", "tFdInvoiceDetailDTO:" + tFdInvoiceDetailDTO);

        boolean flag = tFdInvoiceDetailService.doSave(tFdInvoiceDetailDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改
     *
     * @param tFdInvoiceDetailDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody TFdInvoiceDetailDTO tFdInvoiceDetailDTO) {
        final String methodName = "TFdInvoiceDetailController:update";
        LOGGER.enter(methodName + "[start]", "tFdInvoiceDetailDTO:" + tFdInvoiceDetailDTO);

        boolean flag = tFdInvoiceDetailService.doSave(tFdInvoiceDetailDTO);

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
        final String methodName = "TFdInvoiceDetailController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tFdInvoiceDetailService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 新建
     *
     * @param tFdInvoiceDetailDTO
     * @return
     */
    @PostMapping("/calculateAmount")
    public Map<String, Object> calculateAmount(@RequestBody TFdInvoiceDetailDTO tFdInvoiceDetailDTO) {
        final String methodName = "TFdInvoiceDetailController:calculateAmount";
        LOGGER.enter(methodName + "[start]", "tFdInvoiceDetailDTO:" + tFdInvoiceDetailDTO);

        TFdInvoiceDetailDTO result = tFdInvoiceDetailService.calculateAmount(tFdInvoiceDetailDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().toResult(result);

    }

}

