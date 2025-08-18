package com.yy.ppm.dispatch.controller;

import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.dispatch.service.TDisLogService;
import com.yy.ppm.dispatch.bean.dto.TDisLogDTO;
import com.yy.ppm.dispatch.bean.dto.TDisLogSearchDTO;

import com.yy.ppm.system.bean.dto.TreeSelectDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @ClassName 调度日志(TDisLog)Controller
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月12日 11:45:00
 */
@RestController
@RequestMapping("/api/v1/internal/tDisLog")
public class TDisLogController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(TDisLogController.class);

    @Autowired
    private TDisLogService tDisLogService;

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('dispatch:disLog:query')")
    public Map<String, Object> getList(TDisLogSearchDTO searchDTO) {
//    	final String methodName = "TDisLogController:getList";
//		LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<TDisLogDTO> pages = tDisLogService.getList(searchDTO);

//        LOGGER.exit( methodName + "result:" + pages);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 查询单条记录
     * @param id
     * @return
     */
    @GetMapping("/getDetail")
    @PreAuthorize("hasAuthority('dispatch:disLog:query')")
    public Map<String, Object> getDetail(@RequestParam("id") Long id) {
//        final String methodName = "TDisLogController:getDetail";
//		LOGGER.enter(methodName + "[start]", "id:" + id);

        TDisLogDTO result = tDisLogService.getDetail(id);

//        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     * @param tDisLogDTO
     * @return
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('dispatch:disLog:add')")
    public Map<String, Object> add(@RequestBody TDisLogDTO tDisLogDTO) {
//        final String methodName = "TDisLogController:add";
//		LOGGER.enter(methodName + "[start]", "tDisLogDTO:" +  tDisLogDTO);

        boolean flag = tDisLogService.doSave(tDisLogDTO);

//        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改
     * @param tDisLogDTO
     * @return
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('dispatch:disLog:update')")
    public Map<String, Object> update(@RequestBody TDisLogDTO tDisLogDTO) {
//        final String methodName = "TDisLogController:update";
//		LOGGER.enter(methodName + "[start]", "tDisLogDTO:" +  tDisLogDTO);

        boolean flag = tDisLogService.doSave(tDisLogDTO);

//        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('dispatch:disLog:delete')")
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
//        final String methodName = "TDisLogController:deleteById";
//		LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tDisLogService.deleteById(id);

//        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }


}

