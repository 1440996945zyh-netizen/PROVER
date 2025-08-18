package com.yy.ppm.appWork.service;

import java.util.List;
import java.util.Map;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.appWork.bean.dto.*;
import com.yy.ppm.appWork.bean.po.THqYardTallyPO;
import com.yy.ppm.appWork.bean.po.TYardTallyItemPO;
import com.yy.ppm.appWork.bean.po.TYardTallyPO;
import com.yy.ppm.dispatch.bean.dto.disShipDynamic.TDisShipvoyageDTO;
import com.yy.ppm.master.bean.dto.MWorkProcessSearchDTO;
import com.yy.ppm.produce.bean.dto.THqDataDTO;
import com.yy.ppm.produce.bean.dto.TPrdDispatchSecondarySearchDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanSearchDTO;

public interface TallyService {

    List<TPrdWorkPlanDTO> getWorkPlan(TPrdWorkPlanSearchDTO searchDTO);

    List<Map<String,Object>> getProcessInfoList(MWorkProcessSearchDTO mWorkProcessSearchDTO);

    List<Map<String, Object>> getMechanics(TPrdDispatchSecondarySearchDTO tPrdDispatchSecondarySearchDTO);

    List<Map<String, Object>> getTransfer(TPrdDispatchSecondarySearchDTO tPrdDispatchSecondarySearchDTO);

    List<Map<String, Object>> getCarFer(TPrdDispatchSecondarySearchDTO tPrdDispatchSecondarySearchDTO);
    List<Map<String, Object>> getCarFerNew(TPrdDispatchSecondarySearchDTO tPrdDispatchSecondarySearchDTO);

    List<Map<String, Object>> getCargoInfoId(String trustId,String planId,String processCode);

    List<Map<String, Object>> getCargoInfoIdTr(String planId);

    void tally(TYardTallyPO tYardTallyPO);
    void tallyNew(TYardTallyPO tYardTallyPO);
    void zzjTally(TYardTallyPO tYardTallyPO);

    void updateTallyInfo(TYardTallyPO tYardTallyPO);
    void updateTallyInfoJhNew(TYardTallyPO tYardTallyPO);

    void tallyChang(TYardTallyPO tYardTallyPO);

    void tallyCheChuan(TYardTallyPO tYardTallyPO);
    void tallyCheChuanNew(TYardTallyPO tYardTallyPO);

    void tallyChangChuan(TYardTallyPO tYardTallyPO);
    void tallyChangChuanNew(TYardTallyPO tYardTallyPO);

    void tallyChangChe(TYardTallyPO tYardTallyPO);
    void tallyChangCheNew(TYardTallyPO tYardTallyPO);

    void tallyDunBao(DunBaoTallyDTO dto);

    List<Map<String, Object>> getStore(Long workPlanId);

    List<Map<String, Object>> getStackPosition(Long id);

    List<TYardTallyPO> getWaitWork(Long trustId);

    Pages<TYardTallyItemPO> getTallyRecord(TallyRecordSearchDTO tallyRecordSearchDTO, PageParameter pageParameter);

    Pages<THqYardTallyPO> getHqTallyRecord(TallyRecordSearchDTO tallyRecordSearchDTO, PageParameter pageParameter);

    List<THqDataDTO> getHqTallyData(TallyRecordSearchDTO tallyRecordSearchDTO, PageParameter pageParameter);

    List<Map<String, Object>> getHqCargoName();

    List<Map<String, Object>> getHqYard();

    TYardTallyPO getTallyInfoById(TallyRecordSearchDTO tallyRecordSearchDTO, PageParameter pageParameter);
    TYardTallyPO getTallyInfoByIdNew(TallyRecordSearchDTO tallyRecordSearchDTO, PageParameter pageParameter);

    TYardTallyPO getTallyNew(TallyRecordSearchDTO tallyRecordSearchDTO, PageParameter pageParameter);

    Map<String, Object> getTallyRecordSum(TallyRecordSearchDTO tallyRecordSearchDTO);
    Map<String, Object> getTallyRecordSumNew(TallyRecordSearchDTO tallyRecordSearchDTO);

    List<AppTallyLadingDTO> getCarDetailedList(TYardMeasureSearchDTO tYardMeasureSearchDTO);
    List<AppTallyLadingDTO> getCarDetailedListNew(TYardMeasureSearchDTO tYardMeasureSearchDTO);

//    Pages<AppTallyCoilNumDTO> getCoilList(AppTallyCoilNumDTO appTallyCoilNumDTO, PageParameter pageParameter);
    List<AppTallyCoilNumDTO> getCoilList(AppTallyCoilNumDTO appTallyCoilNumDTO);
    List<AppTallyCoilNumDTO> getBoxList(AppTallyCoilNumDTO appTallyCoilNumDTO);

    List<DepartureDTO> getDepartureList(String truckPlate);

    String selectBh(String code);

    List<Map<String, Object>>  getDept(Long planId);

    void deleteNotes(TYardTallyPO tYardTallyPO);
    void deleteNotesNew(TYardTallyPO tYardTallyPO);

    List<Map<String, Object>> getHatch(Long id);

    void updateTally(TYardTallyPO tYardTallyPO);
    void updateTallyInfoNew(TYardTallyPO tYardTallyPO);

	Map<String, Object> getWorkProgress(Long trustId, String workDate, String classCode, String cargoInfoNo);
	Map<String, Object> getWorkProgressNew(Long trustId, String workDate, String classCode, String cargoInfoNo,Long workPlanId);

    List<TallyCargoDTO> getCargoStatistics(List<Long> ids);

	Map<String, Object> getTransportEquipmentCargoInfo(Long trustId, String vehicleNo);

	Map<String, Object> getTransportEquipmentCargoInfoZ(Long trustId, String vehicleNo);

    void departureSub(DepartureDTO departureDTO);

    void departureRevoke(DepartureDTO departureDTO);

    List<DepartureDTO> getDepartureRecordList(String startDate, String endDate);

    List<Map<String,Object>>  listDisShipVoyageApp();

    List<AppTallyCoilNumDTO> lookCoil(Long id);
}
