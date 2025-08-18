package com.yy.ppm.produce.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.business.bean.dto.TBusTrustDTO;
import com.yy.ppm.produce.bean.dto.TPrdOddLogResultDTO;
import com.yy.ppm.produce.bean.dto.TPrdOddResultDTO;
import com.yy.ppm.produce.bean.dto.TPrdOddSaveDTO;
import com.yy.ppm.produce.bean.dto.TPrdOddSearchDTO;
import com.yy.ppm.produce.service.TPrdOddWorkPlanService;
import com.yy.ppm.produce.service.TPrdWorkTicketService;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @ClassName 零工申请
 * @author wangxd
 * @version 1.0.0
 * @Description
 * @createTime 2023年12月12日 11:21:00
 */
@RestController
@RequestMapping("/api/v1/interface/tPrdOddInterface")
public class TPrdOddWorkPlanController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(TPrdOddWorkPlanController.class);

    @Autowired
    private TPrdOddWorkPlanService tPrdOddWorkPlanService;
    @Autowired
    private TPrdWorkTicketService tPrdWorkTicketService;

    /**
     * 查询
     * @param dto
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(TPrdOddSearchDTO dto, PageParameter parameter) {
        final String methodName = "TPrdOddWorkPlanController:getList";
		LOGGER.enter(methodName + "[start]", "dto:" +  dto);

        Pages<TPrdOddResultDTO> result = tPrdOddWorkPlanService.getList(dto, parameter);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 查询日志
     * @param id
     * @return
     */
    @GetMapping("/getLogList")
    public Map<String, Object> getLogList(@NotNull(message = "ID不能为空") @RequestParam("id") Long id, PageParameter parameter) {
        final String methodName = "TPrdOddWorkPlanController:getLogList";
        LOGGER.enter(methodName + "[start]", "id:" +  id);

        List<TPrdOddLogResultDTO> result = tPrdOddWorkPlanService.getLogList(id, parameter);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    @GetMapping("/getDetail")
    public Map<String, Object> getDetail(@NotNull(message = "ID不能为空") @RequestParam("id") Long id) {
        final String methodName = "TPrdOddWorkPlanController:getDetail";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        TPrdOddResultDTO result = tPrdOddWorkPlanService.getDetail(id);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    /**
     * 查询所有班组
     *
     * @param
     * @return
     */
    @GetMapping("/getDepts")
    public Map<String, Object> getDepts(String type) {
        List<SysDeptDTO> result = tPrdWorkTicketService.getDepts(type);
        result.stream().forEach(item -> {
            item.setValue(item.getDeptNo());
            item.setLabel(item.getDeptName());
        });
        return Response.SUCCESS.newBuilder().toResult(result);
    }
    /**
     * 保存
     * @param dto
     * @return
     */
    @PostMapping("/doSave")
    public Map<String, Object> doSave(@RequestBody TPrdOddSaveDTO dto) {
        final String methodName = "TPrdOddWorkPlanController:doSave";
        LOGGER.enter(methodName + "[start]", "dto:" +  dto);

        tPrdOddWorkPlanService.doSave(dto);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("操作成功").toResult();
    }

    /**
     * 确认
     * @param dto
     * @return
     */
    @PostMapping("/confirm")
    public Map<String, Object> confirm(@RequestBody TPrdOddSaveDTO dto) {
        final String methodName = "TPrdOddWorkPlanController:confirm";
        LOGGER.enter(methodName + "[start]", "dto:" +  dto);

        tPrdOddWorkPlanService.confirm(dto);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("操作成功").toResult();
    }

    /**
     * 驳回
     * @param dto
     * @return
     */
    @PostMapping("/reject")
    public Map<String, Object> reject(@RequestBody TPrdOddSaveDTO dto) {
        final String methodName = "TPrdOddWorkPlanController:reject";
        LOGGER.enter(methodName + "[start]", "dto:" +  dto);

        tPrdOddWorkPlanService.reject(dto);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("操作成功").toResult();
    }

    /**
     * 作废
     * @param dto
     * @return
     */
    @PostMapping("/abandoned")
    public Map<String, Object> abandoned(@RequestBody TPrdOddSaveDTO dto) {
        final String methodName = "TPrdOddWorkPlanController:abandoned";
        LOGGER.enter(methodName + "[start]", "dto:" +  dto);

        tPrdOddWorkPlanService.abandoned(dto);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("操作成功").toResult();
    }
    /**
     * 统一更新零工单号
     * @return
     */
    @GetMapping("/updateOddPlanNo")
    public Map<String, Object> updateOddPlanNo() {
        final String methodName = "TPrdOddWorkPlanController:updateOddPlanNo";
        LOGGER.enter(methodName + "[start]", "");

        tPrdOddWorkPlanService.autoOddPlanNo();

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("操作成功").toResult();
    }

    /**
     * 取消确认
     * @param id
     * @return
     */
    @GetMapping("/cancelConfirm")
    public Map<String, Object> cancelConfirm(@RequestParam(value = "id") Long id) {
        final String methodName = "TPrdOddWorkPlanController:cancelConfirm";
        LOGGER.enter(methodName + "[start]", "id:" +  id);

        tPrdOddWorkPlanService.cancelConfirm(id);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("操作成功").toResult();
    }
    /**
     * 取消一级审核
     * @param id
     * @return
     */
    @GetMapping("/cancelFirstApprove")
    public Map<String, Object> cancelFirstApprove(@RequestParam(value = "id") Long id) {
        final String methodName = "TPrdOddWorkPlanController:cancelFirstApprove";
        LOGGER.enter(methodName + "[start]", "id:" +  id);

        tPrdOddWorkPlanService.cancelFirstApprove(id);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("操作成功").toResult();
    }
    /**
     * 取消二级审核
     * @param ids
     * @return
     */
    @PostMapping("/cancelSecondApprove")
    public Map<String, Object> cancelSecondApprove(@RequestBody  List<Long> ids) {
        final String methodName = "TPrdOddWorkPlanController:cancelSecondApprove";
        LOGGER.enter(methodName + "[start]", "ids:" +  ids);

        tPrdOddWorkPlanService.cancelSecondApprove(ids);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("操作成功").toResult();
    }

    /**
     * 一级审批
     * @param dto
     * @return
     */
    @PostMapping("/firstApprove")
    public Map<String, Object> firstApprove(@RequestBody TPrdOddSaveDTO dto) {
        final String methodName = "TPrdOddWorkPlanController:firstApprove";
        LOGGER.enter(methodName + "[start]", "dto:" +  dto);

        tPrdOddWorkPlanService.firstApprove(dto);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("操作成功").toResult();
    }

    /**
     * 二级审批
     * @param ids
     * @return
     */
    @PostMapping("/secondApprove")
    public Map<String, Object> secondApprove(@RequestBody  List<Long> ids) {
        final String methodName = "TPrdOddWorkPlanController:secondApprove";
        LOGGER.enter(methodName + "[start]", "dto:" +  ids);

        tPrdOddWorkPlanService.secondApprove(ids);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("操作成功").toResult();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "TPrdOddWorkPlanController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tPrdOddWorkPlanService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 查询部门
     *
     * @param level 部门等级；type 部门类型，machine 机械队；labor 装卸队
     * @return
     */
    @GetMapping("/getDeptByType")
    public Map<String, Object> getDeptByType(@RequestParam("level") @NotNull(message = "查询部门参数不能为空") Integer level,
                                             @RequestParam("type") @NotNull(message = "查询部门参数不能为空") String type) {
        List<SysDeptDTO> result = tPrdOddWorkPlanService.getDeptByType(level, type);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 三级审批
     * @param ids
     * @return
     */
    @PostMapping("/thirdApprove")
    public Map<String, Object> thirdApprove(@RequestBody  List<Long> ids) {
        final String methodName = "TPrdOddWorkPlanController:thirdApprove";
        LOGGER.enter(methodName + "[start]", "dto:" +  ids);

        tPrdOddWorkPlanService.thirdApprove(ids);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("操作成功").toResult();
    }

    /**
     * 取消三级审核
     * @param ids
     * @return
     */
    @PostMapping("/cancelThirdApprove")
    public Map<String, Object> cancelThirdApprove(@RequestBody  List<Long> ids) {
        final String methodName = "TPrdOddWorkPlanController:cancelThirdApprove";
        LOGGER.enter(methodName + "[start]", "ids:" +  ids);

        tPrdOddWorkPlanService.cancelThirdApprove(ids);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("操作成功").toResult();
    }

}

