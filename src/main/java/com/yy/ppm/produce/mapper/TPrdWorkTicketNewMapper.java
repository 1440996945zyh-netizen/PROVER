package com.yy.ppm.produce.mapper;

import cn.hutool.core.date.DateTime;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.appWork.bean.po.TYardTallyItemPO;
import com.yy.ppm.master.bean.dto.MWorkProcessDTO;
import com.yy.ppm.master.bean.po.MCargoPO;
import com.yy.ppm.master.bean.po.MPieceWorkTeamPO;
import com.yy.ppm.master.bean.po.MWorkProcessPO;
import com.yy.ppm.master.bean.po.StorageYardPO;
import com.yy.ppm.produce.bean.dto.DaoYunWeightGoodsDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanLocationDTO;
import com.yy.ppm.produce.bean.dto.WfSettlementInsertDto;
import com.yy.ppm.produce.bean.dto.WorkTicketTableDTO;
import com.yy.ppm.produce.bean.dto.workTicket.*;
import com.yy.ppm.produce.bean.po.*;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import com.yy.ppm.system.bean.po.SysDeptPO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;

import java.math.BigDecimal;
import java.util.*;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-14 15:38
 */
public interface TPrdWorkTicketNewMapper {

    Integer getWorkTicketCount(@Param("workPlanId") Long workPlanId);

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

    TPrdWorkTicketDTO getWorkTicket(@Param("workPlanId") Long workPlanId, 
                                    @Param("ticketType") String ticketType,
                                    @Param("isJsg") String isJsg);

    Long getStorageYard(@Param("parentId") Long parentId, @Param("storageYardNm") String storageYardNm);

    @Edit
    int insertStorageYard(StorageYardPO storageYard);



    List<TPrdWorkTicketDetailDTO> listWorkTicketDetailGroup(@Param("workTicketId")Long workTicketId, @Param("groupId") Long groupId);

    List<TPrdWorkTicketEquipmentPO> listWorkTicketEquipment(@Param("workTicketDetailIds") List<Long> workTicketDetailIds);

    List<TPrdWorkTicketLaborPO> listWorkTicketLabor(Long workTicketId);

    int deleteWorkTicketDetail(Long workTicketId);

    int deleteWorkTicketEquipment(@Param("workTicketDetailIds") List<Long> workTicketDetailIds);

    int deleteWorkTicketLabor(Long workTicketId);

    @Edit
    int updateWorkTicket(TPrdWorkTicketDTO workTicket);

    @Edit
    int updateWorkTicketList(@Param("list") List<WorkTicketTableDTO> list);

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

    /**
     * 获取当前登陆人所在部门的id
     * @param loginUserId
     * @return
     */
    Map<String, Object> getDeptLevel2InfoByUserId(@Param("loginUserId") Long loginUserId);
    Map<String, Object> getDeptLevel1InfoByUserId(@Param("loginUserId") Long loginUserId);
    Map<String, Object> getDeptLevel1InfoByDeptId(@Param("deptId") Long deptId);
    /**
     *
     * 根据作业票主表获取子表集合
     * @param workTicketId
     * @return
     */
    List<TPrdWorkTicketDetailDTO> listWorkTicketDetail(Long workTicketId);

    /**
     * 根据部门id获取父级部门
     * @param deptId
     * @return
     */
    List<SysDeptDTO> getDeptsParenet(Long deptId);

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

    /**
     * 通过理货数据汇总作业票信息
     * @param workPlanId
     * @return
     */
    List<TPrdWorkTicketDetailDTO> getTicketInfoWithTally(Long workPlanId);

    /**
     * 获取机械信息
     * @param workPlanId
     * @return
     */
    List<TPrdWorkTicketEquipmentPO> getEquipmentInfoByWorkPlanId(@Param("workPlanId") Long workPlanId);
    /**
     * 获取作业票的机械信息
     * @param workTicketDetials
     * @return
     */
    List<TPrdWorkTicketEquipmentPO> getEquipmentInfoByTicket(@Param("workTicketDetials") List<Long> workTicketDetials);

    /**
     * 获取固机队的id
     * @return
     */
    Long getDeptGJD();

    /**
     * 通过计划id获取新签票 子表
     * @param workPlanId
     * @param type
     * @return
     */
    List<TPrdWorkTicketDetailDTO> getTicketInfoByPlanId(@Param("planId") Long workPlanId,@Param("ticketType") String type,@Param("noGj") String noGj);

    /**
     * 查询新签票
     * @param workPlanId
     * @param ticketType
     * @param workTicketStatus
     * @return
     */
    TPrdWorkTicketDTO getWorkTicketInfoList(@Param("planId") Long workPlanId, @Param("ticketType") String ticketType,@Param("workTicketStatus") String workTicketStatus);

    String getWorkPlanType(@Param("workPlanId") Long workPlanId);

    List<TPrdWorkTicketDetailDTO> getTicketInfoForAdd(@Param("workPlanId") Long workPlanId, String type);

    List<TPrdWorkTicketDetailDTO> getTicketDetils(@Param("ticketId") Long id);

    List<TPrdSalaryPO> getSalary(@Param("workTicketId") Long id);
    List<TPrdSalaryPO> getSalaryByTicketDetial(@Param("workTicketDetials") List<Long> workTicketDetials);

    MWorkProcessDTO getProcessInfo(String processCode);

    List<Map<String, Object>> getDept(@Param("deptNames") List<String> list);

    List<TPrdWorkTicketDetailDTO> getPoundRecordInfo(@Param("workPlanId") Long workPlanId);

    void delTicketEquipmentByPlanId(@Param("workPlanId") Long workPlanId);

    List<Map<String, Object>> getCargoList(@Param("workPlanId") Long workPlanId);

    List<WorkTicketTableDTO> getWorkTiccketTableMain(WorkTicketTableDTO query);

    List<WorkTicketTableDTO> getWorkTiccketTableBack(@Param("ticketIds") List<Long> ticketIds);

    List<Map<String,Object>> getEqTypeCodeResultMap(Long id);

    List<Map<String, Object>> getinfo(Long id);

    //撤销理货自动撤销计件
    void delSalaryByTicketId(@Param("ticketId") Long id);
    //撤销理货自动撤销分配
    List<Long> getDaoYunList(@Param("ticketIds") List<Long> collect);

    List<WorkTicketTableDTO> getFuZhuMain(WorkTicketTableDTO query);
    List<WorkTicketTableDTO> getFuZhuMainBack(@Param("ticketIds") List<Long> ticketIds);


    List<DaoYunWeightGoodsDTO> getPlanByTicket(Long workTicketId);

    BigDecimal getDaoYunWeightGoods(DaoYunWeightGoodsDTO dto);

    /**
     * 获取非辅助计划的外付签票
     * @param query
     * @return
     */
    List<WorkTicketTableDTO> getWorkTicketTable(WorkTicketTableDTO query);
    List<WorkTicketTableDTO> getWorkTicketTableTask(WorkTicketTableDTO query);
    List<Map<String,String>> getDaoYunWeightTable(WorkTicketTableDTO query);

    List<Map<String,String>> getSettlementStatistics(Map<String,Object> map);
    List<Map<String,String>> getSettlementWFStatistics(Map<String,Object> map);
    List<Map<String,String>> getSettlementDYStatistics(Map<String,Object> map);


    /**
     * 获取辅助计划的外付签票
     * @param query
     * @return
     */
    List<WorkTicketTableDTO> getWaiFuTicketTable(WorkTicketTableDTO query);
    List<WorkTicketTableDTO> getWaiFuTicketTableTask(WorkTicketTableDTO query);
    List<WorkTicketTableDTO> getWorkTicketTableForEx(WorkTicketTableDTO query);
    List<WorkTicketTableDTO> getWaiFuTicketTableForEx(WorkTicketTableDTO query);

    void updateWaifuStatus(WorkTicketTableDTO query);
    void updateWaifuFuZhuStatus(WorkTicketTableDTO query);
    void updateWaifuStatusHr(WorkTicketTableDTO query);
    void updateWaifuFuZhuStatusHr(WorkTicketTableDTO query);


    List<Map<String, Object>> getDeptsForEx();

    int getExByWorkDate(Date workDate);

    int getFuZhuExByWorkDate(Date workDate);

    @Edit
    void insertWfSettlementBatch(@Param("list") ArrayList<WfSettlementInsertDto> wfSettlementInsertDtos);

    /**
     * 删除非倒运的外付汇总数据
     * @param startTime
     */
    void deleteSettlement(@Param("workDate") Date startTime);
    void deleteDySettlement(@Param("workDate") String workDate);

    void updateWaifuStatusHr30(WorkTicketTableDTO query);

    void updateWaifuFuZhuStatusHr30(WorkTicketTableDTO query);


    void updateSettlementStatus(
            @Param("ddStatus") String ddStatus,
            @Param("kcStatus") String kcStatus,
            @Param("lzStatus") String lzStatus,
            @Param("wfStatus") String wfStatus,
            @Param("startDate") DateTime startDate,
            @Param("endDate") DateTime endDate,
            @Param("distributeType") String distributeType
                                );

    void updateSettlementStatus1(
            @Param("ddStatus") String ddStatus,
            @Param("kcStatus") String kcStatus,
            @Param("lzStatus") String lzStatus,
            @Param("wfStatus") String wfStatus,
            @Param("startDate") DateTime startDate,
            @Param("endDate") DateTime endDate,
            @Param("distributeType") String distributeType
                                );

    void updateSettlementStatusLevel1(@Param("status") String status,
                                      @Param("distributeType") String distributeType,
                                      @Param("startDate") String date,
                                      @Param("userId") Long loginUserId,
                                      @Param("userName") String loginUserName,
                                      @Param("nowDate") Date date1);

    void updateSettlementStatusLevel2(@Param("status") String status,
                                      @Param("distributeType") String distributeType,
                                      @Param("startDate") String date,
                                      @Param("userId") Long loginUserId,
                                      @Param("userName") String loginUserName,
                                      @Param("nowDate") Date date1);

    void updateSettlementStatusLevel3(@Param("status") String status,
                                      @Param("distributeType") String distributeType,
                                      @Param("startDate") String date,
                                      @Param("userId") Long loginUserId,
                                      @Param("userName") String loginUserName,
                                      @Param("nowDate") Date date1);
    void updateSettlementStatusLevel31(@Param("status") String status,
                                      @Param("distributeType") String distributeType,
                                      @Param("startDate") String date,
                                      @Param("userId") Long loginUserId,
                                      @Param("userName") String loginUserName,
                                      @Param("nowDate") Date date1,
                                       @Param("list") List<Long> list);

    void updateSettlementStatusLevel4(@Param("status") String status,
                                      @Param("distributeType") String distributeType,
                                      @Param("startDate") String date,
                                      @Param("userId") Long loginUserId,
                                      @Param("userName") String loginUserName,
                                      @Param("nowDate") Date date1);
    void updateSettlementStatusLevel41(@Param("status") String status,
                                      @Param("distributeType") String distributeType,
                                      @Param("startDate") String date,
                                      @Param("userId") Long loginUserId,
                                      @Param("userName") String loginUserName,
                                      @Param("nowDate") Date date1,
                                      @Param("list") List<Long> list);

    WfSettlementInsertDto getWfSettkementList(@Param("distributeType") String distributeType,@Param("setmentDate") Date date);

    SysDeptDTO getByLoginUserId(@Param("loginUserId") Long loginUserId);

    SysDeptDTO getInOutTypeByLoginUserId(@Param("loginUserId") Long loginUserId);

    void updateWaiFuTicketPrdStatus(@Param("ids") List<Long> list,
                                    @Param("status") String code,
                                    @Param("userId") Long loginUserId,
                                    @Param("userName") String loginUserName,
                                    @Param("handleDate") Date date);
    void updateWaiFuTicketHrStatus( @Param("ids") List<Long> list,
                                    @Param("status") String code,
                                    @Param("userId") Long loginUserId,
                                    @Param("userName") String loginUserName,
                                    @Param("handleDate") Date date);

    void updateWaiFuTicketHr( @Param("items") List<WorkTicketTableDTO> list,
                              @Param("status") String code,
                              @Param("userId") Long loginUserId,
                              @Param("userName") String loginUserName,
                              @Param("handleDate") Date date);

    void updateWaiFuTicketHrV( @Param("items") List<WorkTicketTableDTO> list,
                               @Param("wbPackingName") String wbPackingName,
                               @Param("status") String code,
                               @Param("userId") Long loginUserId,
                               @Param("userName") String loginUserName,
                               @Param("handleDate") Date date);

    void insertWfHRNewBatch(@Param("list") List<WorkTicketTableDTO> workTiccketTableV21);

    void deleteWfHRNewBatch(@Param("workDate") Date startTime);

    List<WorkTicketTableDTO> getWfHrNewTableList(WorkTicketTableDTO query);

    void updateWfHrWorkTicketList(@Param("list") List<WorkTicketTableDTO> updateList);

    void updateNewWaiFuCalHr( @Param("items") List<WorkTicketTableDTO> list,
                              @Param("status") String code,
                              @Param("userId") Long loginUserId,
                              @Param("userName") String loginUserName,
                              @Param("handleDate") Date date);
}
