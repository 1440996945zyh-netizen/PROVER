package com.yy.ppm.storage.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.framework.annotation.Log;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.enums.DispatchEnum;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanSearchDTO;
import com.yy.ppm.produce.service.TPrdWorkPlanService;
import io.swagger.v3.oas.annotations.tags.Tag;
import oracle.jdbc.proxy.annotation.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName 作业计划表-集疏港申请Controller
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月21日 16:21:00
 */
@RestController
@RequestMapping("/api/v1/internal/openPortApply")
@Tag(name = "生产作业.集疏港计划")
public class OpenPortApplyController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(OpenPortApplyController.class);

    @Autowired
    private TPrdWorkPlanService tPrdWorkPlanService;

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('storage:openPortApply:query')")
    public Map<String, Object> getList(TPrdWorkPlanSearchDTO searchDTO) {
    	final String methodName = "OpenPortApplyController:getList";
		LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        searchDTO.setPlanType(DispatchEnum.WorkPlanTypeEnum.TRANSPORT.getCode());
        List<TPrdWorkPlanDTO> pages = tPrdWorkPlanService.getList(searchDTO);

        LOGGER.exit( methodName + " result:" + pages);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 导入集疏港作业指令
     * @return
     */
    @PostMapping("/importTrust")
    //@PreAuthorize("hasAuthority('storage:openPortApply:save')")
    @Log(title = "集疏港计划/转运计划导入作业指令,insertWorkPlan", value = OperateTypeEnum.INSERT)
    public Map<String, Object> insertWorkPlan(@RequestBody List<Long> trustIds, String workDate, String classCode, String className) {
        final String methodName = "TPrdWorkPlanController:insertWorkPlan";
        LOGGER.enter(methodName + "[start]", "trustIds:" +  trustIds);

        boolean flag = tPrdWorkPlanService.insertOpenPortTrust(trustIds, workDate, classCode, className);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 查询单条记录
     * @param id
     * @return
     */
    @GetMapping("/getDetail")
    @Log(title = "作业计划,查询单条计划详情", value = OperateTypeEnum.QUERY)
    public Map<String, Object> getDetail(@RequestParam("id") Long id) {
        final String methodName = "OpenPortApplyController:getDetail";
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
    @PreAuthorize("hasAuthority('storage:openPortApply:add')")
    public Map<String, Object> add(@RequestBody TPrdWorkPlanDTO tPrdWorkPlanDTO) {
        final String methodName = "OpenPortApplyController:add";
		LOGGER.enter(methodName + "[start]", "tPrdWorkPlanDTO:" +  tPrdWorkPlanDTO);

        tPrdWorkPlanDTO.setPlanType(DispatchEnum.WorkPlanTypeEnum.TRANSPORT.getCode());
        boolean flag = tPrdWorkPlanService.doSave(tPrdWorkPlanDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();
    }

    /**
     * 批量导入集疏港申请
     * @return
     */
    @PostMapping("/addBatch")
    @Log(title = "集疏港计划/转运计划导入上班次,addBatch", value = OperateTypeEnum.INSERT)
    public Map<String, Object> addBatch(@RequestBody List<Long> ids, String workDate, String classCode, String className) {
        final String methodName = "OpenPortApplyController:add";
		LOGGER.enter(methodName + "[start]", "ids:" +  ids);

        if (ids == null) {
            throw new BusinessRuntimeException("请选择要导入的工班计划~");
        }
        boolean flag = tPrdWorkPlanService.addBatch(ids, workDate, classCode, className);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "导入成功" : "导入失败").toResult();
    }

    /**
     * 批量导入集疏港申请
     * @return
     */
    @PostMapping("/addJSGBatch")
    @Log(title = "集疏港计划导入上班次,addJSGBatch", value = OperateTypeEnum.INSERT)
    public Map<String, Object> addJSGBatch(@RequestBody List<TPrdWorkPlanDTO> dtos, String workDate, String classCode, String className) {
        final String methodName = "OpenPortApplyController:add";
        LOGGER.enter(methodName + "[start]", "dtos:" +  dtos);

        if (dtos == null) {
            throw new BusinessRuntimeException("请选择要导入的工班计划~");
        }
        boolean flag = tPrdWorkPlanService.addJSGBatch(dtos, workDate, classCode, className);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "导入成功" : "导入失败").toResult();
    }


    /**
     * 修改
     * @param tPrdWorkPlanDTO
     * @return
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('storage:openPortApply:update')")
    public Map<String, Object> update(@RequestBody TPrdWorkPlanDTO tPrdWorkPlanDTO) {
        final String methodName = "OpenPortApplyController:update";
		LOGGER.enter(methodName + "[start]", "tPrdWorkPlanDTO:" +  tPrdWorkPlanDTO);
        tPrdWorkPlanDTO.setPlanType(DispatchEnum.WorkPlanTypeEnum.TRANSPORT.getCode());
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
    @PreAuthorize("hasAuthority('storage:openPortApply:delete')")
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "OpenPortApplyController:deleteById";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tPrdWorkPlanService.deleteByIds(Arrays.asList(id));

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

}

