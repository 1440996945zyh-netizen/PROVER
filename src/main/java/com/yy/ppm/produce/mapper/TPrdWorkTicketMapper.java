package com.yy.ppm.produce.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.appWork.bean.po.TYardTallyItemPO;
import com.yy.ppm.master.bean.po.MCargoPO;
import com.yy.ppm.master.bean.po.MPieceWorkTeamPO;
import com.yy.ppm.master.bean.po.MWorkProcessPO;
import com.yy.ppm.master.bean.po.StorageYardPO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanLocationDTO;
import com.yy.ppm.produce.bean.dto.salary.TPrdSalaryExcelDTO;
import com.yy.ppm.produce.bean.dto.workTicket.*;
import com.yy.ppm.produce.bean.po.*;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import com.yy.ppm.system.bean.po.SysDeptPO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-14 15:38
 */
public interface TPrdWorkTicketMapper {

    List<TPrdWorkPlanDTO> listWorkPlan(TPrdWorkPlanQuery query);

    List<TPrdWorkPlanDTO> listWorkPlanJsg(TPrdWorkPlanQuery query);

    List<TBusCargoInfoDTO> listTrustCargo(Long workPlanId);

    List<TBusCargoInfoDTO> listTrustCargos(List<Long> ids);

    List<TBusCargoInfoDTO> listTargetCargo(Long cargoInfoId);

    List<MPieceWorkTeamPO> listPieceWorkTeam(MPieceWorkTeamPO query);

    List<TPrdWorkPlanLocationPO> listWorkPlanLocation(Long workPlanId);

    List<TPrdDispatchSecondaryPO> listLabor(Long workPlanId);

    TPrdWorkPlanPO getWorkPlan(Long workPlanId);

    @Edit
    int insertWorkTicket(TPrdWorkTicketDTO workTicket);

    @Edit
    int insertWorkTicketJsg(TPrdWorkTicketDTO workTicket);

    @Edit
    int insertWorkTicketDetail(@Param("details") List<TPrdWorkTicketDetailDTO> details);

    @Edit
    int insertWorkTicketEquipment(@Param("equipments") List<TPrdWorkTicketEquipmentPO> equipments);

    @Edit
    int insertWorkTicketLabor(@Param("labors") List<TPrdWorkTicketLaborPO> labors);

    TPrdWorkTicketDTO getWorkTicket(@Param("workPlanId") Long workPlanId, @Param("ticketType") String ticketType,@Param("isJsg") String isJsg);

    Long getStorageYard(@Param("parentId") Long parentId, @Param("storageYardNm") String storageYardNm);

    @Edit
    int insertStorageYard(StorageYardPO storageYard);

    /**
     *
     * 根据作业票主表获取子表集合
     * @param workTicketId
     * @return
     */
    List<TPrdWorkTicketDetailDTO> listWorkTicketDetail(Long workTicketId);

    List<TPrdWorkTicketDetailDTO> listWorkTicketDetailGroup(@Param("workTicketId")Long workTicketId, @Param("groupId") Long groupId);

    List<TPrdWorkTicketEquipmentPO> listWorkTicketEquipment(@Param("workTicketDetailIds") List<Long> workTicketDetailIds);

    List<TPrdWorkTicketLaborPO> listWorkTicketLabor(Long workTicketId);

    int deleteWorkTicketDetail(Long workTicketId);

    int deleteWorkTicketEquipment(@Param("workTicketDetailIds") List<Long> workTicketDetailIds);

    int deleteWorkTicketLabor(Long workTicketId);

    @Edit
    int updateWorkTicket(TPrdWorkTicketDTO workTicket);

    int deleteWorkTicket(@Param("workPlanId") Long workPlanId,@Param("type") String type);

    @Edit
    int reviewWorkTicket(TPrdWorkTicketPO workTicket);

    List<MPieceWorkTeamPO> listPieceWorkTeamById(@Param("pieceWorkTeamIds") List<Long> pieceWorkTeamIds);

    List<MCargoPO> listCargo(@Param("cargoCodes") List<String> cargoCodes);

    List<MWorkProcessPO> listWorkProcess(@Param("processCodes") List<String> processCodes);

    List<TPrdAttendancePO> listAttendance(@Param("workDate") Date workDate, @Param("classCode") String classCode, @Param("deptIds") List<Long> deptIds);

    List<TPrdAttendanceUserPO> listAttendanceUser(@Param("attendanceIds") List<Long> attendanceIds);

    @Edit
    int insertSalary(@Param("salaries") List<TPrdSalaryPO> salaries);

    int cancelReviewWorkTicket(TPrdWorkTicketPO workTicket);

    List<TPrdPortStorageDetailPO> listPortStorageDetail(Long workTicketId);

    List<TPrdPortStorageDetailPO> listPortStorageDetailItem(List<Long> ids);

    int deleteSalary(@Param("workTicketDetailIds") List<Long> workTicketDetailIds);

    int deleteSalaryZ(Long id);

    List<Map<String, Object>> getWorkTicketGroup(Long id);

    List<SysDeptPO> listDept(@Param("deptIds") List<Long> deptIds);

    List<TPrdWorkTicketDetailDTO> getTicketInfo(Long workPlanId,String type);

    List<TPrdWorkTicketDetailDTO> getTickets(@Param("ids") List<Long> ids,@Param("type") String type);
    List<TPrdWorkTicketDTO> getTicket(@Param("workPlanId") Long workPlanId,@Param("ticketType") String ticketType);

    List<TPrdWorkTicketDetailDTO> getTicketInfoLw(@Param("code") String code,@Param("workPlanId") Long workPlanId);

    List<TPrdWorkTicketDetailDTO> getTicketInfoLwList(@Param("ids") List<Long> ids);

    Map<String, Object> getDeptInfo(Long loginUserId);

    Integer getIsProcrss(String processDetailCode);

    List<SysDeptDTO> getDepts(String type);

    List<SysDeptDTO> getDeptsTally(Long deptId);

    List<String> getProcessIsTally(@Param("processCode") String processCode,@Param("type") String type);

    List<TPrdWorkTiTckInfoDTO> getWorkTicketList(TPrdWorkPlanQuery query);

    Integer getCargoIsUpdate(String cargoCode);

    Map<String,Object> getProcessIsUpdate(String processDetailCode);

    String getDeptInternal(Long deptId);

    List<Long> getDeptRollCall(@Param("workDate") Date workDate, @Param("classCode") String classCode, @Param("deptId") Long deptId);

    List<Long> getDeptRollCallItem(@Param("workDate") Date workDate, @Param("classCode") String classCode, @Param("deptId") Long deptId);

    String getCargoWorkType(String cargoCode);

    Map<String, Object> getTallyWorkMeasure(TicketMeasureDTO ticketMeasureDTO);

    Integer getUserRole(@Param("str") String str, @Param("loginUserId") Long loginUserId);

    List<TYardTallyItemPO> getTally(@Param("ids") List<Long> ids);

    List<Map<String, Object>> getLocation(@Param("ids") List<Long> ids, @Param("type") String type);
    List<Map<String, Object>> getTallyLocation(@Param("ids") List<Long> ids);

    Map<String, Object> getShipInfo( @Param ("shipvoyageId")Long shipvoyageId, @Param("shipvoyageItemId") Long shipvoyageItemId);

    String getCargoInfo(Long cargoInfoId);

    TPrdWorkTiTckInfoDTO getIsWorkTicket(Long planId);

    List<TPrdWorkTiTckInfoDTO> getIsWorkTickets(TPrdWorkPlanQuery query);

    List<TPrdWorkPlanDTO> listWorkPlanById(@Param("workPlanId") Long workPlanId);

    TYardTallyItemPO getTallySh(@Param("id") Long planId, @Param("cargoCode") String cargoCode, @Param("startTime") String startTime,@Param("endTime") String endTime);
    TYardTallyItemPO getTallyShZq(@Param("ids") List<Long> ids, @Param("cargoCode") String cargoCode, @Param("startTime") String startTime,@Param("endTime") String endTime);

    List<TPrdWorkPlanLocationDTO> getWorkPlanLocationList(TPrdWorkPlanQuery searchDTO);

    Map<String, Object> getCargoSalaryType(String cargoCode);

    String getBanZu(Long deptId);

    Integer getIsPg(@Param("code") String code,@Param("workPlanId") Long workPlanId);

    List<Long> getTrustIDs(List<Long> ids);

    String getProcessTicketType(String processDetailCode);

    Map<String, Object> getDeptRy(Long pieceWorkTeamId);

    String getProcess(@Param("code") String processCode, @Param("type") String type);

    Map<String, Object> getDeptDm(List<Long> ids);

    String getTrustIdWicket(Long id);

    String getTrustIdWicketJsg(Long id);

    String getTicketStatus(@Param("id") Long id, @Param("type") String type);

    String getShipVoyageName(List<String> list);

    Map<String, Object> getWorkCargoInfo(Long workPlanId);

    Map<String, Object> getWorkShipInfo(Long workPlanId);

    Map<String, Object> getTrustWorkShipInfo(Long workPlanId);

    Integer getIsStations(List<String> idList);

    Integer getSalaryEx(Long id);

   Map<String, Object> getTrustShipvoyage(Long trustId);

   Map<String, Object> getShipvoyage(Long workPlanId);

    List<TPrdWorkTiTckInfoDTO> getMonthWorkTicketList(TPrdWorkPlanQuery query);


    Cursor<TPrdWorkTiTickInfoExportDTO> getExportMonthWorkTicketList(TPrdWorkPlanQuery query);

    String getEqTypeCode(Long id);

    Map<String, Object> getStorageSurplus(@Param("cargoInfoId") Long cargoInfoId, @Param("storehouseId") Long storehouseId, @Param("regionId") Long regionIdSource, @Param("massId") Long massIdSource);

    List<TPrdWorkPlanDTO> listWorkPlanMonthJsg(TPrdWorkPlanQuery query);

    List<Map<String,Object>> getShipNameByCargoInfoId(@Param("ids") List<Long> ids);

    String getProIsMeanwhile(String processDetailCode);

    List<Long> getWorkCargoTrustId(@Param("cargoInfoId") Long cargoInfoId);

    String getTicketType(Long id);
}
