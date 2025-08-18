package com.yy.ppm.produce.controller;

import cn.hutool.core.io.IORuntimeException;
import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.excel.export.utils.ResponseUtils;
import com.yy.common.util.ValidatorUtils;
import com.yy.framework.annotation.Log;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.master.bean.po.MPieceWorkTeamPO;
import com.yy.ppm.produce.bean.dto.salary.SalaryQueryDTO;
import com.yy.ppm.produce.bean.dto.workTicket.*;
import com.yy.ppm.produce.bean.po.TPrdDispatchSecondaryPO;
import com.yy.ppm.produce.bean.po.TPrdWorkPlanLocationPO;
import com.yy.ppm.produce.service.TPrdWorkTicketService;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description 签票
 * @Date 2023-08-14 15:23
 */
@RestController
@RequestMapping("/api/external/workTicket")
@Validated
public class TPrdWorkTicketController {

    @Autowired
    private TPrdWorkTicketService tPrdWorkTicketService;

    /**
     * 作业计划列表
     *
     * @param query
     * @return
     */
    @GetMapping("/listWorkPlan")
    public Map<String, Object> listWorkPlan(TPrdWorkPlanQuery query) {
        if (query.getWorkDate() == null) {
            throw new BusinessRuntimeException("作业日期不能为空");
        }
        if (StringUtils.isBlank(query.getClassCode())) {
            throw new BusinessRuntimeException("作业班次不能为空");
        }
        if (StringUtils.isBlank(query.getPlanType())) {
            throw new BusinessRuntimeException("计划类型不能为空");
        }
        List<TPrdWorkPlanDTO> result = tPrdWorkTicketService.listWorkPlan(query);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 作业计划id查指令票货
     *
     * @param workPlanId
     * @return
     */
    @GetMapping("/listTrustCargo")
    public Map<String, Object> listTrustCargo(@NotNull(message = "作业计划id不能为空") Long workPlanId) {
        List<TBusCargoInfoDTO> result = tPrdWorkTicketService.listTrustCargo(workPlanId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 票货id查目标票货
     *
     * @param cargoInfoId
     * @return
     */
    @GetMapping("/listTargetCargo")
    public Map<String, Object> listTargetCargo(@NotNull(message = "票货id不能为空") Long cargoInfoId) {
        List<TBusCargoInfoDTO> result = tPrdWorkTicketService.listTargetCargo(cargoInfoId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     *获取作业量
     *
     * @param ticketMeasureDTO
     * @return
     */
    @GetMapping("/getWorkMeasure")
    public Map<String, Object> getWorkMeasure(TicketMeasureDTO ticketMeasureDTO) {
        Map<String,Object> result = tPrdWorkTicketService.getWorkMeasure(ticketMeasureDTO);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     *获取当前用户是否是调度/库场角色
     *
     * @param flag
     * @return
     */
    @GetMapping("/getUserRole")
    public Map<String, Object> getUserRole(String flag) {
        Integer count = tPrdWorkTicketService.getUserRole(flag);
        return Response.SUCCESS.newBuilder().toResult(count);
    }

    /**
     * 操作工班列表
     *
     * @param query
     * @return
     */
    @GetMapping("/listPieceWorkTeam")
    public Map<String, Object> listPieceWorkTeam(MPieceWorkTeamPO query) {
        List<MPieceWorkTeamPO> result = tPrdWorkTicketService.listPieceWorkTeam(query);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 作业计划id查起始终点位置
     *
     * @param workPlanId
     * @return
     */
    @GetMapping("/listWorkPlanLocation")
    public Map<String, Object> listWorkPlanLocation(@NotNull(message = "作业计划id不能为空") Long workPlanId) {
        List<TPrdWorkPlanLocationPO> result = tPrdWorkTicketService.listWorkPlanLocation(workPlanId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 根据计划ID查询作业票信息
     *
     * @param workPlanId
     * @return
     */
    @GetMapping("/getTicketInfo")
    public Map<String, Object> getTicketInfo(@NotNull(message = "请选择一条计划") Long workPlanId,String type,String cargoCode,String processCode) {
        List<TPrdWorkTicketDetailDTO> result = tPrdWorkTicketService.getTicketInfo(workPlanId,type,cargoCode,processCode);
        return Response.SUCCESS.newBuilder().toResult(result);
    }


    /**
     * 查询子作业过程是否作为理货量
     *
     * @param code
     * @return
     */
    @GetMapping("/getProcessType")
    public Map<String, Object> getProcessType(@NotNull(message = "作业过程代码不能为空") String code) {
        Integer result = tPrdWorkTicketService.getProcessType(code);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 查询所有班组
     *
     * @param
     * @return
     */
    @GetMapping("/getDepts")
    public Map<String, Object> getDepts(@NotNull(message = "查询部门参数不能为空") String type) {
        List<SysDeptDTO> result = tPrdWorkTicketService.getDepts(type);
        return Response.SUCCESS.newBuilder().toResult(result);
    }


    /**
     * 查询理货量班组
     *
     * @param
     * @return
     */
    @GetMapping("/getDeptsTally")
    public Map<String, Object> getDeptsTally() {
        List<SysDeptDTO> result = tPrdWorkTicketService.getDeptsTally();
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 根据主作业过程查询子作业过程是否理货量
     *
     * @param
     * @return
     */
    @GetMapping("/getProcessIsTally")
    public Map<String, Object> getProcessIsTally(String processCode,String type,String cargoCode,Long workPlanId) {
        List<String> result = tPrdWorkTicketService.getProcessIsTally(processCode,type,cargoCode,workPlanId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 查询相关作业过程
     *
     * @param
     * @return
     */
    @GetMapping("/getProcess")
    public Map<String, Object> getProcess(String processCode,String type) {
        String result = tPrdWorkTicketService.getProcess(processCode,type);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 劳务队列表
     *
     * @param workPlanId
     * @return
     */
    @GetMapping("/listLabor")
    public Map<String, Object> listLabor(@NotNull(message = "作业计划id不能为空") Long workPlanId) {
        List<TPrdDispatchSecondaryPO> result = tPrdWorkTicketService.listLabor(workPlanId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 新增作业票
     *
     * @param workTicket
     * @return
     */
    @Log(title ="新增作业票",value = OperateTypeEnum.INSERT)
    @PostMapping("/insertWorkTicket")
    public Map<String, Object> insertWorkTicket(@RequestBody TPrdWorkTicketDTO workTicket) {
        ValidatorUtils.FieldBean bean;
        if ((bean = ValidatorUtils.validator(workTicket)).isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        for (TPrdWorkTicketDetailDTO v1 : workTicket.getDetails()) {
            if ((bean = ValidatorUtils.validator(v1)).isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
            if (CollectionUtils.isNotEmpty(v1.getEquipments())) {
                if ((bean = ValidatorUtils.validator(v1.getEquipments())).isSuccess()) {
                    throw new BusinessRuntimeException(bean.getMsg());
                }
            }
        }
        if (CollectionUtils.isNotEmpty(workTicket.getLabors())) {
            if ((bean = ValidatorUtils.validator(workTicket.getLabors())).isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
        }

        tPrdWorkTicketService.insertWorkTicket(workTicket);
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 查询作业票
     *
     * @param workPlanId
     * @return
     */
    @GetMapping("/getWorkTicket")
    public Map<String, Object> getWorkTicket(@NotNull(message = "作业计划id不能为空") Long workPlanId,String ticketType) {
        TPrdWorkTicketDTO result = tPrdWorkTicketService.getWorkTicket(workPlanId,ticketType);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 查询作业票列表
     *
     * @param query
     * @return
     */
    @GetMapping("/getWorkTicketList")
    public Map<String, Object> getWorkTicketList(TPrdWorkPlanQuery query) {
        List<TPrdWorkTiTckInfoDTO> result = tPrdWorkTicketService.getWorkTicketList(query);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 修改作业票
     *
     * @param workTicket
     * @return
     */
    @Log(title ="修改作业票",value = OperateTypeEnum.UPDATE)
    @PutMapping("/updateWorkTicket")
    public Map<String, Object> updateWorkTicket(@RequestBody TPrdWorkTicketDTO workTicket) {
        ValidatorUtils.FieldBean bean;
        if ((bean = ValidatorUtils.validator(workTicket)).isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        for (TPrdWorkTicketDetailDTO v1 : workTicket.getDetails()) {
            if ((bean = ValidatorUtils.validator(v1)).isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
            if (CollectionUtils.isNotEmpty(v1.getEquipments())) {
                if ((bean = ValidatorUtils.validator(v1.getEquipments())).isSuccess()) {
                    throw new BusinessRuntimeException(bean.getMsg());
                }
            }
        }
        if (CollectionUtils.isNotEmpty(workTicket.getLabors())) {
            if ((bean = ValidatorUtils.validator(workTicket.getLabors())).isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
        }
        tPrdWorkTicketService.updateWorkTicket(workTicket);
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 删除作业票
     *
     * @param workPlanId
     * @return
     */
    @Log(title ="删除作业票",value = OperateTypeEnum.DELETE)
    @DeleteMapping("/deleteWorkTicket")
    public Map<String, Object> deleteWorkTicket(@NotNull(message = "作业计划id不能为空") Long workPlanId,String type) {
        tPrdWorkTicketService.deleteWorkTicket(workPlanId,type);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 审核作业票
     *
     * @return
     */
    @Log(title ="审核作业票",value = OperateTypeEnum.UPDATE)
    @PutMapping("/reviewWorkTicket")
    public Map<String, Object> reviewWorkTicket(@RequestBody  TicketPlanIdDTO ticketPlanIdDTO) {
        tPrdWorkTicketService.reviewWorkTicket(ticketPlanIdDTO);
        return Response.SUCCESS.newBuilder().out("审核成功").toResult();
    }

    /**
     * 审核作业票集疏港
     *
     * @return
     */
    @PutMapping("/reviewWorkTicketJsg")
    public Map<String, Object> reviewWorkTicketJsg(@RequestBody  TPrdWorkPlanJsgDTO tPrdWorkPlanJsgDTO) {
        tPrdWorkTicketService.reviewWorkTicketJsg(tPrdWorkPlanJsgDTO);
        return Response.SUCCESS.newBuilder().out("审核成功").toResult();
    }

    /**
     * 销审作业票
     *
     * @return
     */
    @PutMapping("/cancelReviewWorkTicket")
    public Map<String, Object> cancelReviewWorkTicket(@RequestBody  TicketPlanIdDTO ticketPlanIdDTO) {
        tPrdWorkTicketService.cancelReviewWorkTicket(ticketPlanIdDTO);
        return Response.SUCCESS.newBuilder().out("销审成功").toResult();
    }



    /**
     * 查询作业票列表（整月查询模块）
     *
     * @param query
     * @return
     */
    @GetMapping("/getMonthWorkTicketList")
    public Map<String, Object> getMonthWorkTicketList(TPrdWorkPlanQuery query) {
        List<TPrdWorkTiTckInfoDTO> result = tPrdWorkTicketService.getMonthWorkTicketList(query);
        return Response.SUCCESS.newBuilder().toResult(result);
    }


    /**
     * 签票审核整月查询导出
     *
     * @param query
     * @param response
     * @return
     */
    @GetMapping("/exportExcel")
    public void exportExcel(TPrdWorkPlanQuery query, HttpServletResponse response) {
        ResponseUtils.compliantWithExcel(response, "签票审核");
        try {
            byte[] bytes = tPrdWorkTicketService.exportExcel(query);
            try {
                response.getOutputStream().write(bytes);
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        } catch (Exception e) {
            ResponseUtils.resetCompliant(response);
            throw e;
        }
    }

}
