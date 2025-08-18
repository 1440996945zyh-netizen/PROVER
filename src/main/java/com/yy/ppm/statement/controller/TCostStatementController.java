package com.yy.ppm.statement.controller;


import cn.hutool.core.io.IORuntimeException;
import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.excel.export.utils.ResponseUtils;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.business.bean.dto.TBusRateDTO;
import com.yy.ppm.produce.bean.dto.workTicket.TPrdWorkPlanQuery;
import com.yy.ppm.statement.bean.dto.ConfirmForMiscAndStorageDTO;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementDTO;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementDetailDTO;
import com.yy.ppm.statement.bean.dto.costShipWaterElectricity.TBusTrustDTO;
import com.yy.ppm.statement.bean.po.TCostStatementPO;
import com.yy.ppm.statement.service.TCostStatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 商务审核 -- 结算单
 */
@RestController
@RequestMapping("/api/external/TCostStatementController")
@Validated
public class TCostStatementController {

    @Autowired
    private TCostStatementService tCostStatementService;

    /**
     * 查询列表
     * @param tCostStatementDTO
     * @param parameter
     * @return
     */
    @GetMapping("/queryAll")
    public Map<String,Object> queryAll(TCostStatementDTO tCostStatementDTO, PageParameter parameter) {
        Pages<TCostStatementPO> result = tCostStatementService.queryAll(tCostStatementDTO, parameter);
        return Response.SUCCESS.newBuilder().toResult(result);
    }


    @GetMapping("/queryAllDetail")
    public Map<String,Object> queryAllDetail(TCostStatementDTO tCostStatementDTO, PageParameter parameter) {
        Pages<TCostStatementPO> result = tCostStatementService.queryAllDetail(tCostStatementDTO, parameter);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 商务审核/撤销
     * @param tCostStatementDTO
     * @return
     */
    @PostMapping("/review")
    public Map<String,Object> review(@RequestBody TCostStatementDTO tCostStatementDTO) {
        tCostStatementService.review(tCostStatementDTO);
        return Response.SUCCESS.newBuilder().out("审核成功").toResult();
    }

    /**
     * 打印标记
     * @param tCostStatementDTO
     * @return
     */
    @PostMapping("/printMark")
    public Map<String,Object> printMark(@RequestBody TCostStatementDTO tCostStatementDTO) {
        tCostStatementService.printMark(tCostStatementDTO);
        return Response.SUCCESS.newBuilder().out("标记成功").toResult();
    }

    /**
     * 商务审核/撤销
     * @param tCostStatementDTO
     * @return
     */
    @GetMapping("/financeReview")
    public Map<String,Object> financeReview(TCostStatementDTO tCostStatementDTO) {
        tCostStatementService.financeReview(tCostStatementDTO);
        return Response.SUCCESS.newBuilder().out("审核成功").toResult();
    }

    /**
     * 查询标准费率
     * @return
     */
    @GetMapping("/queryRate")
    public Map<String,Object> queryRate() {
        List<TBusRateDTO> info = tCostStatementService.queryRate();
        return Response.SUCCESS.newBuilder().toResult(info);
    }

    /**
     * 新增
     * @param tCostStatementDTO
     * @return
     */
    @PostMapping("/insert")
    public Map<String,Object> insert(@RequestBody TCostStatementDTO tCostStatementDTO) {
        tCostStatementService.insert(tCostStatementDTO);
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }

    /**
     * 查询详情
     * @param tCostStatementDTO
     * @return
     */
    @GetMapping("/queryById")
    public Map<String,Object> queryById(TCostStatementDTO tCostStatementDTO) {
        TCostStatementDTO info = tCostStatementService.queryById(tCostStatementDTO);
        return Response.SUCCESS.newBuilder().toResult(info);
    }

    @GetMapping("/queryByIdzk")
    public Map<String,Object> queryByIdzk(TCostStatementDTO tCostStatementDTO) {
        TCostStatementDTO info = tCostStatementService.queryByIdzk(tCostStatementDTO);
        return Response.SUCCESS.newBuilder().toResult(info);
    }

    /**
     * 修改
     * @param tCostStatementDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String,Object> update(@RequestBody TCostStatementDTO tCostStatementDTO) {
        tCostStatementService.update(tCostStatementDTO);
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }

    /**
     * 删除
     * @param tCostStatementDTO
     * @return
     */
    @DeleteMapping("/deleteById")
    public Map<String,Object> deleteById(TCostStatementDTO tCostStatementDTO) {
        tCostStatementService.deleteById(tCostStatementDTO);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 查询费目
     * @param
     * @return
     */
    @GetMapping("/queryRateItem")
    public Map<String,Object> queryRateItem() {
        List<Map<String,Object>> info = tCostStatementService.queryRateItem();
        return Response.SUCCESS.newBuilder().toResult(info);
    }

    /**
     * 保存文件
     * @param dto
     * @return
     */
    @PostMapping("/uploadConfirmFile")
    public Map<String, Object> uploadConfirmFile(@RequestBody ConfirmForMiscAndStorageDTO dto) {
        tCostStatementService.saveConfirmFile(dto);
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }

    /**
     * 复核/撤销
     * @param tCostStatementDTO
     * @return
     */
    @PostMapping("/recheck")
    public Map<String,Object> recheck(@RequestBody TCostStatementDTO tCostStatementDTO) {
        tCostStatementService.recheck(tCostStatementDTO);
        return Response.SUCCESS.newBuilder().out("操作成功").toResult();
    }

    /**
     * 申请开票/撤销
     * @param tCostStatementDTO
     * @return
     */
    @PostMapping("/applyInvoice")
    public Map<String,Object> applyInvoice(@RequestBody TCostStatementDTO tCostStatementDTO) {
        tCostStatementService.applyInvoice(tCostStatementDTO);
        return Response.SUCCESS.newBuilder().out("操作成功").toResult();
    }

    /**
     * 查看文件
     * @param tCostStatementDTO
     * @return
     */
    @GetMapping("/queryFiles")
    public Map<String,Object> queryFiles(TCostStatementDTO tCostStatementDTO) {
        List<Map<String,Object>> list = tCostStatementService.queryFiles(tCostStatementDTO);
        return Response.SUCCESS.newBuilder().out("操作成功").toResult(list);
    }

    /**
     * 新增
     * @param tCostStatementDetailDTO
     * @return
     */
    @PostMapping("/updateStatementItem")
    @Log(title = "修改结算单明细", value = OperateTypeEnum.UPDATE)
    public Map<String,Object> updateStatementItem(@RequestBody TCostStatementDetailDTO tCostStatementDetailDTO) {
        tCostStatementService.updateStatementItem(tCostStatementDetailDTO);
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }

    @PostMapping("/updateStatementItemD")
    @Log(title = "修改结算单明细(折扣)", value = OperateTypeEnum.UPDATE)
    public Map<String,Object> updateStatementItemD(@RequestBody TCostStatementDetailDTO tCostStatementDetailDTO) {
        tCostStatementService.updateStatementItemD(tCostStatementDetailDTO);
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }

    /**
     * 红冲
     * @param tCostStatementDTO
     * @return
     */
    @GetMapping("/redRush")
    @Log(title = "结算单红冲", value = OperateTypeEnum.UPDATE)
    public Map<String,Object> redRush(TCostStatementDTO tCostStatementDTO) {
        tCostStatementService.redRush(tCostStatementDTO);
        return Response.SUCCESS.newBuilder().out("操作成功").toResult();
    }

    @GetMapping("/exportExcel")
    public void exportExcel(TCostStatementDTO query, HttpServletResponse response) {
        ResponseUtils.compliantWithExcel(response, "结算汇总表");
        try {
            byte[] bytes = tCostStatementService.exportExcel(query);
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
