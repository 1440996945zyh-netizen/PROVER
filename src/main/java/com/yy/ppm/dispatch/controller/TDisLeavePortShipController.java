package com.yy.ppm.dispatch.controller;

import com.yy.common.enums.Response;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.dispatch.bean.dto.disShipDynamic.TDisShipDynamicDTO;
import com.yy.ppm.dispatch.bean.po.TDisLeavePortCostShipPO;
import com.yy.ppm.dispatch.service.TDisLeavePortShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/external/disLeavePort")
@Validated
public class TDisLeavePortShipController {

    @Autowired
    private TDisLeavePortShipService tDisLeavePortShipService;

    @GetMapping("/getCostShipList")
    public Map<String, Object> getCostShipList(Long shipvoyageId) {
        if (shipvoyageId == null) {
            throw new BusinessRuntimeException("航次id不能为空");
        }

        List<TDisLeavePortCostShipPO> result = tDisLeavePortShipService.getCostShipList(shipvoyageId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }
}
