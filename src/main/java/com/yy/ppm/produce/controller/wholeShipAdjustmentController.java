package com.yy.ppm.produce.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.produce.bean.dto.TDisShipVoyageDTO;
import com.yy.ppm.produce.bean.dto.TWholeShipAdjustmenRes;
import com.yy.ppm.produce.bean.dto.TWholeShipAdjustmentQueryDTO;
import com.yy.ppm.produce.bean.dto.workTicket.TPrdWorkTicketDetailDTO;
import com.yy.ppm.produce.bean.po.TWholeShipAdjustmentExaminePO;
import com.yy.ppm.produce.service.TWholeShipAdjustmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/internal/wholeShip")
@Validated
public class wholeShipAdjustmentController {
    @Autowired
    TWholeShipAdjustmentService service;

    @GetMapping("/getList")
    public Map<String, Object> getList(TWholeShipAdjustmentQueryDTO queryDTO){
        if(queryDTO==null){
            throw new BusinessRuntimeException("缺少必要的请求参数");
        }
        if(queryDTO.getStartTime()==null){
            throw new BusinessRuntimeException("请选择开始时间");
        }
        if(queryDTO.getEndTime()==null){
            throw new BusinessRuntimeException("请选择结束时间");
        }
        Pages<TDisShipVoyageDTO> result = service.getList(queryDTO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    @GetMapping("/getShipPLanTicketStatus")
    public Map<String, Object> getShipPLanTicketStatus(TWholeShipAdjustmentQueryDTO queryDTO){
        if(queryDTO==null){
            throw new BusinessRuntimeException("缺少必要的请求参数");
        }
        if(queryDTO.getShipVoyageItemId()==null){
            throw new BusinessRuntimeException("请先选择船");
        }
        Map<String, Object> resultMap = service.getShipPLanTicketStatus(queryDTO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(resultMap);
    }

    @GetMapping("/getTicketListForChange")
    @Log(title ="整船调整获取详情值",value = OperateTypeEnum.QUERY)
    public Map<String, Object> getTicketListForChange(TWholeShipAdjustmentQueryDTO queryDTO){
        TWholeShipAdjustmenRes result = service.getTicketListForChange1128(queryDTO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    @PutMapping("/updateTickeyDetail/{shipvoyageItemId}")
    @Log(title ="整船调整更新",value = OperateTypeEnum.UPDATE)
    public Map<String, Object> updateTickeyDetail(@PathVariable @NotNull(message = "船名航次信息不能为空") Long shipvoyageItemId, @RequestBody List<TPrdWorkTicketDetailDTO> reqList){
        service.updateTickeyDetail1128(shipvoyageItemId,reqList);
        return Response.SUCCESS.newBuilder().out("更新成功").toResult();
    }
    @GetMapping("/updateShipAdjustStatus/{shipvoyageItemId}")
    @Log(title ="整船调整同步更新船舶状态",value = OperateTypeEnum.UPDATE)
    public Map<String, Object> updateShipAdjustStatus(@PathVariable @NotNull(message = "船名航次信息不能为空") Long shipvoyageItemId, String allotType){
        service.updateShipAdjustStatus(shipvoyageItemId,allotType);
        return Response.SUCCESS.newBuilder().out("撤销成功").toResult();
    }

    @PutMapping("/personClearConfirm")
    @Log(title ="整船调整人员完工确认",value = OperateTypeEnum.UPDATE)
    public Map<String, Object> personClearConfirm(TWholeShipAdjustmentExaminePO po){
        service.personClearConfirm(po);
        return Response.SUCCESS.newBuilder().out("人员完工确认成功").toResult();
    }

    @PutMapping("/macClearConfirm")
    @Log(title ="整船调整机械完工确认",value = OperateTypeEnum.UPDATE)
    public Map<String, Object> macClearConfirm(TWholeShipAdjustmentExaminePO po){
        service.macClearConfirm(po);
        return Response.SUCCESS.newBuilder().out("机械完工确认成功").toResult();
    }

    @GetMapping("/updateShipAdjustClearPersonStatus/{shipvoyageItemId}")
    @Log(title ="整船调整完工确认人员审核撤销",value = OperateTypeEnum.UPDATE)
    public Map<String, Object> updateShipAdjustClearPersonStatus(@PathVariable @NotNull(message = "船名航次信息不能为空") Long shipvoyageItemId){
        service.updateShipAdjustClearPersonStatus(shipvoyageItemId);
        return Response.SUCCESS.newBuilder().out("人员撤销成功").toResult();
    }

    @GetMapping("/updateShipAdjustClearMacStatus/{shipvoyageItemId}")
    @Log(title ="整船调整完工确认机械审核撤销",value = OperateTypeEnum.UPDATE)
    public Map<String, Object> updateShipAdjustClearMacStatus(@PathVariable @NotNull(message = "船名航次信息不能为空") Long shipvoyageItemId){
        service.updateShipAdjustClearMacStatus(shipvoyageItemId);
        return Response.SUCCESS.newBuilder().out("机械撤销成功").toResult();
    }
}
