package com.yy.ppm.business.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.business.bean.dto.TBusTrustCargoDTO;
import com.yy.ppm.business.bean.dto.TBusTrustCargoQueryDTO;
import com.yy.ppm.business.bean.dto.TBusVehicleTransferDTO;
import com.yy.ppm.business.service.TBusVehicleTransferService;
import com.yy.ppm.dispatch.bean.dto.TDisLogDTO;
import com.yy.ppm.statement.bean.dto.TMiscBillingDTO;
import com.yy.ppm.system.bean.dto.SysRoleDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/internal/tBusVehicleTransfer")
@Validated
public class TBusVehicleTransferController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(TBusVehicleTransferController.class);

    @Autowired
    private TBusVehicleTransferService tBusVehicleTransferService;

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('business:vehicleTransfer:query')")
    @Log(title ="查询指令票货列表",value = OperateTypeEnum.QUERY)
    public Map<String, Object> getList(TBusTrustCargoQueryDTO searchDTO) {
    	final String methodName = "TBusVehicleTransferController:getList";
		LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<TBusTrustCargoQueryDTO> pages = tBusVehicleTransferService.getList(searchDTO);

        LOGGER.exit( methodName + "result:" + pages);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }
    
    /**
     * 查询指令关联配工信息列表
     * @param trustId
     * @param trustCargoId
     * @return
     */
    @GetMapping("/getTrustCagroDispatchSecondary")
    @PreAuthorize("hasAuthority('business:vehicleTransfer:query')")
    @Log(title ="查询指令票货配工列表",value = OperateTypeEnum.QUERY)
    public Map<String, Object> getTrustCagroDispatchSecondary(Long trustId, Long trustCargoId) {
        final String methodName = "TBusVehicleTransferController:getTrustCagroDispatchSecondary";
        LOGGER.enter(methodName + "[start]", "trustId:" +  trustId + ", trustCargoId:" + trustCargoId);
        
        List<Map<String, Object>> resultList = tBusVehicleTransferService.getTrustCagroDispatchSecondary(trustId, trustCargoId);
        
        LOGGER.exit(methodName + "[end]", "result:" + resultList);
        return Response.SUCCESS.newBuilder().toResult(resultList);
    }
    
    /**
     * 新增倒运设备信息
     * @param tBusVehicleTransferDTO
     * @return
     */
    @PostMapping("/insertVehicleTransferList")
    @PreAuthorize("hasAuthority('business:vehicleTransfer:insert')")
    @Log(title ="新增倒运设备信息",value = OperateTypeEnum.INSERT)
    public Map<String, Object> insertVehicleTransferList(@RequestBody TBusVehicleTransferDTO tBusVehicleTransferDTO) {
        final String methodName = "TBusVehicleTransferController:insertVehicleTransferList";
        LOGGER.enter(methodName + "[start]", "tBusVehicleTransferDTO:" + tBusVehicleTransferDTO);

        tBusVehicleTransferService.insertVehicleTransferList(tBusVehicleTransferDTO);
        
        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().toResult();
    }
    
    /**
     * 查询过磅信息
     * @param trustCargoId
     * @return
     */
    @GetMapping("/getVehicleTransferList")
    @PreAuthorize("hasAuthority('business:vehicleTransfer:query')")
    @Log(title ="查询指令票货配工列表",value = OperateTypeEnum.QUERY)
    public Map<String, Object> getVehicleTransferList(Long trustCargoId) {
        final String methodName = "TBusVehicleTransferController:getVehicleTransferList";
        LOGGER.enter(methodName + "[start]", "trustCargoId:" + trustCargoId);
        
        List<TBusVehicleTransferDTO> resultList = tBusVehicleTransferService.getVehicleTransferList(trustCargoId);
        
        LOGGER.exit(methodName + "[end]", "result:" + resultList);
        return Response.SUCCESS.newBuilder().toResult(resultList);
    }

    /**
     * 查询二次过磅信息
     * @param trustCargoId
     * @return
     */
    @GetMapping("/getSecondWeighTon")
    @Log(title ="查询指令票货查询是否二次过磅",value = OperateTypeEnum.QUERY)
    public Map<String, Object> getSecondWeighTon(Long trustCargoId) {
        final String methodName = "TBusVehicleTransferController:getSecondWeighTon";
        LOGGER.enter(methodName + "[start]", "trustCargoId:" + trustCargoId);

        TBusTrustCargoDTO result = tBusVehicleTransferService.getTrustCargoById(trustCargoId);
        String isSecondWeigh = "0";
        if (result != null && StringUtils.isNotBlank(result.getIsSecondWeigh())) {
            isSecondWeigh = result.getIsSecondWeigh();
        }
        LOGGER.exit(methodName + "[end]", "result:" + isSecondWeigh);
        return Response.SUCCESS.newBuilder().toResult(isSecondWeigh);
    }

    /**
     * 修改内倒类型
     *
     * @param id
     * @param innertransportType
     * @return
     */
/*    @PutMapping("/updateInnertransportType")
    @PreAuthorize("hasAuthority('business:vehicleTransfer:updateInnertransportType')")
    public Map<String, Object> updateInnertransportType(
            @NotNull(message = "指令票货ID不能为空") Long id,
            @NotBlank(message = "内倒类型不能为空") String innertransportType
    ) {
        final String methodName = "TBusVehicleTransferController:updateInnertransportType";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        tBusVehicleTransferService.updateInnertransportType(id, innertransportType);

        LOGGER.exit(methodName + "[end]", "");
        return Response.SUCCESS.newBuilder().out("更新成功").toResult();
    }*/

    /**
     * 修改
     * @param tBusTrustCargoDTO
     * @return
     */
    @PutMapping("/updateInvert")
    public Map<String, Object> update(@RequestBody TBusTrustCargoDTO tBusTrustCargoDTO) {
        final String methodName = "TDisLogController:update";
        LOGGER.enter(methodName + "[start]", "tBusTrustCargoDTO:" +  tBusTrustCargoDTO);

        boolean flag = tBusVehicleTransferService.doSave(tBusTrustCargoDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }
    /**
     * 修改内倒类型
     *
     * @param cargoCode
     * @return
     */
    @PutMapping("/getEmptyHeavy")
    public Map<String, Object> getEmptyHeavyList(@NotBlank(message = "货物代码不能为空") String cargoCode) {
        final String methodName = "TBusVehicleTransferController:getEmptyHeavyList";

        TBusTrustCargoDTO tBusTrustCargoDTO = tBusVehicleTransferService.getEmptyHeavy(cargoCode);

        LOGGER.exit(methodName + "[end]", "");
        return Response.SUCCESS.newBuilder().toResult(tBusTrustCargoDTO);
    }
    /**
     * 修改主界面状态
     */
    @PutMapping("/changeStatus")
    public Map<String, Object> changeStatus(@RequestBody TBusTrustCargoDTO tBusTrustCargoDTO) {

        final String methodName = "TBusVehicleTransferController:update";
        LOGGER.enter(methodName + "[start]", "tBusTrustCargoDTO:" + tBusTrustCargoDTO);

        int count = tBusVehicleTransferService.changeStatus(tBusTrustCargoDTO);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("修改成功").toResult(count);
    }


    /**
     * 查询已派
     * @param trustId
     * @param trustCargoId
     * @return
     */
    @GetMapping("/getVehicleList")
    @PreAuthorize("hasAuthority('business:vehicleTransfer:query')")
    @Log(title ="查询指令票货配工列表",value = OperateTypeEnum.QUERY)
    public Map<String, Object> getVehicleList(Long trustId, Long trustCargoId) {
        final String methodName = "TBusVehicleTransferController:getTrustCagroDispatchSecondary";
        LOGGER.enter(methodName + "[start]", "trustId:" +  trustId + ", trustCargoId:" + trustCargoId);

        List<TBusVehicleTransferDTO> resultList = tBusVehicleTransferService.getVehicleList(trustId, trustCargoId);

        LOGGER.exit(methodName + "[end]", "result:" + resultList);
        return Response.SUCCESS.newBuilder().toResult(resultList);
    }

    /**
     * 修改派工界面状态
     */
    @PutMapping("/changeDetailStatus")
    public Map<String, Object> changeDetailStatus(@RequestBody TBusVehicleTransferDTO tBusVehicleTransferDTO) {

        final String methodName = "TBusVehicleTransferController:update";
        LOGGER.enter(methodName + "[start]", "tBusVehicleTransferDTO:" + tBusVehicleTransferDTO);

        int count = tBusVehicleTransferService.changeDetailStatus(tBusVehicleTransferDTO);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("修改成功").toResult(count);
    }

    /**
     * 查询费用
     * @param trustCargoId
     * @return
     */
    @GetMapping("/getMiscFeeList")
    public Map<String, Object> getMiscFeeList(Long trustCargoId) {
        final String methodName = "TBusVehicleTransferController:getMiscFeeList";
        LOGGER.enter(methodName + "[start]", "trustCargoId:" + trustCargoId);

        List<TMiscBillingDTO> resultList = tBusVehicleTransferService.getMiscFeeList(trustCargoId);

        LOGGER.exit(methodName + "[end]", "result:" + resultList);
        return Response.SUCCESS.newBuilder().toResult(resultList);
    }

}
