package com.yy.ppm.master.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.ppm.master.bean.dto.MWorkScheduleDTO;
import com.yy.ppm.master.service.MWorkScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * (MWorkSchedule)表控制层
 *
 * @author 张超
 * @date 2021-03-11 14:45:39
 */
@RestController
@RequestMapping(value = "/api/external/workschedule")
@Validated
public class MWorkScheduleController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(MWorkScheduleController.class);
    /**
     * 服务对象
     */
    @Autowired
    private MWorkScheduleService mWorkScheduleService;

    /**
	 * 根据实体类筛选数据列表
	 *
	 * @param
	 * @return 统一数据封装
	 */
	@GetMapping("/getlist")
	@PreAuthorize("hasAuthority('master:workSchedule:query')")
	public Map<String, Object> getList() {
		final String methodName = "MWorkScheduleController: getList";
		LOGGER.enter(methodName + "[start]");

		List<MWorkScheduleDTO> mWorkScheduleList = mWorkScheduleService.getList();

		LOGGER.exit(methodName + "result:" + mWorkScheduleList);
		return Response.SUCCESS.newBuilder().out("查询成功").toResult(mWorkScheduleList);
	}

	/**
	 * 保存
	 * @param workScheduleList
	 * @return
	 */
	@PostMapping("/save")
	@PreAuthorize("hasAuthority('master:workSchedule:save')")
	public Map<String, Object> save(@RequestBody List<MWorkScheduleDTO> workScheduleList) {
		final String methodName = "MWorkScheduleController: save";
		LOGGER.enter(methodName + "[start]", "workScheduleList:" + workScheduleList);

		int count = mWorkScheduleService.save(workScheduleList);

		LOGGER.exit(methodName + "result:" + count);
		return Response.SUCCESS.newBuilder().out("新增成功").toResult(count);
	}

}
