package com.yy.ppm.statement.controller.storageSettle;

import com.yy.common.enums.Response;
import com.yy.common.excel.POIReadUtils;
import com.yy.common.excel.export.ExcelExporter;
import com.yy.common.excel.export.enums.ExcelTemplate;
import com.yy.common.excel.export.utils.ResponseUtils;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.ValidatorUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.enums.BusHandoverlistTypeEnum;
import com.yy.ppm.statement.bean.dto.ConfirmForMiscAndStorageDTO;
import com.yy.ppm.statement.bean.dto.CostBillDtoSheetTemplate;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementDetailDTO;
import com.yy.ppm.statement.bean.dto.storageSettle.TBusHandoverlistDTO;
import com.yy.ppm.statement.bean.dto.storageSettle.TBusHandoverlistQueryDTO;
import com.yy.ppm.statement.bean.dto.storageSettle.TCostStorageSettleDTO;
import com.yy.ppm.statement.bean.dto.storageSettle.TCostStorageSettleDetailDTO;
import com.yy.ppm.statement.service.storageSettle.StorageSettleService;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description 堆存费结算
 * @Date 2023-11-24 8:58
 */
@RestController
@RequestMapping("/api/external/storageSettle")
@Validated
public class StorageSettleController {

    @Autowired
    private StorageSettleService service;

    /**
     * 交接清单列表
     *
     * @param parameter
     * @param query
     * @return
     */
    @GetMapping("/listHandoverlist")
    public Map<String, Object> listHandoverlist(PageParameter parameter, TBusHandoverlistQueryDTO query) {
        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(query);
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        if (!BusHandoverlistTypeEnum.contains(query.getType())) {
            throw new BusinessRuntimeException("错误的类型");
        }

        Pages<TBusHandoverlistDTO> result = service.listHandoverlist(parameter, query);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 待结算明细列表
     *
     * @param handoverlistId
     * @return
     */
    @GetMapping("/listDetail")
    public Map<String, Object> listDetail(@NotNull(message = "交接清单ID不能为空") Long handoverlistId) {
        List<TCostStorageSettleDetailDTO> result = service.listDetail(handoverlistId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 合同列表
     *
     * @param handoverlistId
     * @param date
     * @return
     */
    @GetMapping("/listContract")
    public Map<String, Object> listContract(@NotNull(message = "交接清单ID不能为空") Long handoverlistId, @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        List<Map<String, Object>> result = service.listContract(handoverlistId, date);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 待结算明细列表（已选合同）
     *
     * @param handoverlistId
     * @param contractRateId
     * @param isUseReduce
     * @return
     */
    @GetMapping("/listDetailWithContract")
    public Map<String, Object> listDetailWithContract(@NotNull(message = "交接清单ID不能为空") Long handoverlistId, @NotNull(message = "合同费率ID不能为空") Long contractRateId, String isUseReduce) {
        List<TCostStorageSettleDetailDTO> result = service.listDetailWithContract(handoverlistId, contractRateId, isUseReduce);
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
        bean = ValidatorUtils.validator(storageSettle.getDetails());
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }

        service.settle(storageSettle);
        return Response.SUCCESS.newBuilder().out("结算成功").toResult();
    }

    /**
     * 堆存费结算列表
     *
     * @param handoverlistId
     * @return
     */
    @GetMapping("/listStorageSettle")
    public Map<String, Object> listStorageSettle(@NotNull(message = "交接清单ID不能为空") Long handoverlistId) {
        List<TCostStorageSettleDTO> result = service.listStorageSettle(handoverlistId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 取消结算
     *
     * @param storageSettleId
     * @return
     */
    @DeleteMapping("/cancelSettle")
    public Map<String, Object> cancelSettle(@NotNull(message = "结算ID不能为空") Long storageSettleId) {
        service.cancelSettle(storageSettleId);
        return Response.SUCCESS.newBuilder().out("取消成功").toResult();
    }

    /**
     * 审核
     *
     * @param storageSettleId
     * @return
     */
    @PutMapping("/review")
    public Map<String, Object> review(@NotNull(message = "结算ID不能为空") Long storageSettleId) {
        service.review(storageSettleId);
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
        service.cancelReview(storageSettleId);
        return Response.SUCCESS.newBuilder().out("销审成功").toResult();
    }

    /**
     * 堆存费商务确认查询列表
     *
     * @param handoverlistId
     * @return
     */
    @GetMapping("/listStorageSettleForConfirm")
    public Map<String, Object> listStorageSettleForConfirm(@NotNull(message = "交接清单ID不能为空") Long handoverlistId) {
        List<TCostStorageSettleDTO> result = service.listStorageSettleForConfirm(handoverlistId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }


    /**
     * 销审
     *
     * @param dto
     * @return
     */
    @PostMapping("/uploadConfirmFile")
    public Map<String, Object> uploadConfirmFile(@RequestBody ConfirmForMiscAndStorageDTO dto) {
        service.saveConfirmFile(dto);
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
        service.confirm(dto);
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
        service.cancelConfirm(dto);
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }

    /**
     * 堆存费打印账单
     *
     * @param ids
     * @param response
     * @throws IOException
     */
    @PostMapping("/printFeeList")
    public void printFeeList(@RequestBody List<Long> ids, HttpServletResponse response) throws IOException {
        if (CollectionUtils.isEmpty(ids)) {
            throw new BusinessRuntimeException("请先选择数据！");
        }
        CostBillDtoSheetTemplate costBillDtoSheetTemplate = service.printFeeList(ids);
        if (costBillDtoSheetTemplate == null) {
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
                            1, 7,
                            1, 12 + Math.max((detailList.size() - 1), 0));

                })
                .build()
                .exportByTemplate(costBillDtoSheetTemplate);

        ResponseUtils.compliantWithExcel(response, ExcelTemplate.FEIYONGMINGXIOTHER.getComment());
        response.getOutputStream().write(excelBytes);
    }

}
