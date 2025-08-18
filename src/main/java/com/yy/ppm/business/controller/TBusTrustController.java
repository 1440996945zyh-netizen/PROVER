package com.yy.ppm.business.controller;

import com.yy.common.enums.CommonEnum;
import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.excel.POIReadUtils;
import com.yy.common.excel.export.ExcelExporter;
import com.yy.common.excel.export.enums.ExcelTemplate;
import com.yy.common.excel.export.utils.ResponseUtils;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.ValidatorUtils;
import com.yy.framework.annotation.Log;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.*;
import com.yy.ppm.business.bean.dto.trust.TrustDTO;
import com.yy.ppm.business.service.TBusTrustService;
import com.yy.ppm.master.bean.po.MTrustTypePO;
import com.yy.ppm.statement.bean.dto.CostBillDtoSheetTemplate;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementDetailDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @ClassName 作业指令表(TBusTrust)Controller
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月05日 09:21:00
 */
@RestController
@RequestMapping("/api/v1/internal/tBusTrust")
@Validated
@Tag(name = "作业通知单")
public class TBusTrustController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(TBusTrustController.class);

    @Autowired
    private TBusTrustService tBusTrustService;

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('business:trustOrder:query')")
    public Map<String, Object> getList(TBusTrustSearchDTO searchDTO) {
    	final String methodName = "TBusTrustController:getList";
		LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<TBusTrustDTO> pages = tBusTrustService.getList(searchDTO);

        LOGGER.exit( methodName + "result:" + pages);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getStorageYardList")
    @PreAuthorize("hasAuthority('business:trustOrder:query')")
    public Map<String, Object> getStorageYardList(TBusTrustSearchDTO searchDTO) {
    	final String methodName = "TBusTrustController:getList";
		LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<TBusTrustDTO> pages = tBusTrustService.getStorageYardList(searchDTO);

        LOGGER.exit( methodName + "result:" + pages);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 查询单条记录
     * @param id
     * @return
     */
    @GetMapping("/getDetail")
    @PreAuthorize("hasAuthority('business:trustOrder:query')")
    public Map<String, Object> getDetail(@NotNull(message = "指令ID不能为空") @RequestParam("id") Long id) {
        final String methodName = "TBusTrustController:getDetail";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        TBusTrustDTO result = tBusTrustService.getDetail(id);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询单条记录(新增带入)
     * @param id
     * @return
     */
    @GetMapping("/getDetailAdd")
    @PreAuthorize("hasAuthority('business:trustOrder:query')")
    public Map<String, Object> getDetailAdd( @RequestParam("id") Long id) {
        final String methodName = "TBusTrustController:getDetail";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        TBusTrustDTO result = tBusTrustService.getDetailAdd(id);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     * @param tBusTrustDTO
     * @return
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('business:trustOrder:add')")
    @Log(title = "新增作业通知单", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> add(@RequestBody TBusTrustDTO tBusTrustDTO) {
        final String methodName = "TBusTrustController:add";
		LOGGER.enter(methodName + "[start]", "tBusTrustDTO:" +  tBusTrustDTO);

        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(tBusTrustDTO, true);
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        boolean anyMatch = Stream.of("集港","拆箱集港", "疏港", "陆销").anyMatch(v1 -> tBusTrustDTO.getType().equals(v1));
        if (anyMatch) {
            if (tBusTrustDTO.getStartTime() == null || tBusTrustDTO.getEndTime() == null) {
                throw new BusinessRuntimeException("【集港】、【拆箱集港】、【疏港】、【陆销】计划开始、结束时间必填");
            }
        }
        tBusTrustDTO.getCargoList().forEach(v1 -> {
            if ("02".equals(v1.getPackingCode())) {
                if (v1.getQuantity() == null) {
                    throw new BusinessRuntimeException("件货件数必填");
                }
            }
            if ("1".equals(v1.getPrintPoundId())) {
                if (v1.getPrintPoundNum() == null) {
                    throw new BusinessRuntimeException("打印次数有效时，次数必填");
                }
            }
        });
        boolean bool = tBusTrustDTO.getCargoList().stream().anyMatch(v1 -> CommonEnum.YesNoMode.YES.getCode().equals(v1.getIsSecondWeigh()));
        if (bool) {
            if (tBusTrustDTO.getStartTime() == null || tBusTrustDTO.getEndTime() == null) {
                throw new BusinessRuntimeException("二次过磅需填写计划开始时间、结束时间");
            }
        }

        tBusTrustService.add(tBusTrustDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out("新增成功").toResult();

    }

    /**
     * 修改
     * @param tBusTrustDTO
     * @return
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('business:trustOrder:update')")
    @Log(title = "发布前更新作业通知单", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> update(@RequestBody TBusTrustDTO tBusTrustDTO) {
        final String methodName = "TBusTrustController:update";
		LOGGER.enter(methodName + "[start]", "tBusTrustDTO:" +  tBusTrustDTO);

        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(tBusTrustDTO, true);
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        boolean anyMatch = Stream.of("集港", "拆箱集港", "疏港", "陆销").anyMatch(v1 -> tBusTrustDTO.getType().equals(v1));
        if (anyMatch) {
            if (tBusTrustDTO.getStartTime() == null || tBusTrustDTO.getEndTime() == null) {
                throw new BusinessRuntimeException("【集港】、【拆箱集港】、【疏港】、【陆销】计划开始、结束时间必填");
            }
        }
        tBusTrustDTO.getCargoList().forEach(v1 -> {
            if ("02".equals(v1.getPackingCode())) {
                if (v1.getQuantity() == null) {
                    throw new BusinessRuntimeException("件货件数必填");
                }
            }
            if ("1".equals(v1.getPrintPoundId())) {
                if (v1.getPrintPoundNum() == null) {
                    throw new BusinessRuntimeException("打印次数有效时，次数必填");
                }
            }
        });
        boolean bool = tBusTrustDTO.getCargoList().stream().anyMatch(v1 -> CommonEnum.YesNoMode.YES.getCode().equals(v1.getIsSecondWeigh()));
        if (bool) {
            if (tBusTrustDTO.getStartTime() == null || tBusTrustDTO.getEndTime() == null) {
                throw new BusinessRuntimeException("二次过磅需填写计划开始时间、结束时间");
            }
        }

        tBusTrustService.update(tBusTrustDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    @GetMapping("/getStopLogList/{trustCargoId}")
    public Map<String, Object> getStopLogList(@NotNull(message = "查看通知单启停状态的时缺失必要的请求参数") @PathVariable Long trustCargoId
    ,Long cargoInfoId,Long trustId) {

        Pages<TrustStopLogRes> result = tBusTrustService.getStopLogList(trustCargoId,cargoInfoId,trustId);

        return Response.SUCCESS.newBuilder().out("修改成功").toResult(result);
    }

    /**
     * 驳回
     * @param tBusTrustDTO
     * @return
     */
    @PutMapping("/reject")
    @PreAuthorize("hasAuthority('business:trustOrder:reject')")
    @Log(title="指令驳回",value=OperateTypeEnum.QUERY)
    public Map<String, Object> reject(@RequestBody TBusTrustDTO tBusTrustDTO) {
        final String methodName = "TBusTrustController:reject";
        LOGGER.enter(methodName + "[start]", "tBusTrustDTO:" +  tBusTrustDTO);


        tBusTrustService.reject(tBusTrustDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out("驳回成功").toResult();
    }



    /**
     * 发布
     * @return
     */
    @PostMapping("/release/{id}")
    @PreAuthorize("hasAuthority('business:trustOrder:deliver')")
    @Log(title="指令发布",value=OperateTypeEnum.QUERY)
    public Map<String, Object> release(@PathVariable("id") Long id) {

        final String methodName = "TBusTrustController:release";
        LOGGER.enter(methodName + "[start]", "id:" +  id);

        boolean flag = tBusTrustService.doRelease(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('business:trustOrder:delete')")
    @Log(title="指令删除",value=OperateTypeEnum.QUERY)
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "TBusTrustController:deleteById";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tBusTrustService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 查询票货信息
     * @return
     */
    @GetMapping("/getticketinfo")
    @PreAuthorize("hasAuthority('business:trustOrder:getticketinfo')")
    public Map<String, Object> getTicketInfo(Long companyId, String tradeType, Long cargoAgentId,
                                             String cargoOwnerId,
                                             PageParameter pageParameter, String isLuxiao, String isShugang,String scn,String shipvoyageItemId,
                                             String cargoInfoNo, String businessNo,String trustType) {
        final String methodName = "TBusTrustController:getTicketInfo";
        LOGGER.enter(methodName + "[start]", "companyId:" + companyId+",tradeType:"+tradeType);

        Pages<TBusCargoInfoDTO> result = tBusTrustService.getTicketInfo(companyId, tradeType, cargoAgentId, cargoOwnerId, pageParameter, isLuxiao, isShugang, scn, shipvoyageItemId, cargoInfoNo, businessNo,trustType);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询票货信息
     * @return
     */
    @GetMapping("/getOrderCargoName")
    public Map<String, Object> getOrderCargoName(String billNo,String shipvoyageItemId,String cargoInfoNo,PageParameter pageParameter) {
        final String methodName = "TBusTrustController:getTicketInfo";

        Pages<TBusCargoInfoDTO> result = tBusTrustService.getOrderCargoName( billNo, shipvoyageItemId, cargoInfoNo, pageParameter);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 撤销发布
     *
     * @param trustId
     * @return
     */
    @PutMapping("/cancelRelease")
    @Log(title="指令撤销发布",value=OperateTypeEnum.QUERY)

    public Map<String, Object> cancelRelease(@NotNull(message = "指令ID不能为空") Long trustId,String type) {
        tBusTrustService.cancelRelease(trustId,type);
        return Response.SUCCESS.newBuilder().out("撤销成功").toResult();
    }

    /**
     * 修改已发布指令
     *
     * @param dto
     * @return
     */
    @PutMapping("/updateAfterRelease")
    @Log(title = "发布后更新作业通知单", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> updateAfterRelease(@RequestBody TrustDTO dto) {
        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(dto);
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        if (CollectionUtils.isNotEmpty(dto.getUpdates())) {
            bean = ValidatorUtils.validator(dto.getUpdates());
            if (bean.isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
        }
        boolean bool = dto.getInserts().stream().anyMatch(v1 -> CommonEnum.YesNoMode.YES.getCode().equals(v1.getIsSecondWeigh()));
        bool = bool || dto.getUpdates().stream().anyMatch(v1 -> CommonEnum.YesNoMode.YES.getCode().equals(v1.getIsSecondWeigh()));
        if (bool) {
            if (dto.getStartTime() == null || dto.getEndTime() == null) {
                throw new BusinessRuntimeException("二次过磅需填写计划开始时间、结束时间");
            }
        }

        tBusTrustService.updateAfterRelease(dto);
        return Response.SUCCESS.newBuilder().out("修改完成").toResult();
    }

    /**
     * 修改已发布指令
     *
     * @param dto
     * @return
     */
    @PutMapping("/updateConsigner")
    @Log(title="修改已经发布的指令-更新委托人",value=OperateTypeEnum.QUERY)
    public Map<String, Object> updateConsigner(@RequestBody TrustDTO dto) {
        tBusTrustService.updateConsigner(dto);
        return Response.SUCCESS.newBuilder().out("委托完成").toResult();
    }

    /**
     * 货主、货物编码、贸别查合同列表
     *
     * @param cargoOwnerId
     * @param cargoCode
     * @param tradeType
     * @return
     */
    @GetMapping("/listContract")
    public Map<String, Object> listContract(
            @NotNull(message = "货主ID不能为空") Long cargoOwnerId,
            @NotBlank(message = "货物编码不能为空") String cargoCode,
            @NotBlank(message = "贸别不能为空") String tradeType
    ) {
        List<Map<String, Object>> list = tBusTrustService.listContract(cargoOwnerId, cargoCode, tradeType);
        return Response.SUCCESS.newBuilder().toResult(list);
    }

    /**
     * 获取阶梯累计量、优惠费率
     * @param contractId
     * @param contractName
     * @return
     */
    @GetMapping("/getPreferentialRate")
    public Map<String, Object> getPreferentialRate(Long contractId, @NotBlank(message = "合同编码不能为空") String contractName,@NotBlank(message = "货物编码不能为空") String cargoCode) {
        Map<String, Object> result = tBusTrustService.getPreferentialRate(contractId,contractName,cargoCode);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 通知单类型列表
     *
     * @return
     */
    @GetMapping("/listTrustType")
    public Map<String, Object> listTrustType() {
        List<MTrustTypePO> trustTypes = tBusTrustService.listTrustType();
        return Response.SUCCESS.newBuilder().toResult(trustTypes);
    }

    /**
     * 通知单类型列表
     *
     * @return
     */
    @GetMapping("/listTrustByShipVoyageId")
    public Map<String, Object> listTrustByShipVoyageId(Long shipVoyageId) {
        final String methodName = "TBusTrustController:listTrustByShipVoyageId";
        LOGGER.enter(methodName + "[start]", "shipVoyageId:" + shipVoyageId);

        Map<String, Object> result = tBusTrustService.isTrust(shipVoyageId);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 船舶航次附件列表
     *
     * @param id
     * @return
     */
    @GetMapping("/listShipvoyageItemFile")
    public Map<String, Object> listShipvoyageItemFile(@NotNull(message = "通知单ID不能为空") Long id) {
        List<Map<String, Object>> result = tBusTrustService.listShipvoyageItemFile(id);
        return Response.SUCCESS.newBuilder().toResult(result);
    }
    /**
     * 库场计划消审核
     * /api/v1/internal/tBusTrust/cancelAudit
     */

    @GetMapping("/cancelAudit")
    @Log(title="库场计划消审核",value=OperateTypeEnum.QUERY)
    public Map<String, Object> cancelAudit(@NotNull(message = "通知单ID不能为空") Long id) {
        Boolean flag = tBusTrustService.kcjhCancelAudit(id);
        return Response.SUCCESS.newBuilder().out(flag?"销审成功":"销审失败").toResult();
    }




    /**
     * 库场计划消审核
     * /api/v1/internal/tBusTrust/cancelAudit
     */

    @GetMapping("/exportFeeEvent")
    public void exportFeeEvent(@NotNull(message = "通知单ID不能为空")@RequestParam("trustId") Long id, HttpServletResponse response) throws IOException {
        TrustFeeExportDTO trustDTO = tBusTrustService.exportFeeEvent(id);
        byte[] excelBytes = ExcelExporter.newBuilder()
                .templatePath(ExcelTemplate.TRUST_FEE_EXPORT_TEMPLATE.getTemplatePath())
                .postHandle(workbook -> {
                    XSSFSheet sheet = workbook.getSheetAt(0);
                    //设置打印区域
                    workbook.setPrintArea(0,
                            1,7,
                            1,16);

                })
                .build()
                .exportByTemplate(trustDTO);

        ResponseUtils.compliantWithExcel(response, ExcelTemplate.TRUST_FEE_EXPORT_TEMPLATE.getComment());
        response.getOutputStream().write(excelBytes);
    }

    /**
     * 修改开启停用状态
     */
    @PutMapping("/isStopStatus")
    @Log(title="计划启停开关",value=OperateTypeEnum.QUERY)
    public Map<String, Object> isStopStatus(@RequestBody TBusTrustCargoDTO tBusTrustCargoDTO) {
        boolean flag = tBusTrustService.isStopStatus(tBusTrustCargoDTO);
        return Response.SUCCESS.newBuilder().out(flag ? "成功" : "失败").toResult();
    }

    @GetMapping("/getTrustCargoById")
    public Map<String, Object> getTrustCargoById(@NotNull(message = "通知单ID不能为空") Long id) {

        TBusTrustDTO result = tBusTrustService.getTrustCargoById(id);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
}

