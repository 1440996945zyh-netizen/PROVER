package com.yy.ppm.statement.controller.storageFee;

import cn.hutool.core.io.IORuntimeException;
import com.yy.common.enums.CommonEnum;
import com.yy.common.enums.Response;
import com.yy.common.excel.POIReadUtils;
import com.yy.common.excel.export.ExcelExporter;
import com.yy.common.excel.export.enums.ExcelTemplate;
import com.yy.common.excel.export.utils.ResponseUtils;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.ValidatorUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.statement.bean.dto.ConfirmForMiscAndStorageDTO;
import com.yy.ppm.statement.bean.dto.CostBillDtoSheetTemplate;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementDetailDTO;
import com.yy.ppm.statement.bean.dto.storageFee.*;
import com.yy.ppm.statement.service.storageFee.StorageFeeService;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/external/storageFee")
@Validated
public class StorageFeeController {

    @Autowired
    private StorageFeeService storageFeeService;

    /**
     * 票货列表
     *
     * @param parameter
     * @param query
     * @return
     */
    @GetMapping("/listCargoInfo")
    public Map<String, Object> listCargoInfo(PageParameter parameter, TBusCargoInfoQueryDTO query) {
        Pages<TBusCargoInfoDTO> result = storageFeeService.listCargoInfo(parameter, query);
        return Response.SUCCESS.newBuilder().toResult(result);
    }
    @GetMapping("/pageExport")
    public void pageExport(PageParameter parameter, TBusCargoInfoQueryDTO query, HttpServletResponse response) {
        try {
            byte[] bytes = storageFeeService.pageExport(query);;
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
     * 计费审核和回执确认的主列表查询
     *
     * @param parameter
     * @param query
     * @return
     */
    @GetMapping("/ListStatementStackFee")
    public Map<String, Object> ListStatementStackFee(PageParameter parameter, TBusCargoInfoQueryDTO query) {
        Pages<TBusCargoInfoDTO> result = storageFeeService.ListStatementStackFee(parameter, query);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 待结算明细列表
     *
     * @param cargoInfoId
     * @param isCalculate
     * @param isFinal
     * @param endDate
     * @param isXC
     * @return
     */
    @GetMapping("/listDetail")
    public Map<String, Object> listDetail(@NotNull(message = "票货ID不能为空") Long cargoInfoId, String isCalculate, String isFinal, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate, String isXC,String isUseReduce,String reduceType) {
        List<TCostStorageSettleDetailDTO> result = storageFeeService.listDetail(cargoInfoId, isCalculate, isFinal, endDate, isXC,isUseReduce,reduceType);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 合同列表
     *
     * @param cargoInfoId
     * @param date
     * @return
     */
    @GetMapping("/listContract")
    public Map<String, Object> listContract(@NotNull(message = "票货ID不能为空") Long cargoInfoId, @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        List<Map<String, Object>> result = storageFeeService.listContract(cargoInfoId, date);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 待结算明细列表（已选合同）
     *
     * @param cargoInfoId
     * @param isCalculate
     * @param isFinal
     * @param endDate
     * @param freeStorageDays
     * @param rate
     * @param tax
     * @param isUseReduce
     * @param isXC
     * @return
     */
    @GetMapping("/listDetailWithContract")
    public Map<String, Object> listDetailWithContract(
            @NotNull(message = "票货ID不能为空") Long cargoInfoId, String isCalculate, String isFinal, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @NotNull(message = "免堆存期不能为空") Integer freeStorageDays, @NotNull(message = "费率不能为空") BigDecimal rate, @NotNull(message = "税率不能为空") BigDecimal tax,
            String isUseReduce, String isXC,String reduceType
    ) {
        List<TCostStorageSettleDetailDTO> result = storageFeeService.listDetailWithContract(cargoInfoId, isCalculate, isFinal, endDate, freeStorageDays, rate, tax, isUseReduce, isXC,reduceType);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 结算
     *
     * @param storageSettle
     * @return
     */
    @PostMapping("/settle")
    public Map<String, Object> settle(@RequestBody TCostStorageSettleDTO storageSettle) {
        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(storageSettle);
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        if (!CommonEnum.YesNoMode.isContains(storageSettle.getIsFinal())) {
            throw new BusinessRuntimeException("错误的是否最终结算编码");
        }
        if (!CommonEnum.YesNoMode.isContains(storageSettle.getIsUseReduce())) {
            throw new BusinessRuntimeException("错误的是否使用减免编码");
        }
        bean = ValidatorUtils.validator(storageSettle.getDetails());
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }

        storageFeeService.settle(storageSettle);
        return Response.SUCCESS.newBuilder().out("结算成功").toResult();
    }

    /**
     * 堆存费结算列表
     *
     * @param cargoInfoId
     * @return
     */
    @GetMapping("/listStorageSettle")
    public Map<String, Object> listStorageSettle(@NotNull(message = "票货ID不能为空") Long cargoInfoId) {
        List<TCostStorageSettleDTO> result = storageFeeService.listStorageSettle(cargoInfoId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 撤销结算
     *
     * @param storageSettleId
     * @return
     */
    @DeleteMapping("/cancelSettle")
    public Map<String, Object> cancelSettle(@NotNull(message = "结算ID不能为空") Long storageSettleId) {
        storageFeeService.cancelSettle(storageSettleId);
        return Response.SUCCESS.newBuilder().out("撤销成功").toResult();
    }

    /**
     * 审核
     *
     * @param storageSettleId
     * @return
     */
    @PutMapping("/review")
    public Map<String, Object> review(@NotNull(message = "结算ID不能为空") Long storageSettleId) {
        storageFeeService.review(storageSettleId);
        return Response.SUCCESS.newBuilder().out("审核成功").toResult();
    }

    /**
     * 销审
     *
     * @param storageSettleId
     * @return
     */
    @PutMapping("/cancelReview")
    public Map<String, Object> cancelReview(@NotNull(message = "结算ID不能为空") Long storageSettleId) {
        storageFeeService.cancelReview(storageSettleId);
        return Response.SUCCESS.newBuilder().out("销审成功").toResult();
    }

    /**
     * 销审
     *
     * @param dto
     * @return
     */
    @PostMapping("/uploadConfirmFile")
    public Map<String, Object> uploadConfirmFile(@RequestBody ConfirmForMiscAndStorageDTO dto) {
        storageFeeService.saveConfirmFile(dto);
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }

    /**
     * 销审
     *
     * @param dto
     * @return
     */
    @PostMapping("/confirm")
    public Map<String, Object> confirm(@RequestBody ConfirmForMiscAndStorageDTO dto) {
        storageFeeService.confirm(dto);
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }

    /**
     * 销审
     *
     * @param dto
     * @return
     */
    @PostMapping("/cancelConfirm")
    public Map<String, Object> cancelConfirm(@RequestBody ConfirmForMiscAndStorageDTO dto) {
        storageFeeService.cancelConfirm(dto);
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }

    /**
     * 堆存费详情
     *
     * @param storageSettleId
     * @return
     */
    @GetMapping("/listStorageSettleById")
    public Map<String, Object> listStorageSettleById(@NotNull(message = "ID不能为空") Long storageSettleId) {
        List<TCostStorageSettleDTO> result = storageFeeService.listStorageSettleById(storageSettleId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }
    /**
     * 堆存费打印账单
     *
     * @param dto
     * @param response
     * @throws IOException
     */
    @PostMapping("/printFeeList")
    public void printFeeList(@RequestBody TReqStorageStatementExportDTO dto, HttpServletResponse response) throws IOException {
        if (CollectionUtils.isEmpty(dto.getIds())) {
            throw new BusinessRuntimeException("请先选择数据！");
        }
        CostBillDtoSheetTemplate costBillDtoSheetTemplate = storageFeeService.printFeeList(dto);
        if (costBillDtoSheetTemplate == null) {
            throw new BusinessRuntimeException("没有要打印的账单");
        }
        byte[] excelBytes = ExcelExporter.newBuilder()
                .templatePath(ExcelTemplate.FEIYONGMINGXISTACK.getTemplatePath())
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
                            10 + Math.max((detailList.size() - 1), 0), 1, 6);
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
                            0, 10,
                            0, 12  + Math.max((detailList.size() - 1), 0));

                })
                .build()
                .exportByTemplate(costBillDtoSheetTemplate);

        ResponseUtils.compliantWithExcel(response, ExcelTemplate.FEIYONGMINGXIOTHER.getComment());
        response.getOutputStream().write(excelBytes);
    }

    /**
     * 获取客户的开票信息
     *
     * @param id
     * @return
     */
    @GetMapping("/getTaxInvoiceCode")
    public Map<String, Object> getTaxInvoiceCode(@NotNull(message = "ID不能为空") Long id) {
        TBusCargoInfoDTO result = storageFeeService.getTaxInvoiceCode(id);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    /**
     * 获取票货混配信息
     *
     * @param cargoInfoId
     * @return
     */
    @GetMapping("/getMixRecordList/{cargoInfoId}")
    public Map<String, Object> getMixRecordList(@NotNull(message = "票货id不能为空") @PathVariable Long cargoInfoId) {
        List<Map<String,Object>> result = storageFeeService.getMixRecordList(cargoInfoId);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 详情导出
     *
     * @param dto
     * @return
     */
    @PostMapping("/storageCostDetailExport")
    public void storageCostDetailExport(@RequestBody TStorageCostDetailExportDTO dto, HttpServletResponse response ) throws IOException {
        storageFeeService.exportDetail(dto,response);
    }

    /**
     * 详情导出
     *
     * @param cargoInfoId
     * @return
     */
    @GetMapping("/getHandoverlistTon")
    public Map<String, Object> storageCostDetailExport(@NotNull(message = "cargoInfoId不能为空") Long cargoInfoId ) {
        BigDecimal result =  storageFeeService.getHandoverlistTon(cargoInfoId);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

}
