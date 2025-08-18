package com.yy.ppm.finance.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.framework.annotation.Log;
import com.yy.ppm.business.bean.dto.TBusRateDTO;
import com.yy.ppm.finance.bean.dto.TFdCreditDebitBillDetailDTO;
import com.yy.ppm.finance.bean.dto.TFdInvoiceDTO;
import com.yy.ppm.finance.bean.po.TFdCreditDebitBillDetailPO;
import com.yy.ppm.finance.service.TFdCreditDebitBillService;
import com.yy.ppm.finance.bean.dto.TFdCreditDebitBillDTO;
import com.yy.ppm.finance.bean.dto.TFdCreditDebitBillSearchDTO;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 贷方解放票据主表(TFdCreditDebitBill)Controller
 * @Description
 * @createTime 2023年10月08日 16:19:00
 */
@RestController
@RequestMapping("/api/v1/internal/tFdCreditDebitBill")
@Validated
@Tag(name = "财务管理.借贷票据")
public class TFdCreditDebitBillController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TFdCreditDebitBillController.class);

    @Autowired
    private TFdCreditDebitBillService tFdCreditDebitBillService;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(TFdCreditDebitBillSearchDTO searchDTO) {

        Pages<TFdCreditDebitBillDTO> pages = tFdCreditDebitBillService.getList(searchDTO);
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
        final String methodName = "TFdCreditDebitBillController:getDetail";

        TFdCreditDebitBillDTO result = tFdCreditDebitBillService.getDetail(id);


        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 贷方新建
     *
     * @param dto
     * @return
     */
    @PostMapping("/add")
    @Log(title = "新增借贷票据", value = OperateTypeEnum.INSERT)
    public Map<String, Object> add(@RequestBody TFdCreditDebitBillDTO dto) {
        final String methodName = "TFdCreditDebitBillController:add";
        LOGGER.enter(methodName + "[start]", "tFdCreditDebitBillDTO:" + dto);

        boolean flag = tFdCreditDebitBillService.doSave(dto);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改
     *
     * @param tFdCreditDebitBillDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody TFdCreditDebitBillDTO tFdCreditDebitBillDTO) {
        final String methodName = "TFdCreditDebitBillController:update";
        LOGGER.enter(methodName + "[start]", "tFdCreditDebitBillDTO:" + tFdCreditDebitBillDTO);

        boolean flag = tFdCreditDebitBillService.update(tFdCreditDebitBillDTO);

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
    @Log(title = "删除借贷信息", value = OperateTypeEnum.DELETE)
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "TFdCreditDebitBillController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tFdCreditDebitBillService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 查询发票，贷方发票
     * @param dto
     * @return
     */
    @GetMapping("/getInvoiceList")
    public Map<String, Object> getInvoiceList(TFdCreditDebitBillSearchDTO dto){
        final String methodName = "TFdCreditDebitBillController:getInvoiceList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + dto);

        List<TFdCreditDebitBillDetailDTO> result = tFdCreditDebitBillService.getInvoiceList(dto);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询发票，贷方发票
     * @param dto
     * @return
     */
    @GetMapping("/getRateList")
    public Map<String, Object> getRateList(TFdCreditDebitBillSearchDTO dto){
        final String methodName = "TFdCreditDebitBillController:getInvoiceList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + dto);

        List<TBusRateDTO> result = tFdCreditDebitBillService.getRateList(dto);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 作废
     *
     * @param dto
     * @return
     */
    @GetMapping("/doVoid")
    @Log(title = "作废借贷信息", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> doVoid(@NotNull(message = "请选择作废数据！") TFdCreditDebitBillDTO dto) {
        final String methodName = "TFdCreditDebitBillController:doVoid";
        LOGGER.enter(methodName + "[start]", "id:" + dto);

        boolean flag  = tFdCreditDebitBillService.doVoid(dto);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "作废成功" : "作废失败").toResult();
    }

    /**
     * 计算税率税额
     * @param dto
     * @return
     */
    @PostMapping("/calculate")
    public Map<String, Object> calculate(@RequestBody  TFdCreditDebitBillDetailDTO dto) {
        final String methodName = "TFdCreditDebitBillController:doVoid";
        LOGGER.enter(methodName + "[start]", "id:" + dto);

        TFdCreditDebitBillDetailDTO result = tFdCreditDebitBillService.calculate(dto);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().toResult(result);
    }
}

