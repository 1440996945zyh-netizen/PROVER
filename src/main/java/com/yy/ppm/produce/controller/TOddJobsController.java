package com.yy.ppm.produce.controller;

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
 * @ClassName 作业计划表-杂项申请Controller
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月21日 16:21:00
 */
@RestController
@RequestMapping("/api/v1/internal/tPrdWorkPlanZx")
@Tag(name = "作业计划.零工申请管理")
public class TOddJobsController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(TOddJobsController.class);

    @Autowired
    private TPrdWorkPlanService tPrdWorkPlanService;

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('produce:oddJobs:query')")
    public Map<String, Object> getList(TPrdWorkPlanSearchDTO searchDTO) {
    	final String methodName = "TPrdWorkPlanZxController:getList";

        searchDTO.setPlanType(DispatchEnum.WorkPlanTypeEnum.SUNDRY.getCode());
        List<TPrdWorkPlanDTO> pages = tPrdWorkPlanService.getList(searchDTO);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 查询单条记录
     * @param id
     * @return
     */
    @GetMapping("/getDetail")
    @PreAuthorize("hasAuthority('produce:oddJobs:query')")
    public Map<String, Object> getDetail(@RequestParam("id") Long id) {
        final String methodName = "TPrdWorkPlanZxController:getDetail";

        TPrdWorkPlanDTO result = tPrdWorkPlanService.getDetail(id);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     * @param tPrdWorkPlanDTO
     * @return
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('produce:oddJobs:add')")
    @Log(title = "零工申请新增", value = OperateTypeEnum.INSERT)
    public Map<String, Object> add(@RequestBody TPrdWorkPlanDTO tPrdWorkPlanDTO) {
        final String methodName = "TPrdWorkPlanZxController:add";
		LOGGER.enter(methodName + "[start]", "tPrdWorkPlanDTO:" +  tPrdWorkPlanDTO);

        tPrdWorkPlanDTO.setPlanType(DispatchEnum.WorkPlanTypeEnum.SUNDRY.getCode());
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
    @PreAuthorize("hasAuthority('produce:oddJobs:update')")
    @Log(title = "转运申请修改", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> update(@RequestBody TPrdWorkPlanDTO tPrdWorkPlanDTO) {
        final String methodName = "TPrdWorkPlanZxController:update";
		LOGGER.enter(methodName + "[start]", "tPrdWorkPlanDTO:" +  tPrdWorkPlanDTO);

        tPrdWorkPlanDTO.setPlanType(DispatchEnum.WorkPlanTypeEnum.SUNDRY.getCode());
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
    @PreAuthorize("hasAuthority('produce:oddJobs:delete')")
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "TPrdWorkPlanZxController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tPrdWorkPlanService.deleteByIds(Arrays.asList(id));

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

}

