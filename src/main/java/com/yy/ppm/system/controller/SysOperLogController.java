package com.yy.ppm.system.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.system.bean.dto.SysOperLogDTO;
import com.yy.ppm.system.bean.dto.SysOperLogSearchDTO;
import com.yy.ppm.system.service.SysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 操作日志
 */
@RestController
@RequestMapping(value = "/api/internal/sysOperLog")
@Validated
public class SysOperLogController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(SysOperLogController.class);
    /**
     * 服务对象
     */
    private final SysOperLogService sysOperLogService;

    public SysOperLogController(SysOperLogService sysOperLogService){
        this.sysOperLogService = sysOperLogService;
    }

    /**
     * 查询
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('system:operlog:query')")
    public Map<String, Object> getList(SysOperLogSearchDTO sysOperLogSearchDTO) {
        final String methodName = "SysOperLogController:getList";
        LOGGER.enter(methodName + "[start]", "sysOperLogSearchDTO:" + sysOperLogSearchDTO);
        Pages<SysOperLogDTO> list = sysOperLogService.getList(sysOperLogSearchDTO);
        LOGGER.exit( methodName + "result:" + list);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);

    }

    /**
     * 根据id查数据
     */
    @GetMapping("/{operId}")
    @PreAuthorize("hasAuthority('system:operlog:query')")
    public Map<String, Object> getById(@PathVariable("operId") Long operId) {
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(sysOperLogService.getById(operId));
    }

}
