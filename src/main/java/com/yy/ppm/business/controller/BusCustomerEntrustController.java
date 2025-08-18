package com.yy.ppm.business.controller;

import com.yy.common.enums.Response;
import com.yy.common.page.Pages;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.TBusCustomerEntrustDTO;
import com.yy.ppm.business.bean.dto.TBusCustomerEntrustReqDTO;
import com.yy.ppm.business.bean.dto.TBusEntrustDetailDTO;
import com.yy.ppm.business.bean.dto.TBusEntrustDetailReqDTO;
import com.yy.ppm.business.service.BusCustomerEntrustService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/external/customerEntrust")
@Validated
public class BusCustomerEntrustController {

    @Autowired
    private BusCustomerEntrustService customerEntrustService;

    /**
     * 客户委托主列表
     *
     * @param query
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(TBusCustomerEntrustReqDTO query) {


        Pages<TBusCustomerEntrustDTO> result = customerEntrustService.getList(query);
        return Response.SUCCESS.newBuilder().toResult(result);
    }
    /**
     * 通过id获取客户委托主列表
     *
     * @param entrustId
     * @return
     */
    @GetMapping("/getcustomerEntrustById/{entrustId}")
    public Map<String, Object> getcustomerEntrustById(@NotNull(message = "客户委托单id不能为空") @PathVariable Long entrustId) {
        TBusCustomerEntrustDTO result = customerEntrustService.getEntrust(entrustId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }
    /**
     * 客户委托单下发集港通知单的数据准备
     *
     * @param entrustId
     * @return
     */
    @GetMapping("/getCustomerEntrustForAddTrust/{entrustId}")
    public Map<String, Object> getCustomerEntrustForAddTrust(@NotNull(message = "客户委托单id不能为空") @PathVariable Long entrustId) {
        TBusCustomerEntrustDTO result = customerEntrustService.getCustomerEntrustForAddTrust(entrustId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }


    /**
     * 客户委托子表
     *
     * @param query
     * @return
     */
    @GetMapping("/getDetailList")
    public Map<String, Object> getDetailList(TBusEntrustDetailReqDTO query) {


        List<TBusEntrustDetailDTO> result = customerEntrustService.getDetailList(query);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 客户委托保存更新
     *
     * @param dto
     * @return
     */
    @PostMapping("/saveOrUpdate")
    public Map<String, Object> saveOrUpdate(@RequestBody TBusCustomerEntrustDTO dto) {
        if (dto.getCargoList().isEmpty()) {
            throw new BusinessRuntimeException("至少添加一条货物信息再进行保存");
        }
        customerEntrustService.saveOrUpdate(dto);
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }

    /**
     * 删除客户委托单
     *
     * @param entrustId
     * @return
     */
    @DeleteMapping("/delCustomerEntrust/{entrustId}")
    public Map<String, Object> delCustomerEntrust( @NotNull(message = "提货委托单ID必传") @PathVariable Long entrustId) {

        Boolean result = customerEntrustService.delCustomerEntrust(entrustId);
        return Response.SUCCESS.newBuilder().out(result?"删除成功":"删除失败").toResult();
    }

}
