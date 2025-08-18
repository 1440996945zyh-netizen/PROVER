package com.yy.ppm.statement.controller.storageCalculate;

import com.yy.common.enums.Response;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.ValidatorUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.enums.ReduceTypeEnum;
import com.yy.ppm.statement.bean.dto.storageCalculate.TBusCargoInfoDTO;
import com.yy.ppm.statement.bean.dto.storageCalculate.TBusCargoInfoQueryDTO;
import com.yy.ppm.statement.bean.dto.storageCalculate.TCostStorageSettleDTO;
import com.yy.ppm.statement.bean.dto.storageCalculate.TCostStorageSettleDetailDTO;
import com.yy.ppm.statement.bean.po.TBusStackFeeReducePO;
import com.yy.ppm.statement.service.storageCalculate.StorageCalculateService;
import org.apache.commons.lang3.StringUtils;
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
 * @Description 堆存费计算
 * @Date 2023-11-24 8:58
 */
@RestController
@RequestMapping("/api/external/storageCalculate")
@Validated
public class StorageCalculateController {

    @Autowired
    private StorageCalculateService service;

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
     * 自定义堆存费免堆期
     *
     * @param stackFeeReduce
     * @return
     */
    @PutMapping("/reduce")
    public Map<String, Object> reduce(@RequestBody TBusStackFeeReducePO stackFeeReduce) {
        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(stackFeeReduce);
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        if (StringUtils.isNotBlank(stackFeeReduce.getReduceType())) {
            if (!ReduceTypeEnum.isContains(stackFeeReduce.getReduceType())) {
                throw new BusinessRuntimeException("错误的优惠类型编码");
            }
            if (ReduceTypeEnum._0.getCode().equals(stackFeeReduce.getReduceType())) {
                stackFeeReduce.setReduceDays(null);
                stackFeeReduce.setReduceEndDate(null);
            }
            if (ReduceTypeEnum._1.getCode().equals(stackFeeReduce.getReduceType())) {
                if (stackFeeReduce.getReduceDays() == null) {
                    throw new BusinessRuntimeException("优惠天数不能为空");
                }
                stackFeeReduce.setReduceEndDate(null);
            }
            if (ReduceTypeEnum._2.getCode().equals(stackFeeReduce.getReduceType())) {
                stackFeeReduce.setReduceDays(null);
                if (stackFeeReduce.getReduceEndDate() == null) {
                    throw new BusinessRuntimeException("减免截止日期不能为空");
                }
            }
        }

        service.reduce(stackFeeReduce);
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }
}
