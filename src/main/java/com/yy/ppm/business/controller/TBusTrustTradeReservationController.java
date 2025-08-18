package com.yy.ppm.business.controller;

import com.yy.common.enums.Response;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.ValidatorUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.trustTradeReservation.TBusTrustCargoDTO;
import com.yy.ppm.business.bean.dto.trustTradeReservation.TBusTrustTradeReservationDTO;
import com.yy.ppm.business.bean.dto.trustTradeReservation.TBusTrustTradeReservationQueryDTO;
import com.yy.ppm.business.bean.po.TBusTrustTradeReservatCarPO;
import com.yy.ppm.business.service.TBusTrustTradeReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @Author linqi
 * @Description 集疏港预约
 * @Date 2023-07-05 15:23
 */
@RestController
@RequestMapping("/api/external/trustTradeReservation")
@Validated
public class TBusTrustTradeReservationController {

    @Autowired
    private TBusTrustTradeReservationService tBusTrustTradeReservationService;

    @GetMapping("/listTrustCargo")
    public Map<String, Object> listTrustCargo(String keyword, Long trustCargoId, PageParameter parameter) {
        List<TBusTrustCargoDTO> result = tBusTrustTradeReservationService.listTrustCargo(keyword, trustCargoId, parameter);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    @PostMapping("/insertTrustTradeReservation")
    @PreAuthorize("hasAuthority('business:portReservation:add')")
    public Map<String, Object> insertTrustTradeReservation(@RequestBody TBusTrustTradeReservationDTO trustTradeReservation) {
        ValidatorUtils.FieldBean bean;
        if ((bean = ValidatorUtils.validator(trustTradeReservation, true, "id")).isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        if (trustTradeReservation.getCars() != null) {
            if ((bean = ValidatorUtils.validator(trustTradeReservation.getCars())).isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
        }

        tBusTrustTradeReservationService.insertTrustTradeReservation(trustTradeReservation);
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    @PutMapping("/updateTrustTradeReservation")
    @PreAuthorize("hasAuthority('business:portReservation:update')")
    public Map<String, Object> updateTrustTradeReservation(@RequestBody TBusTrustTradeReservationDTO trustTradeReservation) {
        ValidatorUtils.FieldBean bean;
        if ((bean = ValidatorUtils.validator(trustTradeReservation)).isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        if (trustTradeReservation.getCars() != null) {
            if ((bean = ValidatorUtils.validator(trustTradeReservation.getCars())).isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
        }

        tBusTrustTradeReservationService.updateTrustTradeReservation(trustTradeReservation);
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    @DeleteMapping("/deleteTrustTradeReservation")
    @PreAuthorize("hasAuthority('business:portReservation:delete')")
    public Map<String, Object> deleteTrustTradeReservation(@RequestParam("trustTradeReservationIds") @NotNull(message = "集疏港指令车队预约id") List<Long> trustTradeReservationIds) {
        tBusTrustTradeReservationService.deleteTrustTradeReservation(trustTradeReservationIds);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    @GetMapping("/listTrustTradeReservation")
    @PreAuthorize("hasAuthority('business:portReservation:query')")
    public Map<String, Object> listTrustTradeReservation(TBusTrustTradeReservationQueryDTO query, PageParameter parameter) {
        Pages<TBusTrustTradeReservationDTO> result = tBusTrustTradeReservationService.listTrustTradeReservation(query, parameter);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    @PostMapping("/parseCars")
    public Map<String, Object> parseCars(MultipartFile file) {
        List<TBusTrustTradeReservatCarPO> result = tBusTrustTradeReservationService.parseCars(file);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    @GetMapping("/getAvailableQuantityAndTon")
    public Map<String, Object> getAvailableQuantityAndTon(@NotNull(message = "已指派物流车队id不能为空") Long assignFleetId, Long trustTradeReservationId) {
        Map<String, Object> result = tBusTrustTradeReservationService.getAvailableQuantityAndTon(assignFleetId, trustTradeReservationId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }
}
