package com.yy.ppm.statement.controller;

import cn.hutool.core.io.IORuntimeException;
import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.excel.POIReadUtils;
import com.yy.common.excel.export.ExcelExporter;
import com.yy.common.excel.export.enums.ExcelTemplate;
import com.yy.common.excel.export.utils.ResponseUtils;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.ValidatorUtils;
import com.yy.framework.annotation.Log;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.statement.bean.dto.CostBillDtoSheetTemplate;
import com.yy.ppm.statement.bean.dto.TMiscBillingDTO;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TBusContractDTO;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementDTO;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementDetailDTO;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementQueryDTO;
import com.yy.ppm.statement.service.TBizCostStatementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
 * @Description
 * @Date 2023-09-18 10:37
 */
@RestController
@RequestMapping("/api/external/bizCostStatement")
@Validated
@Tag(name = "计费相关.包干费相关接口")
public class TBizCostStatementController {

    @Autowired
    private TBizCostStatementService tBizCostStatementService;

    /**
     * 结算单列表
     *
     * @param query
     * @param parameter
     * @return
     */
    @GetMapping("/listCostStatement")
    @Log(title = "结算单列表查询", value = OperateTypeEnum.QUERY)
    public Map<String, Object> listCostStatement(TCostStatementQueryDTO query, PageParameter parameter) {
        if (StringUtils.isBlank(query.getType())) {
            throw new BusinessRuntimeException("结算单类型不能为空");
        }
        Pages<TCostStatementDTO> result = tBizCostStatementService.listCostStatement(query, parameter);
        return Response.SUCCESS.newBuilder().toResult(result);
    }
    @GetMapping("/pageExport")
    @Log(title = "结算单列表查询", value = OperateTypeEnum.QUERY)
    public void pageExport(TCostStatementQueryDTO query, PageParameter parameter, HttpServletResponse response) {


        try {
            byte[] bytes = tBizCostStatementService.pageExport(query);;
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
     * 结算单明细列表
     *
     * @param statementId
     * @return
     */
    @GetMapping("/listCostStatementDetail")
    @Log(title = "结算单明细列表查询", value = OperateTypeEnum.QUERY)
    public Map<String, Object> listCostStatementDetail(@NotNull(message = "结算单ID不能为空") Long statementId) {
        List<TCostStatementDetailDTO> result = tBizCostStatementService.listCostStatementDetail(statementId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 结算单ID查可用合同
     *
     * @param statementId
     * @param date
     * @return
     */
    @GetMapping("/listContract")
    @Log(title = "结算单ID查询合同", value = OperateTypeEnum.QUERY)
    public Map<String, Object> listContract(@NotNull(message = "结算单ID不能为空") Long statementId, @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        List<TBusContractDTO> result = tBizCostStatementService.listContract(statementId, date);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 结算单ID查可用合同 非船舶用
     *
     * @param statementId
     * @param date
     * @return
     */
    @GetMapping("/listContractForLULS")
    @Log(title = "结算单ID查询合同(LJLS)", value = OperateTypeEnum.QUERY)
    public Map<String, Object> listContractForLULS(@NotNull(message = "结算单ID不能为空") Long statementId, @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        List<TBusContractDTO> result = tBizCostStatementService.listContractForLULS(statementId, date);
        return Response.SUCCESS.newBuilder().toResult(result);
    }
    /**
     * 结算单ID查可用合同
     *
     * @param statementId
     * @param date
     * @return
     */
    @GetMapping("/listContractDefault")
    @Log(title = "结算单ID查询合同(default)", value = OperateTypeEnum.QUERY)
    public Map<String, Object> listContractDefault(@NotNull(message = "结算单ID不能为空") Long statementId, @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        List<TBusContractDTO> result = tBizCostStatementService.listContractDefault(statementId, date);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 结算
     *
     * @param dto
     * @return
     */
    @PutMapping("/statement")
    @Log(title = "结算", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> statement(@RequestBody TCostStatementDTO dto) {
        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(dto);
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        bean = ValidatorUtils.validator(dto.getDetails());
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }

        tBizCostStatementService.statement(dto);
        return Response.SUCCESS.newBuilder().out("结算成功").toResult();
    }

    /**
     * 撤销结算
     *
     * @param statementId
     * @return
     */
    @PutMapping("/cancelStatement")
    @Log(title = "撤销结算", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> cancelStatement(@NotNull(message = "结算单ID不能为空") Long statementId) {
        tBizCostStatementService.cancelStatement(statementId);
        return Response.SUCCESS.newBuilder().out("撤销成功").toResult();
    }

    /**
     * 审核
     *
     * @param statementId
     * @return
     */
    @PutMapping("/review")
    @Log(title = "计费审核", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> review(@NotNull(message = "结算单ID不能为空") Long statementId) {
        tBizCostStatementService.review(statementId);
        return Response.SUCCESS.newBuilder().out("审核成功").toResult();
    }

    /**
     * 销审
     *
     * @param statementId
     * @return
     */
    @PutMapping("/cancelReview")
    @Log(title = "销审/撤销计费审核", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> cancelReview(@NotNull(message = "结算单ID不能为空") Long statementId) {
        tBizCostStatementService.cancelReview(statementId);
        return Response.SUCCESS.newBuilder().out("销审成功").toResult();
    }

    /**
     * 商务确认
     *
     * @param dto
     * @return
     */
    @PostMapping("/confirm")
    @Log(title = "商务确认", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> confirm(@RequestBody TCostStatementDTO dto) {
        tBizCostStatementService.confirm(dto);
        return Response.SUCCESS.newBuilder().out("商务确认成功").toResult();
    }

    /**
     * 驳回
     *
     * @param dto
     * @return
     */
    @PostMapping("/reject")
    @Log(title = "驳回", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> reject(@RequestBody TCostStatementDTO dto) {
        tBizCostStatementService.reject(dto);
        return Response.SUCCESS.newBuilder().out("驳回成功").toResult();
    }

    /**
     * 取消商务确认
     *
     * @param dto
     * @return
     */
    @PostMapping("/cancelConfirm")
    @Log(title = "取消商务确认", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> cancelConfirm(@RequestBody TCostStatementDTO dto) {
        tBizCostStatementService.cancelConfirm(dto);
        return Response.SUCCESS.newBuilder().out("取消商务确认成功").toResult();
    }
    /**
     * 包干费打印
     *
     * @param dto
     * @return
     */
    @PostMapping("/printCostBill")
    @Log(title = "包干费账单打印", value = OperateTypeEnum.QUERY)
    public void exportByTemplate1(HttpServletResponse response,@RequestBody TCostStatementDTO dto) throws IOException {

        CostBillDtoSheetTemplate costBillDtoSheetTemplate = tBizCostStatementService.exportCostBill(dto);
        if(costBillDtoSheetTemplate==null){
            throw new BusinessRuntimeException("没有要打印的账单");
        }
        byte[] excelBytes = ExcelExporter.newBuilder()
                .templatePath(ExcelTemplate.FEIYONGMINGXI.getTemplatePath())
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
     * 包干费打印批量
     *
     * @param dto
     * @return
     */
    @PostMapping("/printSettlement")
    @Log(title = "包干费账单打印批量", value = OperateTypeEnum.QUERY)
    public void printSettlement(HttpServletResponse response, @RequestBody TCostStatementDTO dto) throws IOException {
        CostBillDtoSheetTemplate costBillDtoSheetTemplate = tBizCostStatementService.exportCostBillBath(dto);
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

            ResponseUtils.compliantWithExcel(response, ExcelTemplate.FEIYONGMINGXIOTHER.getComment());
            response.getOutputStream().write(excelBytes);
    }

    /**
     * 校验一批结算单是不是属于同一个合同
     *
     * @param statementIds
     * @return
     */
    @PostMapping("/contractFlag")
    @Log(title = "校验一批结算单是不是属于同一个合同", value = OperateTypeEnum.QUERY)
    public Map<String, Object> getContractFlag(HttpServletResponse response, @RequestBody List<Long> statementIds) throws IOException {
        boolean flag = tBizCostStatementService.getContractFlag(statementIds);
        return Response.SUCCESS.newBuilder().out(flag?"同一合同":"必须选择同一合同下的数据").toResult(flag);

    }
    /**
     * 文件保存
     *
     * @param dto
     * @return
     */
    @PostMapping("/saveFile")
    @Log(title = "商务确认上传回执单", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> saveFile(@RequestBody TCostStatementDTO dto) {

        tBizCostStatementService.saveFile(dto);

        return Response.SUCCESS.newBuilder().out("文件保存成功").toResult();
    }


    /**
     * 获取预结算量
     *
     * @param statementId
     * @return
     */
    @GetMapping("/getPreNumberCount")
    @Log(title = "获取预结算量", value = OperateTypeEnum.QUERY)
    public Map<String, Object> getPreNumberCount(@NotNull(message = "结算单ID不能为空") Long statementId) {
        TCostStatementDetailDTO result = tBizCostStatementService.getPreNumberCount(statementId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 获取预结算量
     *
     * @param statementId
     * @return
     */
    @GetMapping("/getMiscFee")
    @Log(title = "查询杂项相关的", value = OperateTypeEnum.QUERY)
    public Map<String, Object> getMiscFee(@NotNull(message = "结算单ID不能为空") Long statementId) {
        List<TMiscBillingDTO> result = tBizCostStatementService.getMiscFee(statementId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

}
