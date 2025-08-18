package com.yy.ppm.finance.controller;

import cn.hutool.core.io.IORuntimeException;
import com.yy.common.excel.export.utils.ResponseUtils;
import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.finance.bean.dto.BusTrustResponseDTO;
import com.yy.ppm.finance.bean.dto.TFdBankCustomerPaymentDTO;
import com.yy.ppm.finance.service.TFdBankCustomerPrepaymentService;
import com.yy.ppm.finance.bean.dto.TFdBankCustomerPrepaymentDTO;
import com.yy.ppm.finance.bean.dto.TFdBankCustomerPrepaymentSearchDTO;

import com.yy.ppm.produce.bean.dto.salary.SalaryQueryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 客户预缴(TFdBankCustomerPrepayment)Controller
 * @Description
 * @createTime 2023年09月14日 10:30:00
 */
@RestController
@RequestMapping("/api/v1/internal/tFdBankCustomerPrepayment")
public class TFdBankCustomerPrepaymentController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TFdBankCustomerPrepaymentController.class);

    @Autowired
    private TFdBankCustomerPrepaymentService tFdBankCustomerPrepaymentService;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(TFdBankCustomerPrepaymentSearchDTO searchDTO) {

        Pages<TFdBankCustomerPrepaymentDTO> pages = tFdBankCustomerPrepaymentService.getList(searchDTO);


        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 获取余额列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getBalanceList")
    public Map<String, Object> getBalanceList(TFdBankCustomerPrepaymentSearchDTO searchDTO) {
        final String methodName = "TFdBankCustomerPrepaymentController:getBalanceList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<TFdBankCustomerPrepaymentDTO> pages = tFdBankCustomerPrepaymentService.getBalanceList(searchDTO);

        LOGGER.exit(methodName + "result:" + pages);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }
    /**
     * 获取扣款明细列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getBalanceDetailList")
    public Map<String, Object> getBalanceDetailList(TFdBankCustomerPrepaymentSearchDTO searchDTO) {
        final String methodName = "TFdBankCustomerPrepaymentController:getBalanceDetailList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<TFdBankCustomerPaymentDTO> pages = tFdBankCustomerPrepaymentService.getBalanceDetailList(searchDTO);

        LOGGER.exit(methodName + "result:" + pages);

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
        final String methodName = "TFdBankCustomerPrepaymentController:getDetail";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        TFdBankCustomerPrepaymentDTO result = tFdBankCustomerPrepaymentService.getDetail(id);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     *
     * @param tFdBankCustomerPrepaymentDTO
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody TFdBankCustomerPrepaymentDTO tFdBankCustomerPrepaymentDTO) {
        final String methodName = "TFdBankCustomerPrepaymentController:add";
        LOGGER.enter(methodName + "[start]", "tFdBankCustomerPrepaymentDTO:" + tFdBankCustomerPrepaymentDTO);

        boolean flag = tFdBankCustomerPrepaymentService.doSave(tFdBankCustomerPrepaymentDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改
     *
     * @param tFdBankCustomerPrepaymentDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody TFdBankCustomerPrepaymentDTO tFdBankCustomerPrepaymentDTO) {
        final String methodName = "TFdBankCustomerPrepaymentController:update";
        LOGGER.enter(methodName + "[start]", "tFdBankCustomerPrepaymentDTO:" + tFdBankCustomerPrepaymentDTO);

        boolean flag = tFdBankCustomerPrepaymentService.doSave(tFdBankCustomerPrepaymentDTO);

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
        final String methodName = "TFdBankCustomerPrepaymentController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tFdBankCustomerPrepaymentService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    @PutMapping("/voidHandle")
    public Map<String, Object> voidHandle(@RequestBody TFdBankCustomerPrepaymentDTO tFdBankCustomerPrepaymentDTO) {
        final String methodName = "TFdBankCustomerPrepaymentController:update";
        LOGGER.enter(methodName + "[start]", "tFdBankCustomerPrepaymentDTO:" + tFdBankCustomerPrepaymentDTO);

        boolean flag = tFdBankCustomerPrepaymentService.voidHandle(tFdBankCustomerPrepaymentDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }

    /**
     * 获取作业通知单表格
     */
    @GetMapping("/getBusTrustList")
    public Map<String, Object> getBusTrustList(String keyWord) {
        final String methodName = "TFdBankCustomerPrepaymentController:getBusTrustList";
        LOGGER.enter(methodName + "[start]","keyWord"+keyWord);

        List<BusTrustResponseDTO> responseDTOS = tFdBankCustomerPrepaymentService.getBusTrustList(keyWord);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().toResult(responseDTOS);
    }


    /**
     * 获取客户预缴余额
     */
    @GetMapping("/getBankCustomerPrepayment")
    Map<String,Object> getBankCustomerPrepayment(TFdBankCustomerPrepaymentSearchDTO searchDTO){
        final String methodName = "TFdBankCustomerPrepaymentController:getBankCustomerPrepayment";
        LOGGER.enter(methodName + "[start]","searchDTO"+searchDTO);
        Map<String,Object> map = tFdBankCustomerPrepaymentService.getBankCustomerPrepayment(searchDTO);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().toResult(map);
    }

    /**
     * 获取客户预缴余额
     */
    @GetMapping("/getTrustOrderList")
    Map<String,Object> getTrustOrderList(TFdBankCustomerPrepaymentSearchDTO searchDTO){
        final String methodName = "TFdBankCustomerPrepaymentController:getTrustOrderList";
        LOGGER.enter(methodName + "[start]","searchDTO"+searchDTO);

        List<BusTrustResponseDTO> responseDTOS = tFdBankCustomerPrepaymentService.getTrustOrderList(searchDTO);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().toResult(responseDTOS);
    }
    /**
     * 获取预缴编号
     */
    @GetMapping("/getPrepaymentCodeList")
    Map<String,Object> getPrepaymentCodeList(TFdBankCustomerPrepaymentSearchDTO searchDTO){
        final String methodName = "TFdBankCustomerPrepaymentController:getPrepaymentCodeList";
        LOGGER.enter(methodName + "[start]","获取预缴编号开始");

        List<TFdBankCustomerPrepaymentDTO> responseDTOS = tFdBankCustomerPrepaymentService.getPrepaymentCodeList(searchDTO);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().toResult(responseDTOS);
    }

    /**
     * 获取金额相关信息
     */
    @GetMapping("/getAmountInfo")
    Map<String,Object> getAmountInfo(TFdBankCustomerPrepaymentSearchDTO searchDTO){
        final String methodName = "TFdBankCustomerPrepaymentController:getPrepaymentCodeList";
        LOGGER.enter(methodName + "[start]","获取预缴编号开始");

        Map<String,Object> resultMap = tFdBankCustomerPrepaymentService.getAmountInfo(searchDTO);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().toResult(resultMap);
    }
    /**
     * 客户余额明细导出
     *
     * @param searchDTO
     * @param response
     * @return
     */
    @GetMapping("/exportBalanceDetail")
    public void exportBalanceDetail(TFdBankCustomerPrepaymentSearchDTO searchDTO, HttpServletResponse response) {
        ResponseUtils.compliantWithExcel(response, "客户余额明细");
        try {
            byte[] bytes = tFdBankCustomerPrepaymentService.exportBalanceDetailList(searchDTO);
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

