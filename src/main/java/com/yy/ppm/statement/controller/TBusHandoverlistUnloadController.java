package com.yy.ppm.statement.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.statement.bean.dto.busHandoverlist.*;
import com.yy.ppm.statement.service.TBusHandoverlistUnloadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description 卸船交接清单
 * @Date 2023-09-07 10:56
 */
@RestController
@RequestMapping("/api/external/busHandoverlistUnload")
@Validated
@Tag(name = "卸船交接清单")
public class TBusHandoverlistUnloadController {

    @Autowired
    private TBusHandoverlistUnloadService tBusHandoverlistService;

    /**
     * 船舶航次列表
     *
     * @param query
     * @param parameter
     * @return
     */
    @GetMapping("/listDisShipvoyageItemUnload")
    public Map<String, Object> listDisShipvoyageItem(TDisShipvoyageItemQueryDTO query, PageParameter parameter) {
        Pages<TDisShipvoyageItemDTO> result = tBusHandoverlistService.listDisShipvoyageItem(query, parameter);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 船舶航次列表获取合计量
     *
     * @param query
     * @param
     * @return
     */
    @GetMapping("/getAllTon")
    public Map<String, Object> getListTon(TDisShipvoyageItemQueryDTO query) {
        BigDecimal result = tBusHandoverlistService.getListTon(query);
        return Response.SUCCESS.newBuilder().toResult(result);
    }
    /**
     * 集疏港指令列表
     *
     * @param query
     * @param parameter
     * @return
     */
    @GetMapping("/listTrust")
    public Map<String, Object> listTrust(TBusTrustQueryDTO query, PageParameter parameter) {
        Pages<TBusTrustDTO> result = tBusHandoverlistService.listTrust(query, parameter);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 船舶航次ID/指令ID查交接清单列表
     *
     * @param shipvoyageItemId
     * @param trustId
     * @return
     */
    @GetMapping("/listBusHandoverlist")
    public Map<String, Object> listBusHandoverlist(Long shipvoyageItemId, Long trustId) {
        if ((shipvoyageItemId == null) == (trustId == null)) {
            throw new BusinessRuntimeException("船舶航次ID和指令ID必须有且仅能有其一");
        }
        List<TBusHandoverlistDTO> result = tBusHandoverlistService.listBusHandoverlist(shipvoyageItemId, trustId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 船舶航次ID/指令ID查票货信息列表
     *
     * @param shipvoyageItemId
     * @param trustId
     * @return
     */
    @GetMapping("/listBusCargoInfo")
    public Map<String, Object> listBusCargoInfo(Long shipvoyageItemId, Long trustId) {
        if ((shipvoyageItemId == null) == (trustId == null)) {
            throw new BusinessRuntimeException("船舶航次ID和指令ID必须有且仅能有其一");
        }
        List<TBusCargoInfoDTO> result = tBusHandoverlistService.listBusCargoInfo(shipvoyageItemId, trustId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 新增卸船交接清单
     *
     * @param dto
     * @return
     */
    @PostMapping("/updateBusHandoverlist")
    @Log(title = "卸船交接清单IUD", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> insertBusHandoverlist(@RequestBody TBusHandoverListUnloadReqDTO dto) {
            if(dto.getShipvoyageItemId()==null){
                throw new BusinessRuntimeException("缺少航次信息");
            }
            if(dto.getShipvoyageId()==null){
                throw new BusinessRuntimeException("缺少船舶信息");
            }
            if(!dto.getHandoverlists().isEmpty()){
                dto.getHandoverlists().forEach(o->{
                    if(o.getCompanyId()==null||StringUtils.isEmpty(o.getCompanyName())){
                        throw new BusinessRuntimeException("缺少作业公司信息");
                    }
                });
            }

        String cargoNos = tBusHandoverlistService.addUpdateHandoverList(dto);
        return Response.SUCCESS.newBuilder().out("新增成功").toResult(cargoNos);
    }

    /**
     * 校验票货是否已经存在通知单
     *
     * @param cargoInfoId
     * @return
     */
    @GetMapping("/isHaveTrust/{cargoInfoId}")
    @Log(title = "卸船交接清单删除校验", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> isHaveTrust(@PathVariable Long cargoInfoId) {
            if(cargoInfoId==null){
                throw new BusinessRuntimeException("没有票货信息");
            }

        boolean result = tBusHandoverlistService.isHaveTrust(cargoInfoId);
        return Response.SUCCESS.newBuilder().out("新增成功").toResult(result);
    }


    /**
     * 校验票货是否已经存在通知单
     *
     * @param voyageId
     * @return
     */
    @GetMapping("/checkHaveTrust/{voyageId}")
    @Log(title = "更新交接清单", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> checkHaveTrust(@NotNull(message = "航次id不能为空") @PathVariable Long voyageId) {

        boolean result = tBusHandoverlistService.checkHaveTrust(voyageId);
        return Response.SUCCESS.newBuilder().out("新增成功").toResult(result);
    }


}
