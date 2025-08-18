package com.yy.ppm.produce.service;

import com.yy.ppm.master.bean.po.MPieceWorkTeamPO;
import com.yy.ppm.produce.bean.dto.workTicket.*;
import com.yy.ppm.produce.bean.po.TPrdDispatchSecondaryPO;
import com.yy.ppm.produce.bean.po.TPrdWorkPlanLocationPO;
import com.yy.ppm.system.bean.dto.SysDeptDTO;

import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-14 15:37
 */
public interface TPrdWorkTicketService {

    List<TPrdWorkPlanDTO> listWorkPlan(TPrdWorkPlanQuery query);

    List<TBusCargoInfoDTO> listTrustCargo(Long workPlanId);

    List<TBusCargoInfoDTO> listTargetCargo(Long cargoInfoId);

    List<MPieceWorkTeamPO> listPieceWorkTeam(MPieceWorkTeamPO query);

    List<TPrdWorkPlanLocationPO> listWorkPlanLocation(Long workPlanId);

    List<TPrdDispatchSecondaryPO> listLabor(Long workPlanId);

    void insertWorkTicket(TPrdWorkTicketDTO workTicket);

    TPrdWorkTicketDTO getWorkTicket(Long workPlanId,String ticketType);

    void updateWorkTicket(TPrdWorkTicketDTO workTicket);

    void deleteWorkTicket(Long workPlanId,String type);

    void reviewWorkTicket(TicketPlanIdDTO ticketPlanIdDTO);

    void reviewWorkTicketJsg(TPrdWorkPlanJsgDTO tPrdWorkPlanJsgDTO);

    void cancelReviewWorkTicket(TicketPlanIdDTO ticketPlanIdDTO);

    List<TPrdWorkTicketDetailDTO> getTicketInfo(Long workPlanId,String type,String cargoCode,String processCode);

    List<SysDeptDTO> getDepts(String type);
    List<SysDeptDTO> getDeptsTally();

    Integer getProcessType(String code);

    List<String> getProcessIsTally(String processCode,String type,String cargoCode,Long workPlanId);

    String getProcess(String processCode,String type);

    List<TPrdWorkTiTckInfoDTO> getWorkTicketList(TPrdWorkPlanQuery query);

    Map<String, Object> getWorkMeasure(TicketMeasureDTO ticketMeasureDTO);

    Integer getUserRole(String flag);

    List<TPrdWorkTiTckInfoDTO> getMonthWorkTicketList(TPrdWorkPlanQuery query);

    byte[] exportExcel(TPrdWorkPlanQuery query);
}
