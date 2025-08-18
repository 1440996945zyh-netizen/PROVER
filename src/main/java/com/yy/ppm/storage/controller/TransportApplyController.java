package com.yy.ppm.storage.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.framework.annotation.Log;
import com.yy.ppm.common.enums.DispatchEnum;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanSearchDTO;
import com.yy.ppm.produce.service.TPrdWorkPlanService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName 作业计划表-转运申请(TPrdWorkPlan)Controller
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月21日 16:21:00
 */
@RestController
@RequestMapping("/api/v1/internal/transportApply")
@Tag(name = "作业计划.转运申请管理")
public class TransportApplyController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(TransportApplyController.class);

    @Autowired
    private TPrdWorkPlanService tPrdWorkPlanService;

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('storage:transportApply:query')")
    public Map<String, Object> getList(TPrdWorkPlanSearchDTO searchDTO) {
    	final String methodName = "TransportApplyController:getList";
		LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        searchDTO.setPlanType(DispatchEnum.WorkPlanTypeEnum.RESHIPMENT.getCode());
        List<TPrdWorkPlanDTO> pages = tPrdWorkPlanService.getList(searchDTO);

        LOGGER.exit( methodName + "result:" + pages);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 查询单条记录
     * @param id
     * @return
     */
    @GetMapping("/getDetail")
    @PreAuthorize("hasAuthority('storage:transportApply:query')")
    public Map<String, Object> getDetail(@RequestParam("id") Long id) {
        final String methodName = "TransportApplyController:getDetail";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        TPrdWorkPlanDTO result = tPrdWorkPlanService.getDetail(id);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     * @param tPrdWorkPlanDTO
     * @return
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('storage:transportApply:add')")
    @Log(title = "转运申请新增", value = OperateTypeEnum.INSERT)
    public Map<String, Object> add(@RequestBody TPrdWorkPlanDTO tPrdWorkPlanDTO) {
        final String methodName = "TransportApplyController:add";
		LOGGER.enter(methodName + "[start]", "tPrdWorkPlanDTO:" +  tPrdWorkPlanDTO);

        tPrdWorkPlanDTO.setPlanType(DispatchEnum.WorkPlanTypeEnum.RESHIPMENT.getCode());
        boolean flag = tPrdWorkPlanService.doSave(tPrdWorkPlanDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改
     * @param tPrdWorkPlanDTO
     * @return
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('storage:transportApply:update')")
    @Log(title = "转运申请修改", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> update(@RequestBody TPrdWorkPlanDTO tPrdWorkPlanDTO) {
        final String methodName = "TransportApplyController:update";
		LOGGER.enter(methodName + "[start]", "tPrdWorkPlanDTO:" +  tPrdWorkPlanDTO);

        tPrdWorkPlanDTO.setPlanType(DispatchEnum.WorkPlanTypeEnum.RESHIPMENT.getCode());
        boolean flag = tPrdWorkPlanService.doSave(tPrdWorkPlanDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('storage:transportApply:delete')")
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "TransportApplyController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tPrdWorkPlanService.deleteByIds(Arrays.asList(id));

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 通过转运计划查询作业过程
     * @param
     * @return
     */
    @GetMapping("/getProcessName")
    @PreAuthorize("hasAuthority('storage:transportApply:query')")
    public Map<String, Object> getProcessName() {
        final String methodName = "TransportApplyController:getList";
        LOGGER.enter(methodName, "通过转运计划查询作业过程[start]");

        List<Map<String, String>> result = tPrdWorkPlanService.getProcessName();

        LOGGER.exit(methodName, "通过转运计划查询作业过程[end]");
        return Response.SUCCESS.newBuilder().toResult(result);
    }


}

