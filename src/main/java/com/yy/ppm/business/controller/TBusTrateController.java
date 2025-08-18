package com.yy.ppm.business.controller;

import com.yy.common.enums.Response;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.ValidatorUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.trate.*;
import com.yy.ppm.business.service.TBusTrateService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Auther linqi
 * @Description 合同优惠
 * @Date 2023-11-08 11:30
 */
@RestController
@RequestMapping("/api/external/trate")
@Validated
public class TBusTrateController {

    @Autowired
    private TBusTrateService tBusTrateService;

    /**
     * 是否阶梯费率 0否
     */
    public static final String IS_TIERED_RATE_0 = "0";

    /**
     * 是否阶梯费率 1是
     */
    public static final String IS_TIERED_RATE_1 = "1";

    /**
     * 新增阶梯费率
     *
     * @param trate
     * @return
     */
    @PostMapping("/insertTrate")
    public Map<String, Object> insertTrate(@RequestBody TBusTrateDTO trate) {
        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(trate, true, "id");
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        boolean bool = trate.getContracts().size() != trate.getContracts().stream().map(TBusTrateContractDTO::getContractNo).distinct().count();
        if (bool) {
            throw new BusinessRuntimeException("重复的合同编号");
        }
        bool = trate.getCustomers().size() != trate.getCustomers().stream().map(TBusTrateCustomerDTO::getCustomerId).distinct().count();
        if (bool) {
            throw new BusinessRuntimeException("重复的客户ID");
        }
        if (trate.getStartTime().compareTo(trate.getEndTime()) > 0) {
            throw new BusinessRuntimeException("错误的有效期区间");
        }
        bean = ValidatorUtils.validator(trate.getContracts());
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        bean = ValidatorUtils.validator(trate.getCustomers());
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        bean = ValidatorUtils.validator(trate.getItems());
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        for (TBusTrateItemDTO item : trate.getItems()) {
            bool = item.getCargos().size() != item.getCargos().stream().map(TBusTrateItemCargoDTO::getCargoCode).distinct().count();
            if (bool) {
                throw new BusinessRuntimeException("重复的货物编码");
            }
            if (Stream.of(IS_TIERED_RATE_0, IS_TIERED_RATE_1).noneMatch(v2 -> v2.equals(item.getIsTieredRate()))) {
                throw new BusinessRuntimeException("错误的是否阶梯费率");
            }
            if (IS_TIERED_RATE_0.equals(item.getIsTieredRate())) {
                if (item.getPreferentialRate() == null) {
                    throw new BusinessRuntimeException("优惠费率值不能为空");
                }
            } else {
                if (CollectionUtils.isEmpty(item.getDetails())) {
                    throw new BusinessRuntimeException("明细不能为空");
                }
            }
            bean = ValidatorUtils.validator(item.getCargos());
            if (bean.isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
            bean = ValidatorUtils.validator(item.getDetails());
            if (bean.isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
        }
        List<String> cargoCodes = trate.getItems().stream()
                .flatMap(v1 -> v1.getCargos().stream().flatMap(v2 -> Stream.of(v2.getCargoCode())))
                .collect(Collectors.toList());
        if (cargoCodes.size() != cargoCodes.stream().distinct().count()) {
            throw new BusinessRuntimeException("不允许相同的货物存在于多个条目中");
        }
        tBusTrateService.verifyUnique(trate);

        tBusTrateService.insertTrate(trate);
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 删除阶梯费率
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deleteTrate")
    public Map<String, Object> deleteTrate(@NotNull(message = "阶梯费率ID不能为空") Long id) {
        tBusTrateService.deleteTrate(id);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 修改阶梯费率
     *
     * @param trate
     * @return
     */
    @PutMapping("/updateTrate")
    public Map<String, Object> deleteTrate(@RequestBody TBusTrateDTO trate) {
        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(trate);
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        boolean bool = trate.getContracts().size() != trate.getContracts().stream().map(TBusTrateContractDTO::getContractNo).distinct().count();
        if (bool) {
            throw new BusinessRuntimeException("重复的合同编号");
        }
        bool = trate.getCustomers().size() != trate.getCustomers().stream().map(TBusTrateCustomerDTO::getCustomerId).distinct().count();
        if (bool) {
            throw new BusinessRuntimeException("重复的客户ID");
        }
        if (trate.getStartTime().compareTo(trate.getEndTime()) > 0) {
            throw new BusinessRuntimeException("错误的有效期区间");
        }
        bean = ValidatorUtils.validator(trate.getContracts());
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        bean = ValidatorUtils.validator(trate.getCustomers());
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        bean = ValidatorUtils.validator(trate.getItems());
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        for (TBusTrateItemDTO item : trate.getItems()) {
            bool = item.getCargos().size() != item.getCargos().stream().map(TBusTrateItemCargoDTO::getCargoCode).distinct().count();
            if (bool) {
                throw new BusinessRuntimeException("重复的货物编码");
            }
            if (Stream.of(IS_TIERED_RATE_0, IS_TIERED_RATE_1).noneMatch(v2 -> v2.equals(item.getIsTieredRate()))) {
                throw new BusinessRuntimeException("错误的是否阶梯费率");
            }
            if (IS_TIERED_RATE_0.equals(item.getIsTieredRate())) {
                if (item.getPreferentialRate() == null) {
                    throw new BusinessRuntimeException("优惠费率值不能为空");
                }
            } else {
                if (CollectionUtils.isEmpty(item.getDetails())) {
                    throw new BusinessRuntimeException("明细不能为空");
                }
            }
            bean = ValidatorUtils.validator(item.getCargos());
            if (bean.isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
            bean = ValidatorUtils.validator(item.getDetails());
            if (bean.isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
        }
        List<String> cargoCodes = trate.getItems().stream()
                .flatMap(v1 -> v1.getCargos().stream().flatMap(v2 -> Stream.of(v2.getCargoCode())))
                .collect(Collectors.toList());
        if (cargoCodes.size() != cargoCodes.stream().distinct().count()) {
            throw new BusinessRuntimeException("不允许相同的货物存在于多个条目中");
        }
        tBusTrateService.verifyUnique(trate, trate.getId());

        tBusTrateService.updateTrate(trate);
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 阶梯费率列表
     *
     * @param parameter
     * @param query
     * @return
     */
    @GetMapping("/listTrate")
    public Map<String, Object> listTrate(PageParameter parameter, TBusTrateQueryDTO query) {
        Pages<TBusTrateDTO> result = tBusTrateService.listTrate(parameter, query);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 发布
     *
     * @param id
     * @return
     */
    @PutMapping("/release")
    public Map<String, Object> release(@NotNull(message = "阶梯费率ID不能为空") Long id) {
        tBusTrateService.release(id);
        return Response.SUCCESS.newBuilder().out("发布成功").toResult();
    }

    /**
     * 撤销发布
     *
     * @param id
     * @return
     */
    @PutMapping("/cancelRelease")
    public Map<String, Object> cancelRelease(@NotNull(message = "阶梯费率ID不能为空") Long id) {
        tBusTrateService.cancelRelease(id);
        return Response.SUCCESS.newBuilder().out("撤销发布成功").toResult();
    }

    /**
     * 修改原始累积量
     *
     * @param trateItemId
     * @param originAccNumber
     * @return
     */
    @PutMapping("/updateOriginAccNumber")
    public Map<String, Object> updateOriginAccNumber(@NotNull(message = "阶梯费率条目ID不能为空") Long trateItemId, BigDecimal originAccNumber) {
        tBusTrateService.updateOriginAccNumber(trateItemId, originAccNumber);
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }
}
