package com.yy.ppm.finance.controller;

import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.finance.service.TFdCreditDebitBillDetailService;
import com.yy.ppm.finance.bean.dto.TFdCreditDebitBillDetailDTO;
import com.yy.ppm.finance.bean.dto.TFdCreditDebitBillDetailSearchDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 贷方解放票据主表(TFdCreditDebitBillDetail)Controller
 * @Description
 * @createTime 2023年10月08日 16:19:00
 */
@RestController
@RequestMapping("/api/v1/internal/tFdCreditDebitBillDetail")
public class TFdCreditDebitBillDetailController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TFdCreditDebitBillDetailController.class);

    @Autowired
    private TFdCreditDebitBillDetailService tFdCreditDebitBillDetailService;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(TFdCreditDebitBillDetailSearchDTO searchDTO) {

        Pages<TFdCreditDebitBillDetailDTO> pages = tFdCreditDebitBillDetailService.getList(searchDTO);


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

        TFdCreditDebitBillDetailDTO result = tFdCreditDebitBillDetailService.getDetail(id);


        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     *
     * @param tFdCreditDebitBillDetailDTO
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody TFdCreditDebitBillDetailDTO tFdCreditDebitBillDetailDTO) {
        final String methodName = "TFdCreditDebitBillDetailController:add";
        LOGGER.enter(methodName + "[start]", "tFdCreditDebitBillDetailDTO:" + tFdCreditDebitBillDetailDTO);

        boolean flag = tFdCreditDebitBillDetailService.doSave(tFdCreditDebitBillDetailDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改
     *
     * @param tFdCreditDebitBillDetailDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody TFdCreditDebitBillDetailDTO tFdCreditDebitBillDetailDTO) {
        final String methodName = "TFdCreditDebitBillDetailController:update";
        LOGGER.enter(methodName + "[start]", "tFdCreditDebitBillDetailDTO:" + tFdCreditDebitBillDetailDTO);

        boolean flag = tFdCreditDebitBillDetailService.doSave(tFdCreditDebitBillDetailDTO);

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
        final String methodName = "TFdCreditDebitBillDetailController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tFdCreditDebitBillDetailService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

}

