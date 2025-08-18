package com.yy.ppm.finance.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.finance.bean.dto.TFdBankPayDTO;
import com.yy.ppm.finance.bean.dto.TFdBankPaySearchDTO;
import com.yy.ppm.finance.service.TFdBankPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author rzg
 * @version 1.0.0
 * @ClassName 付款银行维护(TFdBankPay)Controller
 * @Description
 * @createTime 2023年09月13日 16:23:00
 */
@RestController
@RequestMapping("/api/v1/internal/tFdBankPay")
public class TFdBankPayController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TFdBankPayController.class);

    @Autowired
    private TFdBankPayService tFdBankPayService;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(TFdBankPaySearchDTO searchDTO) {

        Pages<TFdBankPayDTO> pages = tFdBankPayService.getList(searchDTO);


        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 查询单条记录
     *
     * @param id
     * @return
     */
    @GetMapping("/getDetail/{id}")
    public Map<String, Object> getDetail(@PathVariable("id") Long id) {
        final String methodName = "TFdBankPayController:getDetail";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        TFdBankPayDTO result = tFdBankPayService.getDetail(id);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     *
     * @param tFdBankPayDTO
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody TFdBankPayDTO tFdBankPayDTO) {
        final String methodName = "TFdBankPayController:add";
        LOGGER.enter(methodName + "[start]", "tFdBankPayDTO:" + tFdBankPayDTO);

        boolean flag = tFdBankPayService.doSave(tFdBankPayDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改
     *
     * @param tFdBankPayDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody TFdBankPayDTO tFdBankPayDTO) {
        final String methodName = "TFdBankPayController:update";
        LOGGER.enter(methodName + "[start]", "tFdBankPayDTO:" + tFdBankPayDTO);

        boolean flag = tFdBankPayService.doSave(tFdBankPayDTO);

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
        final String methodName = "TFdBankPayController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tFdBankPayService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }


    @GetMapping("/getSelectList")
    public Map<String, Object> getSelectList() {
        final String methodName = "TFdBankPayController:getSelectList";
        LOGGER.enter(methodName + "[start]");

        List<Map<String, Object>>  result= tFdBankPayService.getSelectList();

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
}

