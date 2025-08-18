package com.yy.ppm.finance.controller;

import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.finance.service.TFdDebtorpaymentDetailService;
import com.yy.ppm.finance.bean.dto.TFdDebtorpaymentDetailDTO;
import com.yy.ppm.finance.bean.dto.TFdDebtorpaymenDetailSearchDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 收据主表(TFdDebtorpaymenDetail)Controller
 * @Description
 * @createTime 2023年09月20日 11:44:00
 */
@RestController
@RequestMapping("/api/v1/internal/tFdDebtorpaymenDetail")
public class TFdDebtorpaymentDetailController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TFdDebtorpaymentDetailController.class);

    @Autowired
    private TFdDebtorpaymentDetailService tFdDebtorpaymentDetailService;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(TFdDebtorpaymenDetailSearchDTO searchDTO) {

        Pages<TFdDebtorpaymentDetailDTO> pages = tFdDebtorpaymentDetailService.getList(searchDTO);


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
        final String methodName = "TFdDebtorpaymenDetailController:getDetail";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        TFdDebtorpaymentDetailDTO result = tFdDebtorpaymentDetailService.getDetail(id);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     *
     * @param tFdDebtorpaymenDetailDTO
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody TFdDebtorpaymentDetailDTO tFdDebtorpaymenDetailDTO) {
        final String methodName = "TFdDebtorpaymenDetailController:add";
        LOGGER.enter(methodName + "[start]", "tFdDebtorpaymenDetailDTO:" + tFdDebtorpaymenDetailDTO);

        boolean flag = tFdDebtorpaymentDetailService.doSave(tFdDebtorpaymenDetailDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改
     *
     * @param tFdDebtorpaymenDetailDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody TFdDebtorpaymentDetailDTO tFdDebtorpaymenDetailDTO) {
        final String methodName = "TFdDebtorpaymenDetailController:update";
        LOGGER.enter(methodName + "[start]", "tFdDebtorpaymenDetailDTO:" + tFdDebtorpaymenDetailDTO);

        boolean flag = tFdDebtorpaymentDetailService.doSave(tFdDebtorpaymenDetailDTO);

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
        final String methodName = "TFdDebtorpaymenDetailController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tFdDebtorpaymentDetailService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

}

