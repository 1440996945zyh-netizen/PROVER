package com.yy.ppm.statement.controller.storageSettleMix;

import com.yy.common.enums.Response;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.ValidatorUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.statement.bean.dto.storageSettleMix.TCostStorageSettleDTO;
import com.yy.ppm.statement.bean.dto.storageSettleMix.TCostStorageSettleDetailDTO;
import com.yy.ppm.statement.bean.dto.storageSettleMix.TBusCargoInfoDTO;
import com.yy.ppm.statement.bean.dto.storageSettleMix.TBusCargoInfoQueryDTO;
import com.yy.ppm.statement.service.storageSettleMix.StorageSettleMixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description 堆存费结算（混配）
 * @Date 2024-03-07 14:35
 */
@RestController
@RequestMapping("/api/external/storageSettleMix")
@Validated
public class StorageSettleMixController {

    @Autowired
    private StorageSettleMixService service;

    /**
     * 票货列表
     *
     * @param parameter
     * @param query
     * @return
     */
    @GetMapping("/listCargoInfo")
    public Map<String, Object> listCargoInfo(PageParameter parameter, TBusCargoInfoQueryDTO query) {
        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(query);
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }

        Pages<TBusCargoInfoDTO> result = service.listCargoInfo(parameter, query);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 待结算明细列表
     *
     * @param cargoInfoId
     * @return
     */
    @GetMapping("/listDetail")
    public Map<String, Object> listDetail(@NotNull(message = "票货ID不能为空") Long cargoInfoId) {
        List<TCostStorageSettleDetailDTO> result = service.listDetail(cargoInfoId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 合同列表
     *
     * @param cargoInfoId
     * @param date
     * @return
     */
    @GetMapping("/listContract")
    public Map<String, Object> listContract(@NotNull(message = "票货ID不能为空") Long cargoInfoId, @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        List<Map<String, Object>> result = service.listContract(cargoInfoId, date);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 待结算明细列表（已选合同）
     *
     * @param cargoInfoId
     * @param contractRateId
     * @param isUseReduce
     * @return
     */
    @GetMapping("/listDetailWithContract")
    public Map<String, Object> listDetailWithContract(@NotNull(message = "票货ID不能为空") Long cargoInfoId, @NotNull(message = "合同费率ID不能为空") Long contractRateId, String isUseReduce) {
        List<TCostStorageSettleDetailDTO> result = service.listDetailWithContract(cargoInfoId, contractRateId, isUseReduce);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 结算
     *
     * @param storageSettle
     * @return
     */
    @PostMapping("/settle")
    public Map<String, Object> settle(@RequestBody TCostStorageSettleDTO storageSettle) {
        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(storageSettle, true, "handoverlistId");
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        bean = ValidatorUtils.validator(storageSettle.getDetails());
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }

        service.settle(storageSettle);
        return Response.SUCCESS.newBuilder().out("结算成功").toResult();
    }

    /**
     * 堆存费结算列表
     *
     * @param cargoInfoId
     * @return
     */
    @GetMapping("/listStorageSettle")
    public Map<String, Object> listStorageSettle(@NotNull(message = "票货ID不能为空") Long cargoInfoId) {
        List<TCostStorageSettleDTO> result = service.listStorageSettle(cargoInfoId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 取消结算
     *
     * @param storageSettleId
     * @return
     */
    @DeleteMapping("/cancelSettle")
    public Map<String, Object> cancelSettle(@NotNull(message = "结算ID不能为空") Long storageSettleId) {
        service.cancelSettle(storageSettleId);
        return Response.SUCCESS.newBuilder().out("取消成功").toResult();
    }

    /**
     * 审核
     *
     * @param storageSettleId
     * @return
     */
    @PutMapping("/review")
    public Map<String, Object> review(@NotNull(message = "结算ID不能为空") Long storageSettleId) {
        service.review(storageSettleId);
        return Response.SUCCESS.newBuilder().out("审核成功").toResult();
    }

    /**
     * 销审
     *
     * @param storageSettleId
     * @return
     */
    @PutMapping("/cancelReview")
    public Map<String, Object> cancelReview(@NotNull(message = "结算ID不能为空") Long storageSettleId) {
        service.cancelReview(storageSettleId);
        return Response.SUCCESS.newBuilder().out("销审成功").toResult();
    }
}
