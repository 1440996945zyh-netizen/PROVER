package com.yy.ppm.dispatch.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.SecurityUtils;
import com.yy.ppm.dispatch.bean.dto.TAnchApplyDTO;
import com.yy.ppm.dispatch.bean.dto.TAnchApplySearchDTO;
import com.yy.ppm.dispatch.service.TAnchApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/external/anchApply/")
@Validated
public class TAnchApplyController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(TAnchApplyController.class);

	@Autowired
	private TAnchApplyService tAnchApplyService;

	@Autowired
	private SecurityUtils securityUtils;


	@GetMapping("/getlist")
	public Map<String, Object> getList(TAnchApplySearchDTO tAnchApplySearchDTO) {
//		final String methodName = "getList";
//		LOGGER.enter("TAnchApplyController:" + methodName + "[start]", "tAnchApplySearchDTO:" + tAnchApplySearchDTO);

		Pages<TAnchApplyDTO> list = tAnchApplyService.getList(tAnchApplySearchDTO);

//		LOGGER.exit("TAnchApplyController:" + methodName + "result:" + list);
		return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
	}

	@GetMapping("/verify")
	public Map<String, Object> verify(TAnchApplyDTO tAnchApplyDTO) {
//		final String methodName = "verify";
//		LOGGER.enter("TAnchApplyController:" + methodName + "[start]", "tAnchApplyDTO:" + tAnchApplyDTO);

		int count = tAnchApplyService.verify(tAnchApplyDTO);

//		LOGGER.exit("BusServiceController:" + methodName + "result:" + count);
		return Response.SUCCESS.newBuilder().out("操作成功").toResult(count);
	}

	@GetMapping("/leaveAnchTime")
	public Map<String, Object> updateLeaveAnchTime(TAnchApplyDTO tAnchApplyDTO) {
//		final String methodName = "updateLeaveAnchTime";
//		LOGGER.enter("TAnchApplyController:" + methodName + "[start]", "tAnchApplyDTO:" + tAnchApplyDTO);

		boolean flag = tAnchApplyService.updateLeaveAnchTime(tAnchApplyDTO);

//		LOGGER.exit(methodName);
		return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
	}


}
