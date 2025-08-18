package com.yy.ppm.appWork.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.appWork.bean.dto.*;
import com.yy.ppm.appWork.bean.po.THqYardTallyPO;
import com.yy.ppm.appWork.bean.po.TYardTallyItemPO;
import com.yy.ppm.appWork.bean.po.TYardTallyPO;
import com.yy.ppm.business.bean.dto.TBusTrustCargoDTO;
import com.yy.ppm.dispatch.bean.dto.disShipDynamic.TDisShipvoyageDTO;
import com.yy.ppm.master.bean.dto.MWorkProcessSearchDTO;
import com.yy.ppm.master.bean.po.MWorkProcessPO;
import com.yy.ppm.produce.bean.dto.THqDataDTO;
import com.yy.ppm.produce.bean.dto.TPrdDispatchSecondarySearchDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanSearchDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TallyMapper {

  List<TPrdWorkPlanDTO> getWorkPlan(TPrdWorkPlanSearchDTO searchDTO);

  List<Map<String,Object>> getWorkPlanCargoInfo(TPrdWorkPlanSearchDTO searchDTO);

  List<Map<String,Object>> getProcessInfoList(MWorkProcessSearchDTO mWorkProcessSearchDTO);

  List<Map<String, Object>> getMechanics(TPrdDispatchSecondarySearchDTO tPrdDispatchSecondarySearchDTO);

  List<Map<String, Object>> getTransfer(TPrdDispatchSecondarySearchDTO tPrdDispatchSecondarySearchDTO);

  @DS("simeauto")
  List<Map<String, Object>> getCarFer(String planCode);

  String getWeightStatus(String id);

  void updateWeightStatus(String id);

  String  getPlanCode(TPrdDispatchSecondarySearchDTO tPrdDispatchSecondarySearchDTO);

  List<Map<String, Object>> getCargoInfoId(@Param("trustId") String trustId,@Param("planId") String planId);

  List<Map<String, Object>> getCargoInfoIdTr(String trustId);

  /**
   * 查询关联ID信息(后场)
   * @param tYardTallyPO
   * @return
   */
  Integer getRelationIdHc(TYardTallyPO tYardTallyPO);

  /**
   * 查询关联ID信息(前沿)
   * @param tYardTallyPO
   * @return
   */
  Integer getRelationIdQy(TYardTallyPO tYardTallyPO);

  /**
   * 更新理货记录关联ID
   * @param tYardTallyPO
   * @return
   */
  int updateRelationId(TYardTallyPO tYardTallyPO);

  /**
   * 新增理货主表
   * @param tYardTallyPO
   * @return
   */
  @Edit
  int tally(TYardTallyPO tYardTallyPO);

  /**
   * 新增理货子表(木材入场理货)
   */
  @Edit
  void tallyItem( @Param("list") List<TYardTallyItemPO> list);


  DunBaoTallyDTO getDbTallyById(@Param("id") Long id);

  List<DunBaoTallyDTO> dbTallyByWeighbridgeId(@Param("id") String id);

  List<DunBaoTallyDTO> dbTallyByPoundId(@Param("reservationPoundId") String reservationPoundId);

  List<DunBaoTallyItemDTO> getDbTallyItemByDbTallyId(@Param("ids") List<Long> ids);

  @Edit
  int saveDbTally(DunBaoTallyDTO dto);

  @Edit
  int updateDbTally(DunBaoTallyDTO dto);

  void deleteDbTallyItemByDbId(@Param("id") Long id);
  /**
   * 新增理货子表(木材入场理货)
   */
  @Edit
  void saveDbTallyItem( @Param("list") List<DunBaoTallyItemDTO> list);

  /**
   * 根据计划id和作业过程查询理货记录
   * @param tallyRecordSearchDTO
   * @return
   */
  Page<TYardTallyItemPO> getTallyRecord(TallyRecordSearchDTO tallyRecordSearchDTO );

  Page<THqYardTallyPO> getHqTallyRecord(TallyRecordSearchDTO tallyRecordSearchDTO );

  List<THqDataDTO> getHqTallyData(TallyRecordSearchDTO tallyRecordSearchDTO );

  List<Map<String, Object>> getHqCargoName();

  List<Map<String, Object>> getHqYard();

  /**
   * 根据计划id和作业过程理货记录汇总
   * @param tallyRecordSearchDTO
   * @return
   */
  Map<String, Object> getTallyRecordSum(TallyRecordSearchDTO tallyRecordSearchDTO);
  Map<String, Object> getTallyRecordSumNew(TallyRecordSearchDTO tallyRecordSearchDTO);

  List<Map<String, Object>> getStore(Long workPlanId);

  List<Map<String, Object>> getStackPosition(Long id);

  @Edit
  void insertStackPosition(Map<String, Object> map);

  List<TYardTallyPO> getWaitWork(Long trustId);

  /**
   * 查看货物是否是理货节点
   * @param cargoCode
   */
  Integer getIsTally(String cargoCode);

  /**
   * 根据指令ID查询票货ID
   * @param trustId
   * @return
   */
  String getTrustCargoId(Long trustId);

  /**
   * 查询出入库记录(港存)
   * @param tYardMeasureSearchDTO
   * @return
   */
  List<AppTallyLadingDTO> getCarDetailedList(TYardMeasureSearchDTO tYardMeasureSearchDTO);
  List<AppTallyLadingDTO> getPortStorgeByPlan(TYardMeasureSearchDTO tYardMeasureSearchDTO);
  List<Map<String,Object>> getPortStorgeByPlanId(TYardMeasureSearchDTO tYardMeasureSearchDTO);

  String selectBh(String code);

  String getInoutType(String processCode);

  @Edit
  int deleteNotes(TYardTallyPO po);

  int deleteNotesItem(Long tallyId);

  void updateNotesRelationId(Long tallyId);

  List<Long> getPortStorageDetailId(Long tallyId);

  List<Long> getPortStorageDetailItemId(Long tallyId);

  Long getTallyById(Long tallyId);

  TYardTallyPO getById(Long tallyId);

  List<Map<String, Object>> getWorkType(Long trustId);

  @Edit
  void updateWeight(TYardTallyPO tYardTallyPO);
  @Edit
  int updateWeightNew(TYardTallyPO tYardTallyPO);

  int updateWeightRevoke(TYardTallyPO tYardTallyPO);

  List<Long> getIsStackPosition(TYardTallyItemPO po);

  Map<String,Object> getIsZq(String processCode);

  List<Map<String, Object>> getTrustId(@Param("cargoInfoId") Long cargoInfoId, @Param("type") List<String> type);

  String getPlanCodeZ(TPrdDispatchSecondarySearchDTO tPrdDispatchSecondarySearchDTO);

  String getPlanNoNew(TPrdDispatchSecondarySearchDTO tPrdDispatchSecondarySearchDTO);

  List<Map<String,Object>>  getTrustInfoNo(@Param("trustId") String trustId, @Param("type") List<String> type);

  //    Page<AppTallyCoilNumDTO> getNumber(AppTallyCoilNumDTO appTallyCoilNumDTO);
  List<AppTallyCoilNumDTO> getNumber(AppTallyCoilNumDTO appTallyCoilNumDTO);
  List<AppTallyCoilNumDTO> getBoxList(AppTallyCoilNumDTO appTallyCoilNumDTO);

  @Edit
  void updateCoilList(List<AppTallyCoilNumDTO> list);

  void insertCoilList(List<AppTallyCoilNumDTO> list);

  TYardTallyPO getTallyInfoById(TallyRecordSearchDTO tallyRecordSearchDTO);
  Integer getTicketById(Long id);
  TYardTallyPO getTallyInfoByIdNew(TallyRecordSearchDTO tallyRecordSearchDTO);

  TYardTallyPO getTallyNew(TallyRecordSearchDTO tallyRecordSearchDTO);

  @Edit
  void updateTally(TYardTallyPO tYardTallyPO);

  @Edit
  void updateTallyItem(TYardTallyItemPO po);

  void updateSourceOrTargetFlag(@Param("flag") String flag,@Param("id") Long id);

  String getPlanCodeNew(String trustId);

  List<Map<String, Object>> getCarFerNew(String planCode);

  List<Map<String, Object>> getDept(Long planId);

  List<Map<String, Object>> getHatch(Long id);
  List<TallyCargoDTO> getCargoStatistics(@Param("ids") List<Long> ids);

  Integer getIsDept(Long id);

  Map<String, Object> getProcess(String processCode);

  Integer getIsPre(String processCode);

  Map<String, Object> getTrustPlan(@Param("trustId") Long trustId);

  List<Map<String, Object>> getCargoInfoList(@Param("trustId") Long trustId, @Param("cargoInfoNo") String cargoInfoNo);

  List<Map<String, Object>> getCargoInfoListNew(@Param("trustId") Long trustId, @Param("cargoInfoNo") String cargoInfoNo);

  List<Map<String, Object>> getWorkInfoByCargoInfoNo(@Param("cargoInfoNo") String cargoInfoNo);

  List<Map<String, Object>> getCabinNoList(@Param("trustId") Long trustId);

  Map<String, Object> getTallyByPlanId(@Param("planId") Long planId);

  List<Map<String, Object>> getShipName(Long trustId);

  Map<String, Object> getTransportEquipmentCargoInfo(@Param("trustId") Long trustId, @Param("vehicleNo") String vehicleNo);

  Map<String, Object> getTransportEquipmentCargoInfoZ(@Param("trustId") Long trustId, @Param("vehicleNo") String vehicleNo);

  List<AppTallyCoilNumDTO> getCoilNumIdList(String tallyId);

  String getCoilMaxStatus(String tallyId);

  @Edit
  void updateCoilNum(@Param("list") List<AppTallyCoilNumDTO> numDTOList,@Param("status") String status);

  void deleteCoilNum(String tallyId);

  List<DepartureDTO> getDepartureList(String truckPlate);

  TYardTallyPO getWeightInfo(TYardTallyPO tYardTallyPO);

  String getIsLogout(Long cargoInfoId);


  List<Long> getNoteTally(Long noteId);

  List<Map<String, Object>> getCargoDeparture(String planNo);

  @DS("simeauto")
  List<DepartureDTO> getNote(String carNo);

  void updateWeightInfo(DepartureDTO departureDTO);

  @Edit
  void insertEmptyTruck(DepartureDTO departureDTO);

  String getWeightStatusNew(String id);

  void updateWeightStatusNew(String id);

  void tallyDepartureSub(TYardTallyPO pos);

  void updateDeparture(Long id);

  Map<String, Object> getProgressWorkData(@Param("cargoInfoId") Long cargoInfoId,
                                          @Param("paramList") List<String> paramList,
                                          @Param("workDate") String workDate,
                                          @Param("classCode") String classCode);

  List<MWorkProcessPO> getMWorkProcessList();

  TYardTallyItemPO getStorage(Long id);

  int getIsBdTally(TYardTallyPO tYardTallyPO);

  List<TYardTallyPO> poByCondition(TYardTallyPO tYardTallyPO);

  List<TYardTallyPO> poByPoundId(TYardTallyPO tYardTallyPO);

  int getByWeighbridgeId(TYardTallyPO tYardTallyPO);

  List<DepartureDTO> getDepartureRecordList(@Param("startDate") String startDate, @Param("endDate") String endDate);

  String getWeightId(Long id);

  void updateWeightRemark(@Param("weightId") String weightId, @Param("remark") String remark);

  String getProcessIsFrontier(String processCode);

  List<Map<String,Object>> listDisShipVoyageApp();

  List<AppTallyCoilNumDTO> getCoilTallyList(Long id);

  String getWeightCargo(Long weighbridgeId);

  Integer getAdmin(Long loginUserId);

  Map<String, Object> getProcessDetail(String processCode);

  String getWorkPlanById(Long planId);
  Map<String, String> getStorehouseByMassId(Long id);

  Map<String, String> getProcessByCode(String code);

    Long getIdByNo(String equipmentNo);

    TBusTrustCargoDTO getTonByTrustIdAndCargoInfoId(@Param("trustId") Long trustId, @Param("cargoInfoId") Long cargoInfoId);

    Long getStackIdByStackName(String stackPositionName);



}
