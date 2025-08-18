package com.yy.ppm.statement.controller;

import com.yy.common.enums.Response;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.ValidatorUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.po.TBusRatePO;
import com.yy.ppm.statement.bean.dto.costShipWaterElectricity.TBusTrustDTO;
import com.yy.ppm.statement.bean.dto.costShipWaterElectricity.TBusTrustQueryDTO;
import com.yy.ppm.statement.bean.dto.costShipWaterElectricity.TPrdWaterElectricityDTO;
import com.yy.ppm.statement.bean.po.TCostShipPO;
import com.yy.ppm.statement.service.TCostShipWaterElectricityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-23 11:11
 */
@RestController
@RequestMapping("/api/external/costShipWaterElectricity")
@Validated
public class TCostShipWaterElectricityController {

    @Autowired
    private TCostShipWaterElectricityService tCostShipWaterElectricityService;

    /**
     * 商务指令列表
     *
     * @param query
     * @param parameter
     * @return
     */
    @GetMapping("/listTrust")
    public Map<String, Object> listTrust(TBusTrustQueryDTO query, PageParameter parameter) {
        Pages<TBusTrustDTO> result = tCostShipWaterElectricityService.listTrust(query, parameter);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 指令ID查加水接电记录
     *
     * @param trustId
     * @return
     */
    @GetMapping("/listWaterElectricity")
    public Map<String, Object> listWaterElectricity(@NotNull(message = "指令ID不能为空") Long trustId) {
        List<TPrdWaterElectricityDTO> result = tCostShipWaterElectricityService.listWaterElectricity(trustId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 费率
     *
     * @return
     */
    @GetMapping("/listRate")
    public Map<String, Object> listRate() {
        List<TBusRatePO> result = tCostShipWaterElectricityService.listRate();
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
            if (v1.getTrustId() == null) {
                throw new BusinessRuntimeException("指令ID不能为空");
            }
        });

        tCostShipWaterElectricityService.statement(costShips);
        return Response.SUCCESS.newBuilder().out("结算成功").toResult();
    }

    /**
     * 回显
     *
     * @param trustId
     * @return
     */
    @GetMapping("/listCostShip")
    public Map<String, Object> listCostShip(@NotNull(message = "指令ID不能为空") Long trustId) {
        List<TCostShipPO> costShips = tCostShipWaterElectricityService.listCostShip(trustId);
        return Response.SUCCESS.newBuilder().toResult(costShips);
    }

    /**
     * 撤销结算
     *
     * @param trustId
     * @return
     */
    @DeleteMapping("/cancelStatement")
    public Map<String, Object> cancelStatement(@NotNull(message = "指令ID不能为空") Long trustId) {
        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(trustId);
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }

        tCostShipWaterElectricityService.cancelStatement(trustId);
        return Response.SUCCESS.newBuilder().out("撤销成功").toResult();
    }

    /**
     * 审核
     *
     * @param trustId
     * @return
     */
    @PutMapping("/review")
    public Map<String, Object> review(@NotNull(message = "指令ID不能为空") Long trustId) {
        tCostShipWaterElectricityService.review(trustId);
        return Response.SUCCESS.newBuilder().out("审核成功").toResult();
    }

    /**
     * 销审
     *
     * @param trustId
     * @return
     */
    @PutMapping("/cancelReview")
    public Map<String, Object> cancelReview(@NotNull(message = "指令ID不能为空") Long trustId) {
        tCostShipWaterElectricityService.cancelReview(trustId);
        return Response.SUCCESS.newBuilder().out("销审成功").toResult();
    }
}
