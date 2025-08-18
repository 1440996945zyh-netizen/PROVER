package com.yy.ppm.gis.controller;

import com.yy.common.enums.Response;
import com.yy.ppm.gis.dto.workArea.ShiftPlan;
import com.yy.ppm.gis.po.MAiaLocationPO;
import com.yy.ppm.gis.service.ShipAisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Hu Jingjing
 * @version 1.0.0
 * 与船舶AIS相关接口
 * @ClassName ShipAisController.java
 * @Description TODO
 * @createTime 2023年09月20日 16:46:00
 */
//@Validated
//@RequiredArgsConstructor
@RestController
@RequestMapping("/api/external/shipAis")
public class ShipAisController {
    @Autowired
    private ShipAisService shipAisService;

    @GetMapping("/getShipTrack")
    public Map<String, Object> getShipTrack(
            @NotNull(message = "mmsi不能为空") String mmsi,
            @NotNull(message = "startTime不能为空") String startTime,
            @NotNull(message = "endTime不能为空") String endTime
    ) {
        List<MAiaLocationPO> shipTrackList = shipAisService.getShipTrack(mmsi, startTime, endTime);
        return Response.SUCCESS.newBuilder().toResult(shipTrackList);
    }

    @GetMapping("/getShipInfo")
    public Map<String, Object> getShipInfo(@NotNull(message = "id不能为空") String id) {
        Map shipInfo = shipAisService.getShipInfo(id);
        return Response.SUCCESS.newBuilder().toResult(shipInfo);
    }
}
