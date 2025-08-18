package com.yy.ppm.appWork.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.FieldErrorUtils;
import com.yy.ppm.appWork.bean.dto.TDisWaterDTO;
import com.yy.ppm.appWork.service.TDisWaterElectricityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;


/**
 * App加水接电
 * @author chenfs
 * @since 2023年2月9日
 */
@RestController
@RequestMapping("/api/external/TDisWaterElectricityController")
@Validated
public class TDisWaterElectricityController {
	/**
	 * 日志组件
	 */
	private static final MicroLogger LOGGER = new MicroLogger(com.yy.ppm.appWork.controller.TDisWaterElectricityController.class);


	@Autowired
	private TDisWaterElectricityService tDisWaterElectricityService;

	/**
	 * 查询作业指令
	 * @param
	 * @param tDisWaterElectricityDTO 实例对象
	 * @return
	 */
	@GetMapping("/queryAllApp")
	public Map<String, Object> queryAllApp(TDisWaterDTO tDisWaterElectricityDTO){
		final String methodName = "queryAllApp";
		LOGGER.enter(methodName, "查询作业指令, tDisWaterElectricityDTO: " + tDisWaterElectricityDTO);

		List<TDisWaterDTO> list = tDisWaterElectricityService.queryAllApp(tDisWaterElectricityDTO);
		LOGGER.exit(methodName);
		return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
	}

	/**
	 * 获取用户列表
	 * @param
	 * @return
	 */
	@GetMapping("/getUserList")
	public Map<String, Object> getUserList(){
		final String methodName = "getUserList";
		LOGGER.enter(methodName, "获取用户列表");
		List<Map<String,Object>> list = tDisWaterElectricityService.getUserList();
		LOGGER.exit(methodName);
		return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
	}

	/**
	 * 根据指令id查水电详情
	 * @param trustId
	 * @return
	 */
	@GetMapping("/queryIdApp")
	public Map<String, Object> queryIdApp(String trustId){
		final String methodName = "queryIdApp";
		LOGGER.enter(methodName, "根据指令id查水电详情, trustId: " + trustId);

		if (!isNumeric(trustId)) {
			LOGGER.info("数值格式不正确~");
			return Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0202).toResult();
		}
		List<TDisWaterDTO> list = tDisWaterElectricityService.queryIdApp(trustId);
		return Response.SUCCESS.newBuilder().toResult(list);
	}

	/**
	 * APP新增加水接电
	 * @param tDisWaterElectricityDTO 实例对象
	 * @param result
	 * @return
	 */
	@PostMapping("/AppInsert")
	public Map<String, Object> AppInsert(@RequestBody @Valid TDisWaterDTO tDisWaterElectricityDTO, BindingResult result){
		final String methodName = "AppInsert";
		LOGGER.enter(methodName, "新增加水接电, tDisWaterElectricityDTO: " + tDisWaterElectricityDTO);

		if (result.hasErrors()) {
			LOGGER.warn("入参校验失败~");
			FieldErrorUtils.FieldBean bean = FieldErrorUtils.getDefaultMessage(result.getFieldErrors(),true, "id");
			if (bean.isSuccess()) {
				return Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0200).out(bean.getMsg()).toResult();
			}
		}
		tDisWaterElectricityService.AppInsert(tDisWaterElectricityDTO);
		LOGGER.exit(methodName);
		return Response.SUCCESS.newBuilder().out("加水接电操作成功").toResult();
	}

	/**
	 * App删除加水接电
	 * @param id 主键id
	 * @return
	 */
	@DeleteMapping("/deleteApp")
	public Map<String, Object> deleteApp(String id){
		final String methodName = "deleteApp";
		LOGGER.enter(methodName, "删除加水接电, id: " + id);

		if (isBlank(id)) {
			LOGGER.info("缺失必填参数id~");
			return Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0201).out("缺失必填参数id").toResult();
		}
		boolean isNumeric = isNumeric(id);
		if (!isNumeric) {
			LOGGER.info("数值格式不正确~");
			return Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0202).toResult();
		}
		tDisWaterElectricityService.deleteApp(Long.parseLong(id));
		return Response.SUCCESS.newBuilder().out("删除加水接电").toResult();
	}

	/**
	 * 根据指令id查水电详情
	 * @param id
	 * @return
	 */
	@GetMapping("/queryById")
	public Map<String, Object> queryById(String id){
		final String methodName = "queryById";
		LOGGER.enter(methodName, "根据id查水电详情, id: " + id);

		if (!isNumeric(id)) {
			LOGGER.info("数值格式不正确~");
			return Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0202).toResult();
		}
		TDisWaterDTO tDisWaterElectricityDTO = tDisWaterElectricityService.queryById(Long.parseLong(id));
		return Response.SUCCESS.newBuilder().toResult(tDisWaterElectricityDTO);
	}
}
