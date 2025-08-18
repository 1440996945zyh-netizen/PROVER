package com.yy.ppm.statement.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.ValidatorUtils;
import com.yy.framework.annotation.Log;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.enums.BusHandoverlistTypeEnum;
import com.yy.ppm.statement.bean.dto.busHandoverlist.*;
import com.yy.ppm.statement.service.TBusHandoverlistService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description 交接清单
 * @Date 2023-09-07 10:56
 */
@RestController
@RequestMapping("/api/external/busHandoverlist")
@Validated
@Tag(name = "交接清单")
public class TBusHandoverlistController {

    @Autowired
    private TBusHandoverlistService tBusHandoverlistService;

    /**
     * 船舶航次列表
     *
     * @param query
     * @param parameter
     * @return
     */
    @GetMapping("/listDisShipvoyageItem")
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
     * 更新交接清单
     *
     * @param dto
     * @return
     */
    @PostMapping("/updateBusHandoverlist")
    @Log(title = "更新交接清单", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> insertBusHandoverlist(@RequestBody UpdateBusHandoverlistDTO dto) {
        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(dto);
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }

        bean = ValidatorUtils.validator(dto.getHandoverlists(), true, "id");
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        dto.getHandoverlists().forEach(v1 -> {
            if (!BusHandoverlistTypeEnum.contains(v1.getType())) {
                throw new BusinessRuntimeException("错误的交接清单类型");
            }
            if (BusHandoverlistTypeEnum.ZHUANGXIECHUAN.getCode().equals(v1.getType())) {
                if (v1.getShipvoyageId() == null) {
                    throw new BusinessRuntimeException("航次ID不能为空");
                }
                if (v1.getShipvoyageItemId() == null) {
                    throw new BusinessRuntimeException("航次子表ID不能为空");
                }
                if (StringUtils.isBlank(v1.getShipName())) {
                    throw new BusinessRuntimeException("船名不能为空");
                }
                if (StringUtils.isBlank(v1.getVoyage())) {
                    throw new BusinessRuntimeException("航次不能为空");
                }
                if (StringUtils.isBlank(v1.getTradeType())) {
                    throw new BusinessRuntimeException("贸别不能为空");
                }
                if (StringUtils.isBlank(v1.getLoadUnload())) {
                    throw new BusinessRuntimeException("装卸不能为空");
                }
            }
            if (BusHandoverlistTypeEnum.LUJILUSHU.getCode().equals(v1.getType())) {
                if (v1.getTrustId() == null) {
                    throw new BusinessRuntimeException("指令ID不能为空");
                }
                if (v1.getTrustCargoId() == null) {
                    throw new BusinessRuntimeException("指令票货ID不能为空");
                }
            }
        });

        if (BusHandoverlistTypeEnum.ZHUANGXIECHUAN.getCode().equals(dto.getHandoverlists().get(0).getType())) {
//            if (CollectionUtils.isEmpty(dto.getFileIds())) {
//                throw new BusinessRuntimeException("附件ID不能为空");
//            }
        }

        StringBuffer cargoNos = tBusHandoverlistService.updateBusHandoverlist(dto);
        return Response.SUCCESS.newBuilder().out("新增成功").toResult(cargoNos);
    }
}
