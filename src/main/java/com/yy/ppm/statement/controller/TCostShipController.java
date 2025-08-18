package com.yy.ppm.statement.controller;

import com.yy.common.enums.Response;
import com.yy.common.excel.export.ExcelExporter;
import com.yy.common.excel.export.enums.ExcelTemplate;
import com.yy.common.excel.export.utils.ResponseUtils;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.ValidatorUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.po.TBusRatePO;
import com.yy.ppm.statement.bean.dto.costShip.*;
import com.yy.ppm.statement.bean.po.TCostShipPO;
import com.yy.ppm.statement.service.TCostShipService;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-20 11:23
 */
@RestController
@RequestMapping("/api/external/costShip")
@Validated
public class TCostShipController {

    @Autowired
    private TCostShipService tCostShipService;

    /**
     * 船舶航次列表
     *
     * @param query
     * @param parameter
     * @return
     */
    @GetMapping("/listShipvoyage")
    public Map<String, Object> listShipvoyageItem(TDisShipvoyageItemQueryDTO query, PageParameter parameter) {
        Pages<TDisShipvoyageItemDTO> result = tCostShipService.listShipvoyageItem(query, parameter);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 停工记录列表
     *
     * @param shipvoyageId
     * @return
     */
    @GetMapping("/listStopRecord")
    public Map<String, Object> listStopRecord(@NotNull(message = "船舶预报ID不能为空") Long shipvoyageId) {
        List<TDisShipDynamicDTO> result = tCostShipService.listStopRecord(shipvoyageId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 船舶预报ID查费率
     *
     * @param shipvoyageId
     * @return
     */
    @GetMapping("/listRate")
    public Map<String, Object> listRate(@NotNull(message = "船舶预报ID不能为空") Long shipvoyageId) {
        List<TBusRatePO> result = tCostShipService.listRate(shipvoyageId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 结算
     *
     * @param costShips
     * @return
     */
    @PostMapping("/statement")
    public Map<String, Object> statement(@RequestBody List<TCostShipPO> costShips) {
        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(costShips);
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }

        costShips.forEach(v1 -> {
            if (v1.getShipvoyageId() == null) {
                throw new BusinessRuntimeException("船舶预报ID不能为空");
            }
            if (v1.getShipvoyageItemId() == null) {
                throw new BusinessRuntimeException("船舶航次ID不能为空");
            }
        });

        tCostShipService.statement(costShips);
        return Response.SUCCESS.newBuilder().out("结算成功").toResult();
    }

    /**
     * 回显
     *
     * @param shipvoyageItemId
     * @return
     */
    @GetMapping("/listCostShip")
    public Map<String, Object> listCostShip(@NotNull(message = "船舶预报ID不能为空") Long shipvoyageItemId) {
        List<TCostShipPO> costShips = tCostShipService.listCostShip(shipvoyageItemId);
        return Response.SUCCESS.newBuilder().toResult(costShips);
    }

    /**
     * 撤销结算
     *
     * @param shipvoyageItemId
     * @return
     */
    @DeleteMapping("/cancelStatement")
    public Map<String, Object> cancelStatement(@NotNull(message = "船舶预报ID不能为空") Long shipvoyageItemId) {

        tCostShipService.cancelStatement(shipvoyageItemId);
        return Response.SUCCESS.newBuilder().out("撤销成功").toResult();
    }
    /**
     * 导出结算
     *
     * @param shipvoyageItemId
     * @return
     */
    @GetMapping("/exportFee")
    public void exportFee(HttpServletResponse response, @NotNull(message = "没有船舶预报ID") Long shipvoyageId,
                          @NotNull(message = "没有船舶调度ID") Long shipvoyageItemId) throws IOException {

        TCostShipExportDTO tCostShipExportDTO = tCostShipService.exportFee(shipvoyageId, shipvoyageItemId);
        byte[] excelBytes = ExcelExporter.newBuilder()
                .templatePath(ExcelTemplate.TINGBOFEI.getTemplatePath())
                .postHandle(workbook->{
                    XSSFSheet sheet = workbook.getSheetAt(0);
                    List<CostShipDetailExportDTO> detailList = tCostShipExportDTO.getDetailList();
                    //合并计费
                    CellRangeAddress cellRangeAddress =
                            new CellRangeAddress(
                                    5 + Math.max((detailList.size() - 1), 0),
                                    5 + Math.max((detailList.size() - 1), 0),
                                    0, 2);
                    sheet.addMergedRegion(cellRangeAddress);
                    //合并计费人
                    CellRangeAddress cellAddressesJiFeiRen = new CellRangeAddress(
                            6 + Math.max((detailList.size() - 1), 0),
                            6 + Math.max((detailList.size() - 1), 0),
                            0, 2);
                    sheet.addMergedRegion(cellAddressesJiFeiRen);
                    //合并审核人等
                    CellRangeAddress cellRangeAddressShenHe = new CellRangeAddress(
                            6+ Math.max((detailList.size() - 1), 0),
                            6+ Math.max((detailList.size() - 1), 0),
                            3,4);
                    sheet.addMergedRegion(cellRangeAddressShenHe);
                    //设置行高
                    for (int i = 6; i <= 6+Math.max((detailList.size() - 1), 0); i++) {
                        XSSFRow row = sheet.getRow(i);
                        if (row == null) {
                            row = sheet.createRow(i);
                        }
                        row.setHeightInPoints(50); // 设置行高，单位是磅，可以根据需要进行转换
                    }
                    //设置打印区域
                    workbook.setPrintArea(0,
                            0,5,
                            0,6 + Math.max((detailList.size() - 1), 0));
                })
                .build()
                .exportByTemplate(tCostShipExportDTO);

        ResponseUtils.compliantWithExcel(response, ExcelTemplate.TINGBOFEI.getComment());
        response.getOutputStream().write(excelBytes);
    }

    /**
     * 审核
     *
     * @param shipvoyageItemId
     * @return
     */
    @PutMapping("/review")
    public Map<String, Object> review(@NotNull(message = "船舶预报ID不能为空") Long shipvoyageItemId) {
        tCostShipService.review(shipvoyageItemId);
        return Response.SUCCESS.newBuilder().out("审核成功").toResult();
    }

    /**
     * 销审
     *
     * @param shipvoyageItemId
     * @return
     */
    @PutMapping("/cancelReview")
    public Map<String, Object> cancelReview(@NotNull(message = "船舶预报ID不能为空") Long shipvoyageItemId) {
        tCostShipService.cancelReview(shipvoyageItemId);
        return Response.SUCCESS.newBuilder().out("销审成功").toResult();
    }

    /**
     * 查询船方计费表中的非停泊费以及杂项表中的非货方费用
     *
     * @param shipvoyageItemId
     * @return
     */
    @GetMapping("/listOtherCostShip")
    public Map<String, Object> listOtherCostShip(@NotNull(message = "船舶预报ID不能为空") Long shipvoyageItemId) {
        List<TCostShipPO> costShips = tCostShipService.listOtherCostShip(shipvoyageItemId);
        return Response.SUCCESS.newBuilder().toResult(costShips);
    }

    @GetMapping("/getBerthDyas")
    public Map<String,Object> getBerthDyas(@RequestParam("shipvoyageItemId") Long shipvoyageItemId){
        Integer berthDyas = tCostShipService.getBerthDyas(shipvoyageItemId);
        return Response.SUCCESS.newBuilder().toResult(berthDyas);
    }


    @GetMapping("/getSpecialDynamicList")
    public Map<String, Object> getSpecialDynamicList(TDisShipvoyageItemQueryDTO query, PageParameter parameter) {
        List<TDisShipvoyageItemDTO> result = tCostShipService.getSpecialDynamicList(query, parameter);
        return Response.SUCCESS.newBuilder().toResult(result);
    }


}
