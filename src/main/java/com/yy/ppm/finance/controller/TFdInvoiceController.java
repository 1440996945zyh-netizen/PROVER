package com.yy.ppm.finance.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.framework.annotation.Log;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.finance.bean.dto.TFdInvoiceDetailDTO;
import com.yy.ppm.finance.service.TFdInvoiceService;
import com.yy.ppm.finance.bean.dto.TFdInvoiceDTO;
import com.yy.ppm.finance.bean.dto.TFdInvoiceSearchDTO;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 发票表(TFdInvoice)Controller
 * @Description
 * @createTime 2023年09月15日 20:22:00
 */
@RestController
@RequestMapping("/api/v1/internal/tFdInvoice")
@Tag(name = "财务管理.发票管理")
public class TFdInvoiceController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TFdInvoiceController.class);

    @Autowired
    private TFdInvoiceService tFdInvoiceService;


    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    @Log(title ="发票列表查询",value = OperateTypeEnum.QUERY)
    public Map<String, Object> getList(TFdInvoiceSearchDTO searchDTO) {

        Pages<TFdInvoiceDTO> pages = tFdInvoiceService.getList(searchDTO);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/invoiceDownload")
    @Log(title ="发票列表查询",value = OperateTypeEnum.QUERY)
    public Map<String, Object> invoiceDownload(TFdInvoiceSearchDTO searchDTO) {

        String result = tFdInvoiceService.invoiceDownload(searchDTO);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 统计查询条件下的发票总金额
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getCountAmount")
    @Log(title ="统计查询条件下的发票总金额",value = OperateTypeEnum.QUERY)
    public Map<String, Object> getCountAmount(TFdInvoiceSearchDTO searchDTO) {
        final String methodName = "TFdInvoiceController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        TFdInvoiceDTO result = tFdInvoiceService.getCountAmount(searchDTO);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询单条记录
     *
     * @param id
     * @return
     */
    @GetMapping("/getDetail")
    @Log(title ="发票详情查询",value = OperateTypeEnum.QUERY)
    public Map<String, Object> getDetail(@RequestParam("id") Long id) {
        final String methodName = "TFdInvoiceController:getDetail";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        TFdInvoiceDTO result = tFdInvoiceService.getDetail(id);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询单条记录
     * @param id
     * @return
     * sdInvoiceService
     */
    @GetMapping("/redApply")
    @Log(title ="红冲申请",value = OperateTypeEnum.QUERY)
    public Map<String, Object> redApply(Long id,String redStatus,String voidReason) {
//        final String methodName = "TFdInvoiceController:redApply";
//        LOGGER.enter(methodName + "[start]", "id:" + id);
//        Boolean result = sdInvoiceService.redApply(id,redStatus,voidReason);
//        LOGGER.exit(methodName + "result:" + result);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult();
    }

    /**
     * 新建
     * @param tFdInvoiceDTO
     * @return
     */
    @PostMapping("/add")
    @Log(title ="发票新增",value = OperateTypeEnum.QUERY)
    public Map<String, Object> add(@RequestBody TFdInvoiceDTO tFdInvoiceDTO) {
        final String methodName = "TFdInvoiceController:add";
        LOGGER.enter(methodName + "[start]", "tFdInvoiceDTO:" + tFdInvoiceDTO);

        boolean flag = tFdInvoiceService.doSave(tFdInvoiceDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改
     *
     * @param tFdInvoiceDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody TFdInvoiceDTO tFdInvoiceDTO) {
        final String methodName = "TFdInvoiceController:update";
        LOGGER.enter(methodName + "[start]", "tFdInvoiceDTO:" + tFdInvoiceDTO);

        boolean flag = tFdInvoiceService.updateData(tFdInvoiceDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }

    /**
     * 作废
     *
     * @param id
     * @return
     */
    @GetMapping("/voidInvoice/{id}")
    @Log(title ="发票作废",value = OperateTypeEnum.UPDATE)
    public Map<String, Object> voidInvoice(@PathVariable("id") Long id) {
        final String methodName = "TFdInvoiceController:voidInvoice";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tFdInvoiceService.voidInvoice(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    /***
     * 获取结算单详情列表
     * @param searchDTO
     * @return
     */
    @GetMapping("/getStatementList")
    @Log(title ="发票 获取结算单详情",value = OperateTypeEnum.QUERY)
    public Map<String, Object> getStatementList(TFdInvoiceSearchDTO searchDTO) {
        final String methodName = "TFdInvoiceController:getStatementList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<TFdInvoiceDetailDTO> result = tFdInvoiceService.getStatementList(searchDTO);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /***
     * 获取结算单详情列表
     * @param searchDTO
     * @return
     */
    @GetMapping("/updateInvoiceCode")
    @Log(title ="发票更新发票号码",value = OperateTypeEnum.UPDATE)
    public Map<String, Object> updateInvoiceCode(TFdInvoiceDTO searchDTO) {
        final String methodName = "TFdInvoiceController:updateInvoiceCode";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        if(searchDTO == null){
            throw new BusinessRuntimeException("没有要操作的数据");
        }
        if(searchDTO.getId() == null){
            throw new BusinessRuntimeException("发票Id为空");
        }
        boolean flag = tFdInvoiceService.updateInvoiceCode(searchDTO);

        LOGGER.exit(methodName + "result:" + flag);

        return Response.SUCCESS.newBuilder().out(flag ? "更新成功" : "更新失败").toResult();
    }

    /**
     * 通过付款人查询发票抬头
     *
     * @param id
     * @return
     */
    @GetMapping("/getInvoice")
    @Log(title ="发票通过付款人查询发票抬头",value = OperateTypeEnum.QUERY)
    public Map<String, Object> getInvoice(@RequestParam("id") Long id) {
        final String methodName = "TFdInvoiceController:getDetail";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        TFdInvoiceDTO result = tFdInvoiceService.getInvoice(id);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

}

