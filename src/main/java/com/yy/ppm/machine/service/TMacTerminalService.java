package com.yy.ppm.machine.service;

import java.util.List;
import java.util.Map;

import com.yy.ppm.machine.bean.dto.*;
import com.yy.ppm.machine.bean.po.TMacWorkNowPO;
import java.util.Date;

/**
 * 车载终端基础信息Service 
 * @author zcc
 * @Date 2023/09/06
 */
public interface TMacTerminalService {

	TMacTerminalDTO getMachineByImei(String imei);
	
	Integer getMacDistance();

	List<TPortVehicleNumDTO> queryPortVehicleNum();
	
	List<TPrdWorkPlanDTO> getWorkPlan(String imei, String macCode, String portCode);

	List<TPrdWorkPlanDTO> getDaoYunWorkPlan(String imei, String macCode, String portCode);

	List<TMacTerminalWorkPlanDTO> getDyWorkPlanByCondition(String imei, String macCode, String workPlanId, String carNo, String portCode);

	//装载理货
	List<Map<String,Object>> getDyPlanByCondition(String imei, String workDate, String classCode);

	//获取表单内容
	Map<String,Object> getPickUpFrom(String imei, Long workPlanId);

	Map<String,Object> getPortStorage(String cargoInfoId,String workPlanId);

	Map<String,Object> getPlanLocation(String cargoInfoId,String workPlanId);

	Map<String,Object> getTallyByPlanId(String workDate,String classCode,String trustId,String imei);

	Boolean tallyListDelete(String tallyId);

	List<TMacTerminalWorkPlanDTO> getWorkPlanByCondition(String imei, String macCode, String workPlanId, String carNo, String portCode);

	List<TMacTerminalWorkPlanDTO> getWorkPlanByConditionForAppPC(String carNo, String workAreaCd, String portCode);

	List<TMacTerminalStackPositionDTO> getStackPositionList();

	List<TMacTerminalStackPositionDTO> getStackPositionByWorkPlanId(Long workPlanId, Long cargoInfoId);

	int macWorkStart(TMacWorkTimeDTO tMacWorkTimeDTO);

	int macWorkEnd(TMacWorkTimeDTO tMacWorkTimeDTO);

	int macWorkPC(TMacWorkTimeDTO tMacWorkTimeDTO);

	int macWorkApp(TMacWorkTimeDTO tMacWorkTimeDTO);

	List<Map<String,Object>> getYardByName(List<String> storageYardNms);

	TMacTerminalLoadCarDTO getLoadCarByWeighbridgeId(Long weighbridgeId);

	List<TCarInHarborForAppDTO> getCarInHarbor(TCarInHarborForAppDTO searchDTO);

	TMacWorkNowPO getMacWorkNow(TMacWorkNowDTO searchDTO);

	int insertMacWorkNow(TMacWorkNowDTO paramDTO);

	boolean doSave(TMacWorKLocationDTO tMacWorKLocationDTO);

	TMacWorKLocationDTO getDetailLocation(Long weighbridgeId);

	boolean cancelTally(Long weighbridgeId);
}
