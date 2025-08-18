package com.yy.ppm.dispatch.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.ValidatorUtils;
import com.yy.framework.annotation.Log;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.enums.InOutPortEnum;
import com.yy.ppm.common.enums.LoadUnloadEnum;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageDTO;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageQueryDTO;
import com.yy.ppm.dispatch.service.MSjsbLogService;
import com.yy.ppm.dispatch.service.TDisShipVoyageService;
import com.yy.ppm.master.controller.MShipController;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Author linqi
 * @Description 船舶预报
 * @Date 2023-07-04 10:44
 */
@RestController
@RequestMapping("/api/external/disShipVoyage")
@Validated
public class TDisShipVoyageController {

    @Autowired
    private TDisShipVoyageService tDisShipVoyageService;

    private static final MicroLogger LOGGER = new MicroLogger(MShipController.class);

    @PostMapping("/insertDisShipVoyageForecast")
    @PreAuthorize("hasAuthority('dispatch:shipForecast:add')")
    public Map<String, Object> insertDisShipVoyageForecast(@RequestBody TDisShipvoyageDTO disShipvoyage) {
        ValidatorUtils.FieldBean bean;
        if ((bean = ValidatorUtils.validator(disShipvoyage, true, "id")).isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        if (!InOutPortEnum.isContains(disShipvoyage.getImpExp())) {
            throw new BusinessRuntimeException("错误的进出口编码");
        }
        if (InOutPortEnum.IN.getCode().equals(disShipvoyage.getImpExp())) {
            if (!LoadUnloadEnum.UNLOAD.getName().equals(disShipvoyage.getLoadUnload())) {
                throw new BusinessRuntimeException("进口仅支持卸");
            }
            if (disShipvoyage.getIn() == null) {
                throw new BusinessRuntimeException("进口不能为空");
            }
            if ((bean = ValidatorUtils.validator(disShipvoyage.getIn())).isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
        }
        if (InOutPortEnum.OUT.getCode().equals(disShipvoyage.getImpExp())) {
            if (!LoadUnloadEnum.LOAD.getName().equals(disShipvoyage.getLoadUnload())) {
                throw new BusinessRuntimeException("出口仅支持装");
            }
            if (disShipvoyage.getOut() == null) {
                throw new BusinessRuntimeException("出口不能为空");
            }
            if ((bean = ValidatorUtils.validator(disShipvoyage.getOut())).isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
        }
        if (InOutPortEnum.INOUT.getCode().equals(disShipvoyage.getImpExp())) {
            if (!LoadUnloadEnum.LOAD_UNLOAD.getName().equals(disShipvoyage.getLoadUnload())) {
                throw new BusinessRuntimeException("进出口仅支持装卸");
            }
            if (disShipvoyage.getIn() == null) {
                throw new BusinessRuntimeException("进口不能为空");
            }
            if (disShipvoyage.getOut() == null) {
                throw new BusinessRuntimeException("出口不能为空");
            }
            if ((bean = ValidatorUtils.validator(disShipvoyage.getIn())).isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
            if ((bean = ValidatorUtils.validator(disShipvoyage.getOut())).isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
        }

        tDisShipVoyageService.insertDisShipVoyageForecast(disShipvoyage);
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    @PostMapping("/insertDisShipVoyage")
    @PreAuthorize("hasAuthority('dispatch:shipForecast:add')")
    @Log(title = "生产系统船舶预报新增", value = OperateTypeEnum.INSERT)
    public Map<String, Object> insertDisShipVoyage(@RequestBody TDisShipvoyageDTO disShipvoyage) {
        ValidatorUtils.FieldBean bean;
        if ((bean = ValidatorUtils.validator(disShipvoyage, true, "id")).isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        if (!InOutPortEnum.isContains(disShipvoyage.getImpExp())) {
            throw new BusinessRuntimeException("错误的进出口编码");
        }

        if ("1".equals(disShipvoyage.getIsPayment()) && disShipvoyage.getPaymentAmount() == null) {
            throw new BusinessRuntimeException("预缴金额不能为空");
        }

        if (InOutPortEnum.IN.getCode().equals(disShipvoyage.getImpExp())) {
            if (!LoadUnloadEnum.UNLOAD.getName().equals(disShipvoyage.getLoadUnload())) {
                throw new BusinessRuntimeException("进口仅支持卸");
            }
            if (disShipvoyage.getIn() == null) {
                throw new BusinessRuntimeException("进口不能为空");
            }
            if ((bean = ValidatorUtils.validator(disShipvoyage.getIn())).isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
        }
        if (InOutPortEnum.OUT.getCode().equals(disShipvoyage.getImpExp())) {
            if (!LoadUnloadEnum.LOAD.getName().equals(disShipvoyage.getLoadUnload())) {
                throw new BusinessRuntimeException("出口仅支持装");
            }
            if (disShipvoyage.getOut() == null) {
                throw new BusinessRuntimeException("出口不能为空");
            }
            if ((bean = ValidatorUtils.validator(disShipvoyage.getOut())).isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
        }
        if (InOutPortEnum.INOUT.getCode().equals(disShipvoyage.getImpExp())) {
            if (!LoadUnloadEnum.LOAD_UNLOAD.getName().equals(disShipvoyage.getLoadUnload())) {
                throw new BusinessRuntimeException("进出口仅支持装卸");
            }
            if (disShipvoyage.getIn() == null) {
                throw new BusinessRuntimeException("进口不能为空");
            }
            if (disShipvoyage.getOut() == null) {
                throw new BusinessRuntimeException("出口不能为空");
            }
            if ((bean = ValidatorUtils.validator(disShipvoyage.getIn())).isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
            if ((bean = ValidatorUtils.validator(disShipvoyage.getOut())).isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
        }
        tDisShipVoyageService.insertDisShipVoyage(disShipvoyage);
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    @PutMapping("/updateDisShipVoyage")
    @PreAuthorize("hasAuthority('dispatch:shipForecast:update')")
    @Log(title ="更新船舶预报信息:updateDisShipVoyage",value = OperateTypeEnum.UPDATE)
    public Map<String, Object> updateDisShipVoyage(@RequestBody TDisShipvoyageDTO disShipvoyage) {
//        LOGGER.enter("TDisShipVoyageController:updateDisShipVoyage");
        ValidatorUtils.FieldBean bean;
        if ((bean = ValidatorUtils.validator(disShipvoyage)).isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }

        if ("1".equals(disShipvoyage.getIsPayment()) && disShipvoyage.getPaymentAmount() == null) {
            throw new BusinessRuntimeException("预缴金额不能为空");
        }

        if (InOutPortEnum.IN.getCode().equals(disShipvoyage.getImpExp())) {
            if (!LoadUnloadEnum.UNLOAD.getName().equals(disShipvoyage.getLoadUnload())) {
                throw new BusinessRuntimeException("进口仅支持卸");
            }
            if (disShipvoyage.getIn() == null) {
                throw new BusinessRuntimeException("进口不能为空");
            }
            if ((bean = ValidatorUtils.validator(disShipvoyage.getIn())).isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
        }
        if (InOutPortEnum.OUT.getCode().equals(disShipvoyage.getImpExp())) {
            if (!LoadUnloadEnum.LOAD.getName().equals(disShipvoyage.getLoadUnload())) {
                throw new BusinessRuntimeException("出口仅支持装");
            }
            if (disShipvoyage.getOut() == null) {
                throw new BusinessRuntimeException("出口不能为空");
            }
            if ((bean = ValidatorUtils.validator(disShipvoyage.getOut())).isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
        }
        if (InOutPortEnum.INOUT.getCode().equals(disShipvoyage.getImpExp())) {
            if (!LoadUnloadEnum.LOAD_UNLOAD.getName().equals(disShipvoyage.getLoadUnload())) {
                throw new BusinessRuntimeException("进出口仅支持装卸");
            }
            if (disShipvoyage.getIn() == null) {
                throw new BusinessRuntimeException("进口不能为空");
            }
            if (disShipvoyage.getOut() == null) {
                throw new BusinessRuntimeException("出口不能为空");
            }
            if ((bean = ValidatorUtils.validator(disShipvoyage.getIn())).isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
            if ((bean = ValidatorUtils.validator(disShipvoyage.getOut())).isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
        }
        String msg = tDisShipVoyageService.updateDisShipVoyage(disShipvoyage);
        return Response.SUCCESS.newBuilder().out(msg).toResult();
    }

    @DeleteMapping("/deleteDisShipvoyage")
    @PreAuthorize("hasAuthority('dispatch:shipForecast:delete')")
    public Map<String, Object> deleteDisShipvoyage(@RequestParam("ids") @NotEmpty(message = "主键id不能为空") List<Long> ids) {
        tDisShipVoyageService.deleteDisShipvoyage(ids);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }
    @Autowired
    private MSjsbLogService sjsbLogService;

    @GetMapping("/listDisShipVoyage")
    @PreAuthorize("hasAuthority('dispatch:shipForecast:query')")
    public Map<String, Object> listDisShipVoyage(TDisShipvoyageQueryDTO query, PageParameter parameter) {
        Pages<TDisShipvoyageDTO> result = tDisShipVoyageService.listDisShipVoyage(query, parameter);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    @GetMapping("/downShipWorkReport")
    @PreAuthorize("hasAuthority('dispatch:shipForecast:query')")
    public Map<String, Object> downShipWorkReport(Long shipVoyageId,HttpServletResponse response) {
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        tDisShipVoyageService.downShipWorkReport(shipVoyageId, response);
        return Response.SUCCESS.newBuilder().toResult();
    }

    @PutMapping("/rejectionDisShipvoyage")
    @PreAuthorize("hasAuthority('dispatch:shipForecast:rejection')")
    public Map<String, Object> rejectionDisShipvoyage(@RequestParam("ids") @NotEmpty(message = "主键id不能为空") List<Long> ids, String rejectionRemark) {
        tDisShipVoyageService.rejectionDisShipvoyage(ids, rejectionRemark);
        return Response.SUCCESS.newBuilder().out("拒收成功").toResult();
    }

    @PutMapping("/voidDisShipvoyage")
    @PreAuthorize("hasAuthority('dispatch:shipForecast:cancel')")
    public Map<String, Object> voidDisShipvoyage(@RequestParam("ids") @NotEmpty(message = "主键id不能为空") List<Long> ids, String delRemark) {
        tDisShipVoyageService.voidDisShipvoyage(ids, delRemark);
        return Response.SUCCESS.newBuilder().out("作废成功").toResult();
    }

    @PutMapping("/receiveDisShipvoyage")
    @PreAuthorize("hasAuthority('dispatch:shipForecast:receive')")
    public Map<String, Object> receiveDisShipvoyage(@RequestParam("ids") @NotEmpty(message = "主键id不能为空") List<Long> ids) {
        tDisShipVoyageService.receiveDisShipvoyage(ids);
        return Response.SUCCESS.newBuilder().out("接收成功").toResult();
    }

    /**
     * 根据内外贸和吨数判断价格
     * @param dwt
     * @param tradeType
     * @return
     */
    @PutMapping("/changeAmount")
    public Map<String, Object> changeAmount( BigDecimal dwt ,String tradeType) {
        Long amount = tDisShipVoyageService.changeAmount(dwt,tradeType);
        return Response.SUCCESS.newBuilder().toResult(amount);
    }

    /**
     * 根据内外贸和吨数判断价格
     * @param shipId
     * @return
     */
    @PutMapping("/lastArrivalType")
    public Map<String, Object> lastArrivalType(Long voyageId,Long shipId) {
        String lastArrivalType = tDisShipVoyageService.getLastArrivalType(voyageId,shipId);
        return Response.SUCCESS.newBuilder().toResult(lastArrivalType);
    }
    /**
     * 获取二级货类
     */
    @GetMapping("/listSecCargoCate")
    public Map<String, Object> listSecCargoCate() {
        List<Map<String, Object>> list = tDisShipVoyageService.getSecCargoCate();
        return Response.SUCCESS.newBuilder().toResult(list);
    }

}
