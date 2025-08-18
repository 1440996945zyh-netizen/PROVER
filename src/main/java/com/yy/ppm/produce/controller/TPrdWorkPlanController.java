package com.yy.ppm.produce.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.framework.annotation.Log;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.bean.dto.ResponsePopupTrustDTO;
import com.yy.ppm.common.bean.dto.SelecSearchDTO;
import com.yy.ppm.common.mapper.SelectMapper;
import com.yy.ppm.dispatch.bean.dto.TDisPortDaynightplanDTO;
import com.yy.ppm.produce.service.TPrdWorkPlanService;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanSearchDTO;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName 作业计划表(TPrdWorkPlan)Controller
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月21日 16:21:00
 */
@RestController
@RequestMapping("/api/v1/internal/tPrdWorkPlan")
@Tag(name = "作业计划.作业计划管理")
public class TPrdWorkPlanController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(TPrdWorkPlanController.class);

    @Autowired
    private TPrdWorkPlanService tPrdWorkPlanService;
    @Resource
    public SelectMapper selectMapper;

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getWorkPlanList")
    @Log(title = "查询作业计划列表,getWOrkPlanLit", value = OperateTypeEnum.QUERY)
    public Map<String, Object> getWorkPlanList(TPrdWorkPlanSearchDTO searchDTO) {

        final String methodName = "TPrdWorkPlanController:getWorkPlanList";

        List<TPrdWorkPlanDTO> pages = tPrdWorkPlanService.getWorkPlanList(searchDTO);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }
    /**
     * 获取集疏港昼夜计划列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getJSGDayNightWorkPlanList")
    public Map<String, Object> getJSGDayNightWorkPlanList(TPrdWorkPlanSearchDTO searchDTO) {

        final String methodName = "TPrdWorkPlanController:getWorkPlanList";

        List<ResponsePopupTrustDTO> pages = tPrdWorkPlanService.getJSGDayNightWorkPlanList(searchDTO);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 导入上班次获取列表（翻页）
     * @param searchDTO
     * @return
     */
    /*@GetMapping("/getLastWorkPlanList")
    @Log(title = "查询上班次作业计划列表,getLastWorkPlanList", value = OperateTypeEnum.QUERY)
    public Map<String, Object> getLastWorkPlanList(TPrdWorkPlanSearchDTO searchDTO) {

        final String methodName = "TPrdWorkPlanController:getLastWorkPlanList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        List<TPrdWorkPlanDTO> pages = tPrdWorkPlanService.getLastWorkPlanList(searchDTO);

        LOGGER.exit( methodName + "result:" + pages);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }*/

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    @Log(title = "查询作业计划列表getList", value = OperateTypeEnum.QUERY)
    public Map<String, Object> getList(TPrdWorkPlanSearchDTO searchDTO) {
    	final String methodName = "TPrdWorkPlanController:getList";

        List<TPrdWorkPlanDTO> pages = tPrdWorkPlanService.getList(searchDTO);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 查询单条记录（导入指令用）
     * @param id
     * @return
     */
    @GetMapping("/getDetail")
    @Log(title = "作业计划查询单条记录（导入指令用）", value = OperateTypeEnum.QUERY)
    public Map<String, Object> getDetail(@RequestParam("id") Long id) {
        final String methodName = "TPrdWorkPlanController:getDetail";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        TPrdWorkPlanDTO result = tPrdWorkPlanService.getDetail(id);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增船舶计划
     * @return
     */
    @PostMapping("/insertWorkPlan")
    @Log(title = "船舶计划/转运计划导入指令（insertWorkPlan）", value = OperateTypeEnum.INSERT)
    public Map<String, Object> insertWorkPlan(@RequestBody List<Long> trustIds, String workDate, String classCode, String className,String planType) {

        boolean flag = tPrdWorkPlanService.insertWorkPlan(trustIds, workDate, classCode, className,planType);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();
    }

    /**
     * 新增零工计划
     * @return
     */
    @PostMapping("/insertLgWorkPlan")
    @Log(title = "新增零工计划（insertWorkPlan）", value = OperateTypeEnum.INSERT)
    public Map<String, Object> insertLgWorkPlan(@RequestBody List<TPrdWorkPlanDTO> list) {

        boolean flag = tPrdWorkPlanService.insertWorkPlan(list);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();
    }

    /**
     * 修改工班计划
     * @param list
     * @return
     */
    @PutMapping("/updateWorkPlan")
    @Log(title = "作业计划保存（updateWorkPlan）", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> updateWorkPlan(@RequestBody List<TPrdWorkPlanDTO> list) {
        final String methodName = "TPrdWorkPlanController:updateWorkPlan";
        LOGGER.enter(methodName + "[start]", "list:" +  list);

        boolean flag = tPrdWorkPlanService.updateWorkPlan(list);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }

    /**
     * 复制船舶计划
     * @param id
     * @return
     */
    @PostMapping("/copyWorkPlan")
    @Log(title = "作业计划复制（copyWorkPlan）", value = OperateTypeEnum.INSERT)
    public Map<String, Object> copyWorkPlan(Long id) {
        final String methodName = "TPrdWorkPlanController:copyWorkPlan";
        LOGGER.enter(methodName + "[start]", "id:" +  id);

        if (id == null) {
            throw new BusinessRuntimeException("请选择要复制的工班计划~");
        }

        boolean flag = tPrdWorkPlanService.copyWorkPlan(Arrays.asList(id));

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "复制成功" : "复制失败").toResult();
    }



    /**
     * 复制集疏港计划
     * @param id
     * @return
     */
    @PostMapping("/copyJSGWorkPlan")
    @Log(title = "作业计划复制（copyJSGWorkPlan）", value = OperateTypeEnum.INSERT)
    public Map<String, Object> copyJSGWorkPlan(Long id) {
        final String methodName = "TPrdWorkPlanController:copyJSGWorkPlan";
        LOGGER.enter(methodName + "[start]", "id:" +  id);

        if (id == null) {
            throw new BusinessRuntimeException("请选择要复制的工班计划~");
        }

        boolean flag = tPrdWorkPlanService.copyJSGWorkPlan(Arrays.asList(id));

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "复制成功" : "复制失败").toResult();
    }

    /**
     * 复制集疏港计划
     * @param id
     * @return
     */
    @PostMapping("/copyZYWorkPlan")
    @Log(title = "作业计划复制（copyZYWorkPlan）", value = OperateTypeEnum.INSERT)
    public Map<String, Object> copyZYWorkPlan(Long id) {
        final String methodName = "TPrdWorkPlanController:copyZYWorkPlan";
        LOGGER.enter(methodName + "[start]", "id:" +  id);

        if (id == null) {
            throw new BusinessRuntimeException("请选择要复制的工班计划~");
        }

        boolean flag = tPrdWorkPlanService.copyZYWorkPlan(Arrays.asList(id));

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "复制成功" : "复制失败").toResult();
    }


    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping("/delete")
    @Log(title = "作业计划删除（deleteByIds）", value = OperateTypeEnum.DELETE)
    public Map<String, Object> deleteByIds(@RequestBody List<Long> ids) {
        final String methodName = "TPrdWorkPlanController:deleteByIds";
		LOGGER.enter(methodName + "[start]", "id:" + ids);

        boolean flag = tPrdWorkPlanService.deleteByIds(ids);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 审核
     * @param ids
     * @return
     */
    @PostMapping("/approve")
    @Log(title = "作业计划审核（approveByIds）", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> approveByIds(@RequestBody List<Long> ids) {
        final String methodName = "TPrdWorkPlanController:approveByIds";
        LOGGER.enter(methodName + "[start]", "id:" + ids);

        boolean flag = tPrdWorkPlanService.approveByIds(ids);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "审核成功" : "审核失败").toResult();
    }

    /**
     * 撤销审核
     * @param ids
     * @return
     */
    @PostMapping("/cancle")
    @Log(title = "作业计划消审（cancelByIds）", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> cancelByIds(@RequestBody List<Long> ids) {
        final String methodName = "TPrdWorkPlanController:cancelByIds";
        LOGGER.enter(methodName + "[start]", "id:" + ids);

        boolean flag = tPrdWorkPlanService.cancelByIds(ids);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "撤销成功" : "撤销失败").toResult();
    }

    /**
     * 船舶计划派调度员
     * @param tPrdWorkPlanDTO
     * @return
     */
    @PutMapping("/updateDispatch")
    @Log(title = "作业计划船舶计划派调度员（updateDispatch）", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> updateDispatch(@RequestBody TPrdWorkPlanDTO tPrdWorkPlanDTO) {
        final String methodName = "TPrdWorkPlanController:updateDispatch";
        LOGGER.enter(methodName + "[start]", "list:" +  tPrdWorkPlanDTO);

        boolean flag = tPrdWorkPlanService.updateDispatch(tPrdWorkPlanDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "派工成功" : "派工失败").toResult();
    }

    /**
     * 按班次导入
     * @param ids
     * @return
     */
    @PostMapping("/importWorkPlan")
    @Log(title = "船舶计划导入上班次(importWorkPlan)", value = OperateTypeEnum.INSERT)
    public Map<String, Object> importWorkPlan(@RequestBody List<Long> ids, String workDate, String classCode, String className) {
        final String methodName = "TPrdWorkPlanController:importWorkPlan";
        LOGGER.enter(methodName + "[start]", "ids:" +  ids);

        if (ids == null) {
            throw new BusinessRuntimeException("请选择要导入的工班计划~");
        }

        boolean flag = tPrdWorkPlanService.importWorkPlan(ids, workDate, classCode, className);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "导入成功" : "导入失败").toResult();
    }

    /**
     * 一般作业过程
     * @return
     */
    @GetMapping("/normalWorkProcess")
    @Log(title = "作业计划一般作业过程下拉框（normalWorkProcess）", value = OperateTypeEnum.QUERY)
    public Map<String, Object> normalWorkProcess() {
        final String methodName = "TPrdWorkPlanController:normalWorkProcess";
        LOGGER.enter(methodName + "[start]");

        List<Map<String, String>> result = tPrdWorkPlanService.normalWorkProcess();

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 根据计划类型选作业过程
     * @return
     */
    @GetMapping("/workProcessType/{type}")
    @Log(title = "作业计划根据计划类型选作业过程（workProcessType）", value = OperateTypeEnum.QUERY)
    public Map<String, Object> workProcessType(@PathVariable("type") Long type ) {
        final String methodName = "TPrdWorkPlanController:workProcessType";
        LOGGER.enter(methodName + "[start]");

        List<Map<String, String>> result = tPrdWorkPlanService.workProcessType(type);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 获取作业过程
     * @return
     */
    @GetMapping("/workProcessType2")
    @Log(title = "作业计划根据计划类型选作业过程（workProcessType）", value = OperateTypeEnum.QUERY)
    public Map<String, Object> workProcessType(Long type,String dictValue) {

        List<Map<String, String>> result = tPrdWorkPlanService.workProcessType(type,dictValue);

        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 获取货主
     * @return
     */
    @GetMapping("/getCustomer")
    @Log(title = "作业计划根据计划类型选作业过程（workProcessType）", value = OperateTypeEnum.QUERY)
    public Map<String, Object> getCustomer() {

        List<Map<String, Object>> result = selectMapper.getCustomerList(new SelecSearchDTO(), "");

        return Response.SUCCESS.newBuilder().toResult(result);
    }
    /**
     * 获取货名
     * @return
     */
    @GetMapping("/getCargo")
    @Log(title = "作业计划根据计划类型选作业过程（workProcessType）", value = OperateTypeEnum.QUERY)
    public Map<String, Object> getCargo() {

        List<Map<String, Object>> result = selectMapper.getCargoInfoSignList(new SelecSearchDTO());

        return Response.SUCCESS.newBuilder().toResult(result);
    }





    /**
     * 集疏港计划 理货员派工
     * @param tPrdWorkPlanDTO
     * @return
     */
    @PutMapping("/updateTally")
    @PreAuthorize("hasAuthority('dispatch:workPlan:tallyUpdate')")
    @Log(title = "作业计划理货员派工（updateTally）", value = OperateTypeEnum.INSERT)
    public Map<String, Object> updateTally(@RequestBody TPrdWorkPlanDTO tPrdWorkPlanDTO) {
        final String methodName = "HouseYardDispatchController:updateTally";
        LOGGER.enter(methodName + "[start]", "list:" +  tPrdWorkPlanDTO);

        boolean flag = tPrdWorkPlanService.updateDispatch(tPrdWorkPlanDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }

    /**
     * 集疏港计划 场地安排
     * @param tPrdWorkPlanDTO
     * @return
     */
    @PutMapping("/updateMass")
    @PreAuthorize("hasAuthority('dispatch:workPlan:massUpdate')")
    @Log(title = "作业计划场地安排（updateMass）", value = OperateTypeEnum.INSERT)
    public Map<String, Object> updateMass(@RequestBody TPrdWorkPlanDTO tPrdWorkPlanDTO) {
        final String methodName = "HouseYardDispatchController:updateMass";
        LOGGER.enter(methodName + "[start]", "tPrdWorkPlanDTO:" +  tPrdWorkPlanDTO);

        boolean flag = tPrdWorkPlanService.updateDispatch(tPrdWorkPlanDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "场地安排成功" : "场地安排失败").toResult();
    }

    /**
     * 集疏港计划 默认回写场地安排
     * @param planId
     * @return
     */
    @GetMapping("/getMassIdsWithPlanId")
    @Log(title = "作业计划默认指派（getMassIdsWithPlanId）", value = OperateTypeEnum.QUERY)
    public Map<String, Object> getMassIdsWithPlanId(Long planId,String tmpParam) {

        if(planId==null){
            throw new BusinessRuntimeException("请先选中计划，或刷新页面之后重试");
        }
        if(
                StringUtils.isBlank(tmpParam)
        ){
            throw new BusinessRuntimeException("缺失请求参数");
        }
        List<Map<String, Object>> result = tPrdWorkPlanService.getMassIdsWithPlanId(planId,tmpParam);


        return Response.SUCCESS.newBuilder().toResult(result);
    }


}

