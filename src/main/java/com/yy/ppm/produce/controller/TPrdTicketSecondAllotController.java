package com.yy.ppm.produce.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.page.Pages;
import com.yy.common.util.ValidatorUtils;
import com.yy.framework.annotation.Log;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.produce.bean.dto.workTicket.TPrdTicketSeconAllotQuery;
import com.yy.ppm.produce.bean.dto.workTicket.TPrdWorkTicketDTO;
import com.yy.ppm.produce.bean.dto.workTicket.TPrdWorkTicketDetailDTO;
import com.yy.ppm.produce.bean.dto.workTicket.TPrdWorkTicketResDTO;
import com.yy.ppm.produce.service.TPrdTicketSecondAllotService;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @Auther linqi （new ^_^）
 * @Description 新流程签票
 * @Date 2023-08-14 15:23
 */
@RestController
@RequestMapping("/api/external/ticketSecondAllot")
@Validated
public class TPrdTicketSecondAllotController {

    @Autowired
    private TPrdTicketSecondAllotService ticketSecondAllotService;

    /**
     * 主列表
     *
     * @param query
     * @return
     */
    @GetMapping("/listTicket")
    public Map<String, Object> listTicket(TPrdTicketSeconAllotQuery query) {
        List<TPrdWorkTicketResDTO> result = ticketSecondAllotService.listTicket(query);
        return Response.SUCCESS.newBuilder().toResult(result);
    }
    /**
     * 详情
     *
     * @param query
     * @return
     */
    @GetMapping("/listDetailForAllot")
    public Map<String, Object> listDetailForAllot(TPrdTicketSeconAllotQuery query) {
        if(query.getId()==null){
            throw new BusinessRuntimeException("缺少签票主表id");
        }
        if(query.getWorkPlanId()==null){
            throw new BusinessRuntimeException("缺少作业计划信息");
        }
//        List<TPrdWorkTicketDetailDTO> result = ticketSecondAllotService.listDetailForAllot(query);
        List<TPrdWorkTicketDetailDTO> result = ticketSecondAllotService.listDetailForAllotNoCargo(query);

        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 新增
     */
    /**
     * (新)新增作业票
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

//        ticketSecondAllotService.insertWorkTicket(workTicket);
        ticketSecondAllotService.insertWorkTicketNoCargo(workTicket);
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 撤销分配
     * @param id
     * @param allotType
     * @return
     */
    @Log(title ="撤销签票",value = OperateTypeEnum.DELETE)
    @DeleteMapping("/deleteAllot")
    public Map<String, Object> deleteAllot(@NotNull(message = "签票id不能为空") Long id,@NotNull(message = "分配类型不能为空") String allotType) {
        ticketSecondAllotService.deleteAllot(id,allotType);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 查询作业部门  根据 sys_dept 中的 TICKET_LEVEL
     *
     * @param
     * @return
     */
    @GetMapping("/getDepts")
    public Map<String, Object> getDepts(String allotType) {
        List<SysDeptDTO> result = ticketSecondAllotService.getDepts(allotType);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

}
