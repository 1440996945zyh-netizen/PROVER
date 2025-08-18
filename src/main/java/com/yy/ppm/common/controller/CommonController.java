package com.yy.ppm.common.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping(value = "/api/external/base")
@Validated
public class CommonController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(CommonController.class);

	@Autowired
	private CommonService baseService;

	/**
	 * 删除
	 * @param gid
	 * @return
	 */
	@DeleteMapping("/deletebygid/{gid}")
	public Map<String, Object> deletebygid(@PathVariable("gid") String gid) {
		final String methodName = "CommonController:deletebygid";
		LOGGER.enter(methodName + "[start]", "gid:" + gid);

		int count = baseService.delete("sys_user","gid",gid);

		LOGGER.exit(methodName + "result:" + count);
		return Response.SUCCESS.newBuilder().out("删除成功").toResult(count);
	}


}
