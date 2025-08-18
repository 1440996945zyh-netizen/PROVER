package com.yy.ppm.dispatch.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.excel.POIReadUtils;
import com.yy.common.excel.export.ExcelExporter;
import com.yy.common.excel.export.enums.ExcelTemplate;
import com.yy.common.excel.export.utils.ResponseUtils;
import com.yy.framework.annotation.Log;
import com.yy.ppm.dispatch.bean.dto.*;
import com.yy.ppm.statement.bean.dto.CostBillDtoSheetTemplate;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementDetailDTO;
import com.yy.ppm.statement.bean.dto.costShip.CostShipDetailExportDTO;
import com.yy.ppm.statement.bean.dto.costShip.TCostShipExportDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.dispatch.service.TDisPortDaynightplanService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;

/**
 * @ClassName 集疏港昼夜计划(TDisPortDaynightplan)Controller
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年11月14日 10:31:00
 */
@RestController
@RequestMapping("/api/v1/internal/tDisPortDaynightplan")
@Tag(name = "商务管理.集疏港昼夜计划")
public class TDisPortDaynightplanController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(TDisPortDaynightplanController.class);

    @Autowired
    private TDisPortDaynightplanService tDisPortDaynightplanService;

    /**
     * 按日期查询昼夜计划详情
     * @param query
     * @return
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('dispatch:portDayNightPlan:query')")
    public Map<String, Object> getList(TDisPortDaynightplanSearch2DTO query) {
//        final String methodName = "TDisPortDaynightplanController:getList";
//		LOGGER.enter(methodName + "集疏港昼夜计划查询 [start]", "date:" + query );

        List<TDisPortDaynightplanDTO> result = tDisPortDaynightplanService.getList(query);

//        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    /**
     * 按日期查询昼夜计划详情
     * @param
     * @return
     */
    @GetMapping("/importYesterdayPlan")
    @PreAuthorize("hasAuthority('dispatch:portDayNightPlan:importTodayPlan')")
    public Map<String, Object> importYesterdayPlan(String planDate,String businessNo) {
//        final String methodName = "TDisPortDaynightplanController:getList";
        List<TDisPortDaynightplanDTO> result = tDisPortDaynightplanService.importYesterdayPlan(planDate,businessNo);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    @GetMapping("/approve/{id}")
    @PreAuthorize("hasAuthority('dispatch:portDayNightPlan:approve')")
    public Map<String, Object> approve(@PathVariable("id") Long id) {
//        final String methodName = "TDisPortDaynightplanController:check";
//        LOGGER.enter(methodName + "[start]", "id:" + id);

        if (id == null) {
            throw new BusinessRuntimeException("请选择要操作的数据");
        }
        boolean flag = tDisPortDaynightplanService.approveById(id);

//        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "审核成功" : "审核失败").toResult();
    }
    @GetMapping("/approveRevoke/{id}")
    @PreAuthorize("hasAuthority('dispatch:portDayNightPlan:approve')")
    public Map<String, Object> approveRevoke(@PathVariable("id") Long id) {
//        final String methodName = "TDisPortDaynightplanController:checkRevoke";
//        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tDisPortDaynightplanService.revokeById(id);

//        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "审核成功" : "审核失败").toResult();
    }
    /**
     * 批量审核
     * @param list
     * @return
     */
    @PostMapping("/approveList")
    @PreAuthorize("hasAuthority('dispatch:portDayNightPlan:approve')")
    @Log(title="审核集疏港昼夜计划",value= OperateTypeEnum.UPDATE)
    public Map<String, Object> approveList(@RequestBody  List<TDisPortDaynightplanDTO> list) {
//        final String methodName = "TDisPortDaynightplanController:approveList";
//        LOGGER.enter(methodName + "[start]", "dto:" +  list);

        tDisPortDaynightplanService.approveList(list);

//        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("操作成功").toResult();
    }

    /**
     * 批量销审
     * @param list
     * @return
     */
    @PostMapping("/approveListRevoke")
    @PreAuthorize("hasAuthority('dispatch:portDayNightPlan:approve')")
    @Log(title="销审集疏港昼夜计划",value= OperateTypeEnum.UPDATE)
    public Map<String, Object> approveListRevoke(@RequestBody  List<TDisPortDaynightplanDTO> list) {
//        final String methodName = "TDisPortDaynightplanController:approveListRevoke";
//        LOGGER.enter(methodName + "[start]", "dto:" +  list);

        tDisPortDaynightplanService.approveListRevoke(list);

//        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("操作成功").toResult();
    }
/*

    *//**
     * 导入昨日计划到今日并返回
     * @param planDate
     * @return
     *//*
    @GetMapping("/importTodayPlan")
    @PreAuthorize("hasAuthority('dispatch:portDayNightPlan:importTodayPlan')")
    public Map<String, Object> importTodayPlan(String planDate) {
        final String methodName = "TDisPortDaynightplanController:importTodayPlan";
        LOGGER.enter(methodName + "集疏港昼夜今日计划导入查询 [start]", "date:" + planDate);

        List<TDisPortDaynightplanDTO> result = tDisPortDaynightplanService.importTodayPlan(planDate);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }*/

    /**
     * 导入计划返回计划量、过磅量、计划剩余量
     * @param
     * @return
     */
    @GetMapping("/getCount")
    public Map<String, Object> getCount(String businessNo) {
//        final String methodName = "TDisPortDaynightplanController:getCount";
//        LOGGER.enter(methodName + "导入计划返回计划量、过磅量、计划剩余量 [start]", "date:" + businessNo);

        TDisPortDaynightplanDTO dto = tDisPortDaynightplanService.getCount(businessNo);

//        LOGGER.exit( methodName + "result:" + dto);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(dto);
    }

    /**
     * 保存
     * @param list 计划内容
     * @param planDate 计划日期
     * @return
     */
    @PostMapping("/doSave")
    @PreAuthorize("hasAuthority('dispatch:portDayNightPlan:save')")
    @Log(title="新增集疏港昼夜计划",value= OperateTypeEnum.INSERT)
    public Map<String, Object> doSave(@RequestBody List<TDisPortDaynightplanDTO> list, String planDate) {
//        final String methodName = "TDisPortDaynightplanController:doSave";
//		LOGGER.enter(methodName + " 集疏港昼夜计划保存[start]", "planDate:" +  planDate);

        boolean flag = tDisPortDaynightplanService.doSave(list, planDate);

//        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "保存成功" : "保存失败").toResult();

    }
    /**
     * 查看当日未做计划的票货详情
     * @param dto
     * @return
     */
    @GetMapping("/getTrustCargoDetail")
    @PreAuthorize("hasAuthority('dispatch:portDayNightPlan:query')")
    public Map<String, Object> getTrustCargoDetail(TDisPortDaynightplanDTO dto) {
//        final String methodName = "TDisPortDaynightplanController:getTrustCargoDetail";
//        LOGGER.enter(methodName + "集疏港昼夜计划票货查询 [start]", "date:" + dto.getPlanDate());

        List<TDisPortDaynightplanDTO> result = tDisPortDaynightplanService.getTrustCargoDetail(dto);

//        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('dispatch:portDayNightPlan:delete')")
    @Log(title="删除集疏港昼夜计划",value= OperateTypeEnum.DELETE)
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
//        final String methodName = "TDisPortDaynightplanController:deleteById";
//		LOGGER.enter(methodName + " 调度昼夜计划删除 [start]", "id:" + id);

        boolean flag = tDisPortDaynightplanService.deleteById(id);

//        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 同步集疏港昼夜计划开始、结束时间到渤海通、车辆集疏港预约表
     * @param id
     * @return
     */
    @PutMapping("/tosToBoHaiTongDayNightPlan")
    @PreAuthorize("hasAuthority('dispatch:portDayNightPlan:tosToBoHaiTong')")
    public Map<String, Object> tosToBoHaiTongDayNightPlan(Long id) {
//        final String methodName = "TDisPortDaynightplanController:tosToBoHaiTongDayNightPlanTask";
//		LOGGER.enter(methodName + " 同步集疏港昼夜计划开始、结束时间到渤海通、车辆集疏港预约表[start]", "id:" +  id);

        tDisPortDaynightplanService.tosToBoHaiTongDayNightPlanTask(id);

//        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out("同步完成！").toResult();
    }


    /**
     * 导出结算
     *
     * @param planDate
     * @return
     */
    @GetMapping("/exportPlan")
    public void exportPlan(HttpServletResponse response, @NotNull(message = "没有日期") String planDate) throws IOException {

        List<TDisPortDaynightplanExportDTO> tDisPortDaynightplanExportDTO = tDisPortDaynightplanService.exportPlan(planDate);
        byte[] excelBytes = ExcelExporter.newBuilder()
                .templatePath(ExcelTemplate.DAYNIGHTPLAN.getTemplatePath())
                .postHandle(workbook->{
                    int index = 0;
                    for (TDisPortDaynightplanExportDTO dto : tDisPortDaynightplanExportDTO) {
                        XSSFSheet sheet = workbook.getSheetAt(index++);
                        CellCopyPolicy cellCopyPolicy = new CellCopyPolicy();
                        List<TDisPortDaynightplanEastExportDTO> detailList1 = dto.getDetailList1();
                        List<TDisPortDaynightplanEastExportDTO> detailList2 = dto.getDetailList2();
                        List<TDisPortDaynightplanEastExportDTO> detailList3 = dto.getDetailList3();
                        //移動西作業區
                        XSSFCell remarkFromCellXi = null;
                        XSSFCell remarkToCellXi = null;

                        remarkFromCellXi = POIReadUtils.getCell(sheet, 4, 0);
                        remarkToCellXi = POIReadUtils.getCell(sheet,
                                4+Math.max(detailList1.size()-1,0)+Math.max(detailList2.size()-1,0)
                                , 0);


                        //移動西
                        if(!CollectionUtils.isEmpty(detailList3)&&detailList3.size()>1){
                            remarkToCellXi.copyCellFrom(remarkFromCellXi, cellCopyPolicy);
                            //合并西
                            CellRangeAddress cellRangeAddressXi = new CellRangeAddress(
                                    4+Math.max(detailList1.size()-1,0)+Math.max(detailList2.size()-1,0),
                                    5+Math.max(detailList1.size()-1,0)+Math.max(detailList2.size()-1,0)+Math.max(detailList3.size()-1,0), 0, 0);
                            sheet.addMergedRegion(cellRangeAddressXi);


                        }
                        //移動中
                        XSSFCell remarkFromCellZhong = null;
                        XSSFCell remarkToCellZhong = null;

                        if (!CollectionUtils.isEmpty(detailList2)&&detailList2.size() > 1) {
                            remarkFromCellZhong = POIReadUtils.getCell(sheet, 2, 0);
                            remarkToCellZhong = POIReadUtils.getCell(sheet, 2+Math.max(detailList1.size()-1,0) , 0);
                        }
                        if(!CollectionUtils.isEmpty(detailList2)&&detailList2.size()>1){
//                            remarkFromCellZhong.copyCellFrom(remarkToCellZhong, cellCopyPolicy);
                            remarkToCellZhong.copyCellFrom(remarkFromCellZhong, cellCopyPolicy);
                            // 合并中作业区
                            CellRangeAddress cellRangeAddress = new CellRangeAddress(
                                    2+Math.max(CollectionUtils.isEmpty(detailList1)?0:detailList1.size()-1,0),
                                    3+Math.max(CollectionUtils.isEmpty(detailList1)?0:detailList1.size()-1,0)+Math.max(detailList2.size()-1,0),
                                    0, 0);
                            sheet.addMergedRegion(cellRangeAddress);


                        }
                        if(!CollectionUtils.isEmpty(detailList1) && detailList1.size()>1){
                            // 合并东作业区
                            CellRangeAddress cellRangeAddress = new CellRangeAddress(
                                    1,
                                    1 + Math.max((CollectionUtils.isEmpty(detailList1)?0:detailList1.size() - 1), 0),
                                    0, 0);
                            sheet.addMergedRegion(cellRangeAddress);


                        }
                        //合并全部的
                        //合并中汇总
                        CellRangeAddress cellRangeAddressHuizong = new CellRangeAddress(
                                3+Math.max(CollectionUtils.isEmpty(detailList1)?0:detailList1.size()-1,0)+Math.max(detailList2.size()-1,0),
                                3+Math.max(CollectionUtils.isEmpty(detailList1)?0:detailList1.size()-1,0)+Math.max(detailList2.size()-1,0),
                                8, 9);
                        sheet.addMergedRegion(cellRangeAddressHuizong);

                        //合并西汇总
                        CellRangeAddress cellRangeAddressXihuizong = new CellRangeAddress(
                                5+Math.max(detailList1.size()-1,0)+Math.max(detailList2.size()-1,0)+Math.max(detailList3.size()-1,0),
                                5+Math.max(detailList1.size()-1,0)+Math.max(detailList2.size()-1,0)+Math.max(detailList3.size()-1,0),
                                8, 9);
                        sheet.addMergedRegion(cellRangeAddressXihuizong);
                        //合并汇总
                        CellRangeAddress cellRangeAddressAllHuizong = new CellRangeAddress(
                                6+Math.max(detailList1.size()-1,0)+Math.max(detailList2.size()-1,0)+Math.max(detailList3.size()-1,0),
                                6+Math.max(detailList1.size()-1,0)+Math.max(detailList2.size()-1,0)+Math.max(detailList3.size()-1,0),
                                8, 9);
                        sheet.addMergedRegion(cellRangeAddressAllHuizong);
                    }

                   /*List<TDisPortDaynightplanEastExportDTO> detailList1 = tDisPortDaynightplanExportDTO.get(0).getDetailList1();
                    List<TDisPortDaynightplanEastExportDTO> detailList2 = tDisPortDaynightplanExportDTO.get(0).getDetailList2();
                    List<TDisPortDaynightplanEastExportDTO> detailList3 = tDisPortDaynightplanExportDTO.get(0).getDetailList3();
                    XSSFSheet sheet = workbook.getSheetAt(0);
                    CellCopyPolicy cellCopyPolicy = new CellCopyPolicy();
                    //移動西作業區
                    XSSFCell remarkFromCellXi = null;
                    XSSFCell remarkToCellXi = null;

                        remarkFromCellXi = POIReadUtils.getCell(sheet, 4, 0);
                        remarkToCellXi = POIReadUtils.getCell(sheet,
                                4+Math.max(detailList1.size()-1,0)+Math.max(detailList2.size()-1,0)
                                , 0);


                    //移動西
                    if(!CollectionUtils.isEmpty(detailList3)&&detailList3.size()>1){
                        remarkToCellXi.copyCellFrom(remarkFromCellXi, cellCopyPolicy);
                        //合并西
                        CellRangeAddress cellRangeAddressXi = new CellRangeAddress(
                                4+Math.max(detailList1.size()-1,0)+Math.max(detailList2.size()-1,0),
                                5+Math.max(detailList1.size()-1,0)+Math.max(detailList2.size()-1,0)+Math.max(detailList3.size()-1,0), 0, 0);
                        sheet.addMergedRegion(cellRangeAddressXi);


                    }
                    //移動中
                    XSSFCell remarkFromCellZhong = null;
                    XSSFCell remarkToCellZhong = null;

                    if (!CollectionUtils.isEmpty(detailList2)&&detailList2.size() > 1) {
                        remarkFromCellZhong = POIReadUtils.getCell(sheet, 2, 0);
                        remarkToCellZhong = POIReadUtils.getCell(sheet, 2+Math.max(detailList1.size()-1,0) , 0);
                    }
                    if(!CollectionUtils.isEmpty(detailList2)&&detailList2.size()>1){
                        remarkFromCellZhong.copyCellFrom(remarkToCellZhong, cellCopyPolicy);
                        // 合并中作业区
                        CellRangeAddress cellRangeAddress = new CellRangeAddress(
                                2+Math.max(CollectionUtils.isEmpty(detailList1)?0:detailList1.size()-1,0),
                                3+Math.max(CollectionUtils.isEmpty(detailList1)?0:detailList1.size()-1,0)+Math.max(detailList2.size()-1,0),
                                0, 0);
                        sheet.addMergedRegion(cellRangeAddress);


                    }
                    if(!CollectionUtils.isEmpty(detailList1) && detailList1.size()>1){
                        // 合并东作业区
                        CellRangeAddress cellRangeAddress = new CellRangeAddress(
                                1,
                                1 + Math.max((CollectionUtils.isEmpty(detailList1)?0:detailList1.size() - 1), 0),
                                0, 0);
                        sheet.addMergedRegion(cellRangeAddress);


                    }
                    //合并全部的
                    //合并中汇总
                    CellRangeAddress cellRangeAddressHuizong = new CellRangeAddress(
                            3+Math.max(CollectionUtils.isEmpty(detailList1)?0:detailList1.size()-1,0)+Math.max(detailList2.size()-1,0),
                            3+Math.max(CollectionUtils.isEmpty(detailList1)?0:detailList1.size()-1,0)+Math.max(detailList2.size()-1,0),
                            8, 9);
                    sheet.addMergedRegion(cellRangeAddressHuizong);

                    //合并西汇总
                    CellRangeAddress cellRangeAddressXihuizong = new CellRangeAddress(
                            5+Math.max(detailList1.size()-1,0)+Math.max(detailList2.size()-1,0)+Math.max(detailList3.size()-1,0),
                            5+Math.max(detailList1.size()-1,0)+Math.max(detailList2.size()-1,0)+Math.max(detailList3.size()-1,0),
                            8, 9);
                    sheet.addMergedRegion(cellRangeAddressXihuizong);
                    //合并汇总
                    CellRangeAddress cellRangeAddressAllHuizong = new CellRangeAddress(
                            6+Math.max(detailList1.size()-1,0)+Math.max(detailList2.size()-1,0)+Math.max(detailList3.size()-1,0),
                            6+Math.max(detailList1.size()-1,0)+Math.max(detailList2.size()-1,0)+Math.max(detailList3.size()-1,0),
                            8, 9);
                    sheet.addMergedRegion(cellRangeAddressAllHuizong);*/
                })

                .build()
                .exportByTemplate(tDisPortDaynightplanExportDTO);

        ResponseUtils.compliantWithExcel(response, ExcelTemplate.DAYNIGHTPLAN.getComment());
        response.getOutputStream().write(excelBytes);
    }
}
