package com.yy.ppm.machine.mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.yy.ppm.business.bean.dto.TBusTrustCargoDTO;
import com.yy.ppm.machine.bean.dto.*;
import com.yy.ppm.produce.bean.po.TPrdPortStorageDetailPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.appWork.bean.po.TYardTallyItemPO;
import com.yy.ppm.appWork.bean.po.TYardTallyPO;
import com.yy.ppm.business.bean.po.TBusTrustTradeReservatCarPO;
import com.yy.ppm.machine.bean.po.TMacWorkNowPO;
import com.yy.ppm.machine.bean.po.TMacWorkTimePO;
import com.yy.ppm.produce.bean.po.TPrdDispatchSecondaryPO;


/**
 * 
 * @author zcc
 * @Date 
 */
@Repository
public interface TMacTerminalMapper {

	TMacTerminalDTO getMachineByImei(@Param("imei") String imei);

	Integer getMacDistance();

	List<TPortVehicleNumDTO> queryPortVehicleNum();

	List<TPrdWorkPlanDTO> getWorkPlan(Map<String, Object> paramMap);

	List<Long> getWorkPlanList(Map<String, Object> paramMap);

	List<TMacTerminalWorkPlanDTO> getWorkPlanByCondition(Map<String, Object> paramMap);

	List<TMacTerminalWorkPlanDTO> getWorkPlanByConditionForAppPC(Map<String, Object> paramMap);
	
	List<TCarInHarborForAppDTO> getWorkPlanByConditionCarInHarbor(TCarInHarborForAppDTO searchDTO);
	
	List<TCarInHarborForAppDTO> getWorkPlanByConditionCarInHarborForLeave(TCarInHarborForAppDTO searchDTO);

	List<TMacTerminalStackPositionDTO> getStackPositionList();

	List<TMacTerminalWorkPlanLocationDTO> getTMacTerminalWorkPlanLocation(@Param("workPlanId") Long workPlanId, @Param("cargoInfoId") Long cargoInfoId);

	List<TMacTerminalStackPositionDTO> getStackPositionListByStackId(@Param("itemList") List<TMacTerminalWorkPlanLocationDTO> itemList);

	List<TMacWorkTimePO> getTMacWorkTimeListByConditionStart(Map<String, Object> paramMap);

	Integer getMacStartWork(Map<String, Object> paramMap);

	List<TMacWorkTimePO> getTMacWorkTimeListByConditionEnd(Map<String, Object> paramMap);
	
	@Edit
	int insertTMacWorkTime(TMacWorkTimePO tMacWorkTimePO);

	@Edit
	int updateTMacWorkTime(TMacWorkTimePO tMacWorkTimePO);

	TBusTrustTradeReservatCarPO getTrustTradeReservatCarById(@Param("id") Long id);

	TPrdDispatchSecondaryPO getPrdDispatchSecondaryByIdAndMacId(@Param("id") Long id);

	List<TMacWorkTimePO> checkMacWorkTimeIsEnd(Map<String, Object> paramMap);

	WeightRecordPoundDTO getWeightRecordPound(@Param("noteId") Long noteId);

	void updateWeightRecordPound(@Param("noteId") Long noteId,@Param("isDirection") String isDirection);

	@Edit
	void insertYardTally(TYardTallyPO yardTallyPO);

	@Edit
	void insertYardTallyItem(TYardTallyItemPO yardTallyItemPO);

	List<Map<String,Object>> getYardByName(List<String> storageYardNms);

	Map<String, String> getFirstChildProcess(@Param("workPlanId") Long workPlanId);

	TMacTerminalLoadCarDTO getLoadCarByWeighbridgeId(Long weighbridgeId);

	List<TMacTerminalWorkPlanDTO> getShipWorkPlanList(Map<String, Object> paramMap);

	List<TMacTerminalWorkPlanDTO> getDyWorkPlanList(Map<String, Object> paramMap);

	List<Map<String,Object>> getDyPlanList(String imei, String workDate, String classCode);

	List<Map<String,Object>> getPickUpFrom(String imei, Long workPlanId);

	List<Map<String,Object>> getCargoNameList(Long workPlanId);

	List<Map<String,Object>> getTransportEquipment(Long workPlanId);

	List<Map<String,Object>> getPortStorage(@Param("direction") String direction,@Param("cargoInfoId") String cargoInfoId,@Param("workPlanId") String workPlanId);

	List<Map<String,Object>> getPlanLocation(@Param("direction") String direction,@Param("cargoInfoId") String cargoInfoId,@Param("workPlanId") String workPlanId);

	List<Map<String,Object>> getTallyByPlanId(@Param("workDate") String workDate,@Param("classCode") String classCode,@Param("trustId") String trustId,@Param("imei") String imei);

	Map<String,Object> getTallySum(@Param("workDate") String workDate,@Param("classCode") String classCode,@Param("trustId") String trustId,@Param("imei") String imei);

	int tallyListDelete(@Param("tallyId") String tallyId,@Param("createBy") Long createBy,@Param("createByName") String createByName,@Param("createTime") Date createTime);
	List<Map<String,Object>> getPortStorageList(@Param("tallyId") String tallyId);
	int deleteStorageDetail(@Param("tallyId") String tallyId);
	List<Map<String,Object>> getSumPortStorage(@Param("ids") List<String> ids);
	int updatePortStorage(@Param("id") Object id,@Param("ton") Object ton,@Param("createBy") Long createBy,@Param("createByName") String createByName,@Param("createTime") Date createTime);

	List<String> getBusinessNoList(@Param("trustId") Long trustId);

	List<TMacTerminalWorkPlanDTO> getShipDataWorkPlanList(@Param("loginUserId") Long loginUserId, 
			@Param("businessNoList") List<String> businessNoList,
			@Param("portCode") String portCode,
			@Param("carNo") String carNo);

	List<TMacTerminalWorkPlanDTO> getShipDataWorkPlanListForAppPC(@Param("loginUserId") Long loginUserId, 
			@Param("businessNoList") List<String> businessNoList,
			@Param("portCode") String portCode,
			@Param("carNo") String carNo);

	Integer getTallyCountByWeighbridgeId(@Param("weighbridgeId") Long weighbridgeId);

	TMacWorkNowPO getMacWorkNow(TMacWorkNowDTO searchDTO);

	@Edit
	int insertMacWorkNow(TMacWorkNowDTO paramDTO);

	int doSave(TMacWorKLocationDTO tMacWorKLocationDTO);

	TMacWorKLocationDTO getDetailLocation(Long weighbridgeId);

    BigDecimal getTonByMassIdAndCargoInfoId(Long massId, Long cargoInfoId);

	Long getListByTrustCargoId(Long trustCargoInfoId);

	PoundDTO getOutTimeByNoteId(@Param("noteId") Long weighbridgeId);

	List<TPrdPortStorageDetailPO> getStorageList(Long weighbridgeId);

	void delTallLog(@Param("noteId") Long weighbridgeId);

	void changPoundTallyStatusWithZero(@Param("noteId") Long weighbridgeId);

	String getWorkPlanById(Long planId);
}