package com.yy.ppm.produce.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.framework.config.MinioConfig;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.bean.po.SysFilePO;
import com.yy.ppm.produce.bean.dto.TTruckLogDTO;
import com.yy.ppm.produce.service.TTruckLogService;
import com.yy.ppm.system.bean.dto.SysVersionControlDTO;
import com.yy.ppm.system.bean.po.SysVersionControlPO;
import com.yy.ppm.system.service.SysVersionControlService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * 车辆作业流水
 * @author wangxd
 */
@RestController
@RequestMapping(value = "/api/internal/tTruckLog")
@Tag(name = "生产作业.车辆作业流水")
@Validated
public class TTruckLogControlController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(TTruckLogControlController.class);

    @Autowired
    private TTruckLogService tTruckLogService;


    /**
     * 列表查询
     * @param tTruckLogDTO
     * @return
     */
	@GetMapping("/getList")
	@PreAuthorize("hasAuthority('produce:tTruckLog:query')")
	@Log(OperateTypeEnum.QUERY)
	public Map<String, Object> getList(TTruckLogDTO tTruckLogDTO) {
		final String methodName = "TTruckLogControlController:getList";

		Pages<TTruckLogDTO> tTruckLogList = tTruckLogService.getList(tTruckLogDTO);

		return Response.SUCCESS.newBuilder().out("查询成功").toResult(tTruckLogList);
	}

}
