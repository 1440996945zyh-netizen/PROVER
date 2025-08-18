package com.yy.ppm.master.controller;

import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.master.service.MStopReasonService;
import com.yy.ppm.master.bean.dto.MStopReasonDTO;
import com.yy.ppm.master.bean.dto.MStopReasonSearchDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @ClassName 船舶停时原因维护(MStopReason)Controller
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月05日 17:21:00
 */
@RestController
@RequestMapping("/api/v1/internal/mStopReason")
public class MStopReasonController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(MStopReasonController.class);

    @Autowired
    private MStopReasonService mStopReasonService;

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('master:stopReason:query')")
    public Map<String, Object> getList(MStopReasonSearchDTO searchDTO) {
    	final String methodName = "MStopReasonController:getList";
		LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<MStopReasonDTO> pages = mStopReasonService.getList(searchDTO);

        LOGGER.exit( methodName + "result:" + pages);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 查询单条记录
     * @param id
     * @return
     */
    @GetMapping("/getDetail")
    @PreAuthorize("hasAuthority('master:stopReason:query')")
    public Map<String, Object> getDetail(@RequestParam("id") Long id) {
        final String methodName = "MStopReasonController:getDetail";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        MStopReasonDTO result = mStopReasonService.getDetail(id);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     * @param mStopReasonDTO
     * @return
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('master:stopReason:add')")
    public Map<String, Object> add(@RequestBody MStopReasonDTO mStopReasonDTO) {
        final String methodName = "MStopReasonController:add";
		LOGGER.enter(methodName + "[start]", "mStopReasonDTO:" +  mStopReasonDTO);

        boolean flag = mStopReasonService.doSave(mStopReasonDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改
     * @param mStopReasonDTO
     * @return
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('master:stopReason:update')")
    public Map<String, Object> update(@RequestBody MStopReasonDTO mStopReasonDTO) {
        final String methodName = "MStopReasonController:update";
		LOGGER.enter(methodName + "[start]", "mStopReasonDTO:" +  mStopReasonDTO);

        boolean flag = mStopReasonService.doSave(mStopReasonDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('master:stopReason:delete')")
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "MStopReasonController:deleteById";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = mStopReasonService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

}

