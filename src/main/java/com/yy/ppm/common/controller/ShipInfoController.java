package com.yy.ppm.common.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.bean.dto.TDisPoundInfoDTO;
import com.yy.ppm.common.service.ShipInfoService;
import com.yy.ppm.dispatch.bean.dto.TDisCostInfoPO;
import com.yy.ppm.dispatch.bean.dto.disShipDynamic.TDisShipDynamicDTO;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageDTO;
import com.yy.ppm.produce.bean.dto.portStorage.InoutDetailQueryDTO;
import com.yy.ppm.produce.bean.dto.portStorage.TPrdPortStorageDTO;
import com.yy.ppm.produce.bean.dto.portStorage.TPrdPortStorageGbCargoInfoDTO;
import org.apache.commons.lang3.StringUtils;
import com.yy.ppm.dispatch.bean.po.TDisLowerCabinPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户管理
 * @author yy
 * @date 2021年2月19日14:13:03
 */
@RestController
@RequestMapping(value = "/api/ShipInfo")
@Validated
public class ShipInfoController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(ShipInfoController.class);

	@Autowired
	private ShipInfoService shipInfoService;

	/**
	 * 获取
	 * @return
	 */
	@GetMapping("/getSteps")
	public Map<String, Object> getSteps(Long shipVoyageId) {
		final String methodName = "ShipInfoController:getSteps";
		LOGGER.enter(methodName + "[start]", "getSteps:" + shipVoyageId);
		Map<String,Object> result = shipInfoService.getSteps(shipVoyageId);
		LOGGER.exit(methodName + "result:" + result);
		return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
	}

	/**
	 * 获取
	 * @return
	 */
	@GetMapping("/getShipVoyageInfo")
	public Map<String, Object> getShipVoyageInfo(Long shipVoyageId) {
		final String methodName = "ShipInfoController:getShipVoyageInfo";
		LOGGER.enter(methodName + "[start]", "getShipVoyageInfo:" + shipVoyageId);
		TDisShipvoyageDTO result = shipInfoService.getShipVoyageInfo(shipVoyageId);
		LOGGER.exit(methodName + "result:" + result);
		return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
	}

	/**
	 * 获取
	 * @return
	 */
	@GetMapping("/getShipDynamicInfo")
	public Map<String, Object> getShipDynamicInfo(Long shipVoyageId) {
		final String methodName = "ShipInfoController:getShipDynamicInfo";
		LOGGER.enter(methodName + "[start]", "getShipVoyageInfo:" + shipVoyageId);
		Map<String, Object> result = shipInfoService.getShipDynamicInfo(shipVoyageId);
		LOGGER.exit(methodName + "result:" + result);
		return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
	}

	/**
	 * 获取
	 * @return
	 */
	@GetMapping("/getShipDoorInfo")
	public Map<String, Object> getShipDoorInfo(TDisLowerCabinPO tDisLowerCabinPO) {
		final String methodName = "ShipInfoController:getShipDoorInfo";
		LOGGER.enter(methodName + "[start]", "getShipDoorInfo:" + tDisLowerCabinPO);
		List<TDisLowerCabinPO> result = shipInfoService.getShipDoorInfo(tDisLowerCabinPO);
		LOGGER.exit(methodName + "result:" + result);
		return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
	}
	@GetMapping("/getPortTrendsInfo")
	public Map<String, Object> getPortTrendsInfo(Long shipVoyageId) {
		final String methodName = "ShipInfoController:getPortTrendsInfo";
		LOGGER.enter(methodName + "[start]", "getPortTrendsInfo:" + shipVoyageId);
		List<TPrdPortStorageGbCargoInfoDTO> result = shipInfoService.getPortTrendsInfo(shipVoyageId);
		LOGGER.exit(methodName + "result:" + result);
		return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
	}


	/**
	 * 获取
	 * @return
	 */
	@GetMapping("/listPortStorage")
	public Map<String, Object> listPortStorage(String cargoInfoNo) {
		final String methodName = "ShipInfoController:listPortStorage";
		LOGGER.enter(methodName + "[start]", "listPortStorage:" + cargoInfoNo);
		List<TPrdPortStorageDTO> result = shipInfoService.listPortStorage(cargoInfoNo);
		LOGGER.exit(methodName + "result:" + result);
		return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
	}

	/**
	 * 查询进出明细
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
		Map<String, Object> result = shipInfoService.getInoutDetail(query);
		return Response.SUCCESS.newBuilder().toResult(result);
	}

	/**
	 * 获取
	 * @return
	 */
	@GetMapping("/getCostInfo")
	public Map<String, Object> getCostInfo(Long shipVoyageId) {
		final String methodName = "ShipInfoController:getCostInfo";
		LOGGER.enter(methodName + "[start]", "getCostInfo:" + shipVoyageId);
		List<TDisCostInfoPO> result = shipInfoService.getCostInfo(shipVoyageId);
		LOGGER.exit(methodName + "result:" + result);
		return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
	}

	/**
	 * 获取
	 * @return
	 */
	@GetMapping("/getPoundInfo")
	public Map<String, Object> getPoundInfo(Long shipVoyageId) {
		final String methodName = "ShipInfoController:getPoundInfo";
		LOGGER.enter(methodName + "[start]", "getPoundInfo:" + shipVoyageId);
		List<TDisPoundInfoDTO> result = shipInfoService.getPoundInfo(shipVoyageId);
		LOGGER.exit(methodName + "result:" + result);
		return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
	}

}
