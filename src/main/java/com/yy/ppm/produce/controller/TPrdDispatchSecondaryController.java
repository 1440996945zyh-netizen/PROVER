package com.yy.ppm.produce.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.framework.annotation.Log;
import com.yy.ppm.common.enums.DispatchEnum;
import com.yy.ppm.produce.bean.dto.*;
import com.yy.ppm.produce.service.TPrdDispatchSecondaryService;
import com.yy.ppm.produce.service.TPrdWorkPlanService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @ClassName 作业计划派工表（二次配工）Controller
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月30日 18:16:00
 */
@RestController
@RequestMapping("/api/v1/internal/tPrdDispatchSecondary")
@Tag(name = "生产作业.二次配工")
public class TPrdDispatchSecondaryController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(TPrdDispatchSecondaryController.class);

    @Autowired
    private TPrdDispatchSecondaryService tPrdDispatchSecondaryService;

    @Autowired
    private TPrdWorkPlanService tPrdWorkPlanService;

    /**
     * 获取工班计划列表
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('produce:dispatchSecondary:query')")
    @Log(title = "二次派工主列表查询", value = OperateTypeEnum.QUERY)
    public Map<String, Object> getList(TPrdWorkPlanSearchDTO searchDTO) {
    	final String methodName = "TPrdDispatchSecondaryController:getList";

        //默认查询已审核的数据
        searchDTO.setStatus(DispatchEnum.WorkPlanStatusEnum.REVIEW.getCode());

        List<TPrdWorkPlanDTO> list = tPrdWorkPlanService.getList(searchDTO);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

    /**
     * 获取二次派工信息
     * @param searchDto
     * @return
     */
    @GetMapping("/getDispatchList")
    @PreAuthorize("hasAuthority('produce:dispatchSecondary:query')")
    @Log(title = "二次派工->获取二次配工信息", value = OperateTypeEnum.QUERY)
    public Map<String, Object> getList(TPrdDispatchSecondarySearchDTO searchDto) {
        final String methodName = "TPrdDispatchSecondaryController:getDispatchList";

        List<TPrdDispatchSecondaryDTO> list = tPrdDispatchSecondaryService.getList(searchDto);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

    /**
     * 获取二次派工信息
     * @param searchDto
     * @return
     */
    @GetMapping("/getDispatchAllList")
    @PreAuthorize("hasAuthority('produce:dispatchSecondary:query')")
    @Log(title = "二次派工->获取二次配工信息(全部)", value = OperateTypeEnum.QUERY)
    public Map<String, Object> getAllList(TPrdDispatchSecondarySearchDTO searchDto) {
        final String methodName = "TPrdDispatchSecondaryController:getAllList";

        List<TPrdDispatchSecondaryDTO> list = tPrdDispatchSecondaryService.getAllList(searchDto);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

    /**
     * 新建
     * @param tPrdDispatchSecondaryDTO
     * @return
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('produce:dispatchSecondary:query')")
    @Log(title = "二次配工单次配工", value = OperateTypeEnum.INSERT)
    public Map<String, Object> add(@RequestBody TPrdDispatchSecondaryDTO tPrdDispatchSecondaryDTO) {
        final String methodName = "TPrdDispatchSecondaryController:add";
		LOGGER.enter(methodName + "[start]", "tPrdDispatchSecondaryDTO:" +  tPrdDispatchSecondaryDTO);

        boolean flag = tPrdDispatchSecondaryService.doSave(tPrdDispatchSecondaryDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 机械配工
     * @param req
     * @return
     */
    @PostMapping("/saveMachine")
    @PreAuthorize("hasAuthority('produce:dispatchSecondary:machineryDispatch')")
    @Log(title = "二次配工.机械配工", value = OperateTypeEnum.INSERT)
    public Map<String, Object> saveMachine(@RequestBody  DispatchSecondaryBatchReq req) {
        final String methodName = "TPrdDispatchSecondaryController:saveMachine";
        LOGGER.enter(methodName + "[start]", "req:" +  req);

        boolean flag = tPrdDispatchSecondaryService.doSaveBatch(req);
        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "批量新建成功" : "批量新建失败").toResult();

    }

    /**
     * 劳务配工
     * @param req
     * @return
     */
    @PostMapping("/saveLabor")
    @PreAuthorize("hasAuthority('produce:dispatchSecondary:laborDispatch')")
    @Log(title = "二次配工.劳务配工", value = OperateTypeEnum.INSERT)
    public Map<String, Object> saveLabor(@RequestBody DispatchSecondaryBatchReq req) {
        final String methodName = "TPrdDispatchSecondaryController:saveLabor";
        LOGGER.enter(methodName + "[start]", "req:" +  req);

        boolean flag = tPrdDispatchSecondaryService.doSaveBatch(req);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "批量新建成功" : "批量新建失败").toResult();
    }

    /**
     * 修改
     * @param tPrdDispatchSecondaryDTO
     * @return
     */
    @PutMapping("/update")
    @Log(title = "二次配工.更新配工信息", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> update(@RequestBody TPrdDispatchSecondaryDTO tPrdDispatchSecondaryDTO) {
        final String methodName = "TPrdDispatchSecondaryController:update";
		LOGGER.enter(methodName + "[start]", "tPrdDispatchSecondaryDTO:" +  tPrdDispatchSecondaryDTO);

        boolean flag = tPrdDispatchSecondaryService.doSave(tPrdDispatchSecondaryDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @Log(title = "二次配工.通过ID删除", value = OperateTypeEnum.DELETE)
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "TPrdDispatchSecondaryController:deleteById";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tPrdDispatchSecondaryService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping("/delete")
    @Log(title = "二次配工.批量删除", value = OperateTypeEnum.DELETE)
    public Map<String, Object> deleteByIds(@RequestBody List<Long> ids) {
        final String methodName = "TPrdDispatchSecondaryController:deleteByIds";
		LOGGER.enter(methodName + "[start]", "ids:" + ids);

        boolean flag = tPrdDispatchSecondaryService.deleteByIds(ids);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }



    /**
     * 获取劳务派工信息
     * @param workPlanId
     * @return
     */
    @GetMapping("/getLaborList")
    @PreAuthorize("hasAuthority('produce:dispatchSecondary:query')")
    @Log(title = "二次配工.获取劳务配工信息", value = OperateTypeEnum.QUERY)
    public Map<String, Object> getLaborList(Long workPlanId) {
        final String methodName = "TPrdDispatchSecondaryController:getLaborList";
        LOGGER.enter(methodName + "[start]", "dispatchType:" + workPlanId);

        List<TPrdDispatchSecondManResultType> list = tPrdDispatchSecondaryService.getLaborList(workPlanId);

        LOGGER.exit( methodName + "result:" + list);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

    /**
     * 获取装卸队派工部门信息
     * @param
     * @return
     */
    @GetMapping("/getLaborDeptList")
    @PreAuthorize("hasAuthority('produce:dispatchSecondary:query')")
    @Log(title = "二次配工.获取装卸队派工部门信息", value = OperateTypeEnum.QUERY)
    public Map<String, Object> getLaborDeptList() {
        List<TPrdDispatchSecondManResultType> list = tPrdDispatchSecondaryService.getLaborDeptList();
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

    /**
     * 根据部门获取装卸队派工班组信息
     * @param
     * @return
     */
    @GetMapping("/getLaborGroupList")
    @PreAuthorize("hasAuthority('produce:dispatchSecondary:query')")
    @Log(title = "二次配工.获取装卸队派工班组信息", value = OperateTypeEnum.QUERY)
    public Map<String, Object> getLaborGroupList(String deptParentId) {
        List<TPrdDispatchSecondManResultType> list = tPrdDispatchSecondaryService.getLaborGroupList(deptParentId);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }


    /**
     * 获取劳务派工信息 回显用
     * @param workPlanId
     * @return
     */
    @GetMapping("/getEchoLaborList")
    @Log(title = "二次配工.获取劳务配工信息回显用", value = OperateTypeEnum.QUERY)
    public Map<String, Object> getEchoLaborList(Long workPlanId) {
        final String methodName = "TPrdDispatchSecondaryController:getLaborList";
        LOGGER.enter(methodName + "[start]", "dispatchType:" + workPlanId);

        List<TPrdDispatchSecondManResultType> list = tPrdDispatchSecondaryService.getEchoLaborList(workPlanId);

        LOGGER.exit( methodName + "result:" + list);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

}

