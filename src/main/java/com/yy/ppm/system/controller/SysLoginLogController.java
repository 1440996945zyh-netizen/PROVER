package com.yy.ppm.system.controller;

import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.system.service.SysLoginLogService;
import com.yy.ppm.system.bean.dto.SysLoginLogDTO;
import com.yy.ppm.system.bean.dto.SysLoginLogSearchDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @ClassName 登录日志表(SysLoginLog)Controller
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月29日 15:51:00
 */
@RestController
@RequestMapping("/api/v1/internal/sysLoginLog")
public class SysLoginLogController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(SysLoginLogController.class);

    @Autowired
    private SysLoginLogService sysLoginLogService;

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(SysLoginLogSearchDTO searchDTO) {
    	final String methodName = "SysLoginLogController:getList";
		LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<SysLoginLogDTO> pages = sysLoginLogService.getList(searchDTO);

        LOGGER.exit( methodName + "result:" + pages);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 查询单条记录
     * @param id
     * @return
     */
    @GetMapping("/getDetail/{id}")
    public Map<String, Object> getDetail(@PathVariable("id") Long id) {
        final String methodName = "SysLoginLogController:getDetail";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        SysLoginLogDTO result = sysLoginLogService.getDetail(id);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     * @param sysLoginLogDTO
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody SysLoginLogDTO sysLoginLogDTO) {
        final String methodName = "SysLoginLogController:add";
		LOGGER.enter(methodName + "[start]", "sysLoginLogDTO:" +  sysLoginLogDTO);

        boolean flag = sysLoginLogService.doSave(sysLoginLogDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改
     * @param sysLoginLogDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody SysLoginLogDTO sysLoginLogDTO) {
        final String methodName = "SysLoginLogController:update";
		LOGGER.enter(methodName + "[start]", "sysLoginLogDTO:" +  sysLoginLogDTO);

        boolean flag = sysLoginLogService.doSave(sysLoginLogDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "SysLoginLogController:deleteById";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = sysLoginLogService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

}

