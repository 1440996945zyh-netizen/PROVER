package com.yy.ppm.tallyExtrinsic.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.tallyExtrinsic.bean.dto.*;
import com.yy.ppm.tallyExtrinsic.bean.po.TYardTallyItemPO;
import com.yy.ppm.tallyExtrinsic.bean.po.TYardTallyMacPO;
import com.yy.ppm.tallyExtrinsic.bean.po.TYardTallyPO;
import com.yy.ppm.master.bean.dto.MWorkProcessSearchDTO;
import com.yy.ppm.produce.bean.dto.TPrdDispatchSecondarySearchDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanSearchDTO;

import java.util.List;
import java.util.Map;

public interface TallyExtrinsicService {

    List<TPrdWorkPlanDTO> getWorkPlan(TPrdWorkPlanSearchDTO searchDTO);

    List<Map<String,Object>> getProcessInfoList(String processCode);

    List<Map<String, Object>> getMechanics(TPrdDispatchExtrinsicDTO tPrdDispatchSecondarySearchDTO);

    List<Map<String, Object>> getStorageList(TPrdDispatchExtrinsicDTO tPrdDispatchSecondarySearchDTO);

    List<Map<String, Object>> getTransfer(TPrdDispatchExtrinsicDTO tPrdDispatchSecondarySearchDTO);

    List<Map<String, Object>> getCarFer(TPrdDispatchExtrinsicDTO tPrdDispatchSecondarySearchDTO);

    List<Map<String, Object>> getTruckNo(TPrdDispatchExtrinsicDTO tPrdDispatchSecondarySearchDTO);

    List<Map<String, Object>> getCargoInfoId(String trustId,String planId,String processCode);

    List<Map<String, Object>> getCargoInfoIdTr(String planId);

    void tally(TYardTallyPO tYardTallyPO);

    void updateTallyInfo(TYardTallyPO tYardTallyPO);

    void tallyChang(TYardTallyPO tYardTallyPO);

    void tallyCheChuan(TYardTallyPO tYardTallyPO);

    void tallyChangChuan(TYardTallyPO tYardTallyPO);

    void tallyChangChe(TYardTallyPO tYardTallyPO);

    List<Map<String, Object>> getStore(Long workPlanId);

    List<Map<String, Object>> getStackPosition(Long id);

    List<TYardTallyPO> getWaitWork(Long trustId,String macName);

    Pages<TYardTallyItemPO> getTallyRecord(TallyRecordSearchDTO tallyRecordSearchDTO, PageParameter pageParameter);

    TYardTallyPO getTallyInfoById(TallyRecordSearchDTO tallyRecordSearchDTO, PageParameter pageParameter);

    TYardTallyPO getTallyNew(TallyRecordSearchDTO tallyRecordSearchDTO, PageParameter pageParameter);

    Map<String, Object> getTallyRecordSum(TallyRecordSearchDTO tallyRecordSearchDTO);

    List<AppTallyLadingDTO> getCarDetailedList(TYardMeasureSearchDTO tYardMeasureSearchDTO);

//    Pages<AppTallyCoilNumDTO> getCoilList(AppTallyCoilNumDTO appTallyCoilNumDTO, PageParameter pageParameter);
    List<AppTallyCoilNumDTO> getCoilList(AppTallyCoilNumDTO appTallyCoilNumDTO);

    List<DepartureDTO> getDepartureList(String truckPlate);

    String selectBh(String code);

    List<Map<String, Object>>  getDept();

    void deleteNotes(TYardTallyPO tYardTallyPO);

    List<Map<String, Object>> getHatch(Long id);

    void updateTally(TYardTallyPO tYardTallyPO);
    void insertMacTally(TYardTallyMacPO tYardTallyMacPO);

	Map<String, Object> getWorkProgress(Long trustId, String workDate, String classCode, String cargoInfoNo);

    List<TallyCargoDTO> getCargoStatistics(List<Long> ids);

	Map<String, Object> getTransportEquipmentCargoInfo(Long trustId, String vehicleNo);

	Map<String, Object> getTransportEquipmentCargoInfoZ(Long trustId, String vehicleNo);

    void departureSub(DepartureDTO departureDTO);

    void departureRevoke(DepartureDTO departureDTO);

    List<DepartureDTO> getDepartureRecordList(String startDate, String endDate);

    List<Map<String,Object>>  listDisShipVoyageApp();

    void tallyConfirm(TYardTallyPO tYardTallyPO);

    TYardTallyMacPO getNewDoorById(String planId, String macName);
}
