package com.yy.ppm.statement.controller;

import cn.hutool.core.io.IORuntimeException;
import com.yy.common.enums.Response;
import com.yy.common.excel.POIReadUtils;
import com.yy.common.excel.export.ExcelExporter;
import com.yy.common.excel.export.enums.ExcelTemplate;
import com.yy.common.excel.export.utils.ResponseUtils;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.SpringUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.TBusCustomerDTO;
import com.yy.ppm.business.bean.dto.TBusRateDTO;
import com.yy.ppm.statement.bean.dto.*;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementDetailDTO;
import com.yy.ppm.statement.bean.po.TMiscBillingPO;
import com.yy.ppm.statement.service.MiscBillingService;
import com.yy.ppm.statement.service.storageAmountCalculate.StorageAmountCalculateDemoService;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/***
 * 杂项计费controller
 * @author yangcl
 * */
@RestController
@RequestMapping("/api/external/miscbilling")
@Validated
public class MiscBillingController {

    @Autowired
    MiscBillingService miscService;

    private static final MicroLogger LOGGER = new MicroLogger(MiscBillingController.class);

    /**
     * 查询费率列表
     * @return
     */
    @GetMapping("/getratelist")
//    @PreAuthorize("hasAuthority('statement:miscbilling:query')")
    public Map<String, Object> getRateList(TBusRateDTO tBusRateDTO) {
        final String methodName = "MiscBillingController:getRateList";
        LOGGER.enter(methodName, "查询费率列表 ");

        List<TBusRateDTO> result = miscService.getRateList(tBusRateDTO);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().toResult(result);
    }
    @GetMapping("/startCalStorage")
    public Map<String, Object> startCalStorage(StorageDemoDTO dto) {


        Pages<StorageDemoDTO> result =SpringUtils.getBean(StorageAmountCalculateDemoService.class).getList(dto);

        return Response.SUCCESS.newBuilder().toResult(result);
    }
    /**
     * 根据费率id获取作业过程
     * @return
     */
    @GetMapping("/getProcessByRateItemCode")
    public Map<String, Object> getProcessByRateItemCode(String rateId) {
        final String methodName = "MiscBillingController:getProcessByRateItemCode";
        LOGGER.enter(methodName, "查询费率列表 ");

        List<TMiscBillingDTO> result = miscService.getProcessByRateId(rateId);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().toResult(result);
    }


    /**
     * 保存杂项计费信息
     * @return
     */
    @PostMapping("/savemiscbilling")
    public Map<String, Object> saveMiscBilling(@RequestBody @Validated TMiscBillingPO po, BindingResult result) {
        final String methodName = "MiscBillingController:saveMiscBilling";
        LOGGER.enter(methodName, "保存杂项计费信息 ");

        if (result.hasErrors()) {
            String msg = result.getFieldError().getDefaultMessage();
            LOGGER.warn("保存杂项计费信息失败,msg: " + msg);
            return Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0200).out(msg).toResult();
        }

         miscService.saveMiscBilling(po);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }

    /**
     * 保存杂项计费信息
     * @return
     */
    @GetMapping("/getlist")
    public Map<String, Object> getList(MiscSearchDTO dto){
        final String methodName = "MiscBillingController:getList";
        LOGGER.enter(methodName, "查询杂项计费信息 ");

        Pages<TMiscBillingDTO> result = miscService.getList(dto);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().toResult(result);
    }
    /**
     *导出杂项计费信息
     * @return
     */
    @GetMapping("/pageExport")
    public void pageExport(MiscSearchDTO dto, HttpServletResponse response){
        final String methodName = "MiscBillingController:getList";


        try {
            byte[] bytes = miscService.pageExport(dto);;
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

    /**
     * 删除杂项计费信息
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        final String methodName = "MiscBillingController:delete";
        LOGGER.enter(methodName, "删除杂项计费信息 ");

        miscService.delete(id);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 根据id获取杂项计费信息
     * @return
     */
    @GetMapping("/getmiscbyid")
    public Map<String, Object> getMiscById(Long id) {
        final String methodName = "MiscBillingController:getMiscById";
        LOGGER.enter(methodName, "删除杂项计费信息 ");

        TMiscBillingDTO dto = miscService.getMiscById(id);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().toResult(dto);
    }

    /**
     * 审核杂项计费信息
     * @return
     */
    @GetMapping("/publishmisc")
    public Map<String, Object> publishMisc(Long id) {
        final String methodName = "MiscBillingController:publishMisc";
        LOGGER.enter(methodName, "审核杂项计费信息 ");

        miscService.publishMisc(id);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("审核成功").toResult();
    }

    /**
     * 根据id撤销杂项计费信息
     * @return
     */
    @GetMapping("/revokeMisc")
    public Map<String, Object> revokeMisc(Long id) {
        final String methodName = "MiscBillingController:revokeMisc";
        LOGGER.enter(methodName, "撤销杂项计费信息 ");

         miscService.revokeMisc(id);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("撤销成功").toResult();
    }



    /**
     * 杂项计费回执确认通用接口
     * @return
     */
    @GetMapping("/getListForCargo")
    public Map<String, Object> getlistForCargo(MiscSearchDTO dto) {
        final String methodName = "MiscBillingController:getListForCargo";
        LOGGER.enter(methodName, "查询杂项计费信息 ");

        Pages<TMiscBillingDTO> result = miscService.getListForCargo(dto);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 杂项计费
     * @param id
     * @return
     */
    @GetMapping("/charging/{id}")
    public Map<String, Object> charging(@PathVariable Long id) {
        final String methodName = "MiscBillingController:charging";
        LOGGER.enter(methodName, "杂项计费");

        boolean flag = miscService.charging(id);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out(flag?"已成功计费":"计费失败").toResult();
    }
    /**
     * 杂项撤销计费
     * @param id
     * @return
     */
    @GetMapping("/cancleCharging/{id}")
    public Map<String, Object> cancleCharging(@PathVariable Long id) {
        final String methodName = "MiscBillingController:cancleCharging";
        LOGGER.enter(methodName, "撤销杂项计");

        boolean flag = miscService.cancleCharging(id);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out(flag?"已成功取消计费":"取消计费失败").toResult();
    }

    /**
     * 开票申请查询列表
     * @param dto
     * @return
     */
    @GetMapping("/getlistForInvoiceApply")
    public Map<String, Object> getlistForInvoiceApply(MiscSearchDTO dto){
        final String methodName = "MiscBillingController:getlistForInvoiceApply";
        LOGGER.enter(methodName, "开票申请查询 ");

        Pages<TMiscBillingDTO> result = miscService.getlistForInvoiceApply(dto);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 开票申请新增
     */
    @PostMapping("/addInvoiceApply")
    public Map<String, Object> addInvoiceApply(@RequestBody @Validated TMiscBillingPO po, BindingResult result) {
        final String methodName = "MiscBillingController:saveMiscBilling";
        LOGGER.enter(methodName, "保存杂项计费信息 ");

        if (result.hasErrors()) {
            String msg = result.getFieldError().getDefaultMessage();
            LOGGER.warn("保存杂项计费信息失败,msg: " + msg);
            return Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0200).out(msg).toResult();
        }

        miscService.addInvoiceApply(po);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }

    /**
     * 开票申请审核
     * @param id
     * @return
     */
    @GetMapping("/auditInvoiceApply")
    public Map<String, Object> auditInvoiceApply(Long id) {
        final String methodName = "MiscBillingController:auditInvoiceApply";
        LOGGER.enter(methodName, "审核杂项计费信息 ");

        miscService.auditInvoiceApply(id);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("审核成功").toResult();
    }

    /**
     * 开票申请消审
     * @param id
     * @return
     */
    @GetMapping("/removeAuditInvoiceApply")
    public Map<String, Object> removeAuditInvoiceApply(Long id) {
        final String methodName = "MiscBillingController:auditInvoiceApply";
        LOGGER.enter(methodName, "审核杂项计费信息 ");

        miscService.removeAuditInvoiceApply(id);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("审核成功").toResult();
    }

    @PostMapping("/printFeeList")
    public void printFeeList(@RequestBody List<Long> ids, HttpServletResponse response) throws IOException {
        if(CollectionUtils.isEmpty(ids)){
            throw new BusinessRuntimeException("请先选择数据！");
        }
        CostBillDtoSheetTemplate costBillDtoSheetTemplate = miscService.printFeeList(ids);
        if(costBillDtoSheetTemplate==null){
            throw new BusinessRuntimeException("没有要打印的账单");
        }
        byte[] excelBytes = ExcelExporter.newBuilder()
                .templatePath(ExcelTemplate.FEIYONGMINGXIOTHER.getTemplatePath())
                .postHandle(workbook -> {
                    XSSFSheet sheet = workbook.getSheetAt(0);
                    List<TCostStatementDetailDTO> detailList = costBillDtoSheetTemplate.getDetailList();

                    CellCopyPolicy cellCopyPolicy = new CellCopyPolicy();

                    // 移动备注
                    if (detailList.size() > 1) {
                        XSSFCell remarkFromCell = POIReadUtils.getCell(sheet, 10, 0);
                        XSSFCell remarkToCell = POIReadUtils.getCell(sheet, 10 + detailList.size() - 1, 0);
                        remarkToCell.copyCellFrom(remarkFromCell, cellCopyPolicy);
                    }

                    // 合并备注
                    CellRangeAddress cellRangeAddress = new CellRangeAddress(
                            10 + Math.max((detailList.size() - 1), 0),
                            10 + Math.max((detailList.size() - 1), 0), 1, 7);
                    sheet.addMergedRegion(cellRangeAddress);

                    // 合并费用情况
                    cellRangeAddress = new CellRangeAddress(7, 9 + Math.max((detailList.size() - 1), 0), 0, 0);
                    sheet.addMergedRegion(cellRangeAddress);

                    // 合并合计（元）
                    cellRangeAddress = new CellRangeAddress(
                            9 + Math.max((detailList.size() - 1), 0),
                            9 + Math.max((detailList.size() - 1), 0), 2, 3);
                    sheet.addMergedRegion(cellRangeAddress);

                    //设置打印区域
                    workbook.setPrintArea(0,
                            1,7,
                            1,12 + Math.max((detailList.size() - 1), 0));

                })
                .build()
                .exportByTemplate(costBillDtoSheetTemplate);

        ResponseUtils.compliantWithExcel(response, ExcelTemplate.FEIYONGMINGXI.getComment());
        response.getOutputStream().write(excelBytes);
    }

    /**
     * 查询客户开票信息
     * @param id
     * @return
     */
    @GetMapping("/getCustomerInfo/{id}")
    public Map<String, Object> getCustomerInfo(@PathVariable Long id) {
        final String methodName = "MiscBillingController:getCustomerInfo";
        LOGGER.enter(methodName, "杂项计费获取客户开票信息 ");

        TBusCustomerDTO  dto = miscService.getCustomerInfo(id);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().toResult(dto);
    }

}
