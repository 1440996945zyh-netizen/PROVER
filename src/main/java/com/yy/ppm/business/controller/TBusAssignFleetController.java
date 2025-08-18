package com.yy.ppm.business.controller;

import com.yy.common.enums.Response;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.ValidatorUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.assignFleet.TBusTrustCargoDTO;
import com.yy.ppm.business.bean.dto.assignFleet.TBusTrustCargoQueryDTO;
import com.yy.ppm.business.bean.po.TBusAssignFleetPO;
import com.yy.ppm.business.service.TBusAssignFleetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @Author linqi
 * @Description 指派物流车队
 * @Date 2023-07-04 17:33
 */
@RestController
@RequestMapping("/api/external/assignFleet")
@Validated
public class TBusAssignFleetController {

    @Autowired
    private TBusAssignFleetService tBusAssignFleetService;

    @GetMapping("/listTrustCargo")
    @PreAuthorize("hasAuthority('business:assignFleet:query')")
    public Map<String, Object> listTrustCargo(TBusTrustCargoQueryDTO query, PageParameter parameter) {
        Pages<TBusTrustCargoDTO> result = tBusAssignFleetService.listTrustCargo(query, parameter);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    @PutMapping("/updateAssignFleet")
    @PreAuthorize("hasAuthority('business:assignFleet:update')")
    public Map<String, Object> updateAssignFleet(@NotNull(message = "指令票货id不能为空") Long trustCargoId, @RequestBody List<TBusAssignFleetPO> assignFleets) {
        assignFleets.forEach(v1 -> {
            v1.setTrustCargoId(trustCargoId);
        });
        ValidatorUtils.FieldBean bean;
        if ((bean = ValidatorUtils.validator(assignFleets)).isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }

        if (assignFleets.stream().map(TBusAssignFleetPO::getCustomerId).distinct().count() != assignFleets.size()) {
            throw new BusinessRuntimeException("重复的物流车队");
        }

        tBusAssignFleetService.updateAssignFleet(trustCargoId, assignFleets);
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }
}
