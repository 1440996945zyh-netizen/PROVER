package com.yy.ppm.produce.service;

import com.yy.common.page.PageParameter;
import com.yy.ppm.appWork.bean.dto.TallyRecordSearchDTO;
import com.yy.ppm.master.bean.po.MPieceWorkTeamPO;
import com.yy.ppm.produce.bean.dto.TPrdDySumDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanSearchDTO;
import com.yy.ppm.produce.bean.dto.WorkTicketTableDTO;
import com.yy.ppm.produce.bean.dto.workTicket.*;
import com.yy.ppm.produce.bean.po.TPrdDispatchSecondaryPO;
import com.yy.ppm.produce.bean.po.TPrdWorkPlanLocationPO;
import com.yy.ppm.produce.bean.po.TPrdWorkTicketEquipmentPO;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-14 15:37
 */
public interface TPrdWorkTicketNewService {

    List<TPrdWorkPlanDTO> listWorkPlan(TPrdWorkPlanQuery query);

    List<com.yy.ppm.produce.bean.dto.TPrdWorkPlanDTO> lgListWorkPlan(TPrdWorkPlanSearchDTO searchDTO);

    List<TBusCargoInfoDTO> listTrustCargo(Long workPlanId);

    List<TBusCargoInfoDTO> listTargetCargo(Long cargoInfoId);

    List<MPieceWorkTeamPO> listPieceWorkTeam(MPieceWorkTeamPO query);

    List<TPrdWorkPlanLocationPO> listWorkPlanLocation(Long workPlanId);

    List<TPrdDispatchSecondaryPO> listLabor(Long workPlanId);

    void insertWorkTicket(TPrdWorkTicketDTO workTicket);
    void lgInsertWorkTicket(TPrdWorkTicketDTO workTicket);

    TPrdWorkTicketDTO getWorkTicket(Long workPlanId,String ticketType);

    void updateWorkTicket(TPrdWorkTicketDTO workTicket);

//    void updateWorkTicketTable(@Param("list") List<WorkTicketTableDTO> list);

    void deleteWorkTicket(Long workPlanId,String type);


    void cancelReviewWorkTicket(TicketPlanIdDTO ticketPlanIdDTO);

    List<TPrdWorkTicketDetailDTO> getTicketInfo(Long workPlanId,String type,String cargoCode,String processCode);
    List<TPrdWorkTicketDetailDTO> getLgTicketInfo(Long workPlanId,String type,String cargoCode,String processCode);

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

    List<TPrdWorkTicketDetailDTO> getTicketInfoForAdd(Long workPlanId, String type, String cargoCode, String processCode);

    List<TPrdWorkTicketEquipmentPO> getInitMahineSlectData(Long workPlanId, String type);

    Map<String, Object> getSummaryQuantityTon(TallyRecordSearchDTO tallyRecordSearchDTO, PageParameter pageParameter);

    List<WorkTicketTableDTO> getWorkTiccketTable(WorkTicketTableDTO query);

    byte[] exportWaiFuTableExcel(WorkTicketTableDTO query);

    List<WorkTicketTableDTO> getWorkTiccketTableV2(WorkTicketTableDTO query);

    List<WorkTicketTableDTO> getWorkTicketDetailList(WorkTicketTableDTO query);

    List<Map<String,String>> getDaoYunWeightTable(WorkTicketTableDTO query);

    List<Map<String,String>> getSettlementStatistics(Map<String,Object> map);

    Boolean updateDaoYunWeightStatus(List<TPrdDySumDTO> query, String settlementDate);

    Boolean updateSettlementStatus(List<Map<String,Object>> query, String settlementDate,String type);

    List<WorkTicketTableDTO> getWfHrNewTable(WorkTicketTableDTO query);

//    Boolean updateSettlementStatus1(List<Map<String,Object>> query, String settlementDate);
}
