package com.yy.ppm.gis.controller;

import com.yy.common.enums.Response;
import com.yy.common.util.ValidatorUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.gis.dto.onSiteDynamics.*;
import com.yy.ppm.gis.service.OnSiteDynamicsService;
import com.yy.ppm.runpile.bean.po.MStorageStackPositionPO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author linqi
 * @Description 现场动态
 * @Date 2023-06-06 17:16
 */
@RestController
@RequestMapping("/api/external/onSiteDynamics")
@Validated
public class OnSiteDynamicsController {

    @Autowired
    private OnSiteDynamicsService onSiteDynamicsService;

    /**
     * 港区元素：船
     *
     * @return
     */
    @GetMapping("/listShip")
    public Map<String, Object> listShip() {
        List<Ship> ships = onSiteDynamicsService.listShip();
        return Response.SUCCESS.newBuilder().toResult(ships);
    }

    /**
     * 港区元素：榄桩
     *
     * @return
     */
    @GetMapping("/listPile")
    public Map<String, Object> listPile() {
        List<Pile> ships = onSiteDynamicsService.listPile();
        return Response.SUCCESS.newBuilder().toResult(ships);
    }

    /**
     * 港区元素：垛位
     *
     * @return
     */
    @GetMapping("/listStack")
    public Map<String, Object> listStack() {
        List<Stack> stacks = onSiteDynamicsService.listStack();
        return Response.SUCCESS.newBuilder().toResult(stacks);
    }

    /**
     * 港区元素：垛位-流机队
     *
     * @return
     */
    @GetMapping("/listStackForMac")
    public Map<String, Object> listStackForMac() {
        List<Stack> stacks = onSiteDynamicsService.listStackForMac();
        return Response.SUCCESS.newBuilder().toResult(stacks);
    }

    /**
     * 港区元素：垛位明细
     *
     * @param id
     * @return
     */
    @GetMapping("/getStack")
    public Map<String, Object> getStack(@NotNull(message = "垛位ID不能为空") Long id) {
        Map<String, Object> result = onSiteDynamicsService.getStack(id);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 港区元素：垛位明细
     *
     * @param id
     * @return
     */
    @GetMapping("/getStackForMacApp")
    public Map<String, Object> getStackForMacApp(@NotNull(message = "垛位ID不能为空") Long id) {
        Map<String, Object> result = onSiteDynamicsService.getStackForMacApp(id);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 港区元素：车辆
     *
     * @return
     */
    @GetMapping("/listCar")
    public Map<String, Object> listCar() {
        List<Car> cars = onSiteDynamicsService.listCar();
        return Response.SUCCESS.newBuilder().toResult(cars);
    }

    /**
     * 车辆历史
     *
     * @param macId
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/listCarHistory")
    public Map<String, Object> listCarHistory(
            @NotBlank String macId,
            @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
            @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime
    ) {
        List<CarHistory> historyList = onSiteDynamicsService.listCarHistory(macId, beginTime, endTime);
        return Response.SUCCESS.newBuilder().toResult(historyList);
    }

    /**
     * 新增垛位点位信息
     *
     * @param storageStackPosition
     * @return
     */
    @PostMapping("/insertStorageStackPosition")
    @PreAuthorize("hasAuthority('gis:onSiteDynamics:insertStorageStackPosition')")
    public Map<String, Object> insertStorageStackPosition(@RequestBody MStorageStackPositionPO storageStackPosition) {
        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(storageStackPosition);
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }

        onSiteDynamicsService.insertStorageStackPosition(storageStackPosition);
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 删除垛位点位信息
     *
     * @param stackId
     * @return
     */
    @DeleteMapping("/deleteStorageStackPosition")
    @PreAuthorize("hasAuthority('gis:onSiteDynamics:deleteStorageStackPosition')")
    public Map<String, Object> deleteStorageStackPosition(@NotNull(message = "垛位ID不能为空") Long stackId) {
        onSiteDynamicsService.deleteStorageStackPosition(stackId);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 查询港存进出明细
     *
     * @param query
     * @return
     */
    @GetMapping("/getInoutDetail")
    public Map<String, Object> getInoutDetail(InoutDetailQueryDTO query) {
        if (StringUtils.isNotBlank(query.getBeginClassCode())) {
            if (query.getBeginWorkDate() == null) {
                throw new BusinessRuntimeException("起始作业日期不能为空");
            }
        }
        if (StringUtils.isNotBlank(query.getEndClassCode())) {
            if (query.getEndWorkDate() == null) {
                throw new BusinessRuntimeException("结束作业日期不能为空");
            }
        }

        Map<String, Object> result = onSiteDynamicsService.getInoutDetail(query);
        return Response.SUCCESS.newBuilder().toResult(result);
    }
}
