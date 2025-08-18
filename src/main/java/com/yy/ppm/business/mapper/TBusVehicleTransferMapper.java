package com.yy.ppm.business.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.*;
import com.yy.ppm.business.bean.po.TBusVehicleTransferPO;
import com.yy.ppm.machine.bean.dto.WeightRecordPoundDTO;
import com.yy.ppm.statement.bean.dto.TMiscBillingDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TBusVehicleTransferMapper {

	public Page<TBusTrustCargoQueryDTO> getPage(TBusTrustCargoQueryDTO searchDTO);

	public List<Map<String, Object>> getTrustCagroDispatchSecondary(@Param("trustId") Long trustId, @Param("trustCargoId") Long trustCargoId);

	public void deleteVehicleTransferList(@Param("trustId") Long trustId, @Param("trustCargoId") Long trustCargoId);

	@Edit
	public void insertVehicleTransferList(@Param("busVehicleTransferList") List<TBusVehicleTransferPO> busVehicleTransferList);

	@Edit
	public void insertVehicleTransfer(TBusVehicleTransferPO tBusVehicleTransferPO);

	public List<TBusVehicleTransferDTO> getVehicleTransferList(Long trustCargoId);

	public List<Map<String, Object>> getVehicleTransferCountList(@Param("ids") List<Long> ids);

	public List<Map<String, Object>> getVehicleTransferTonsList(@Param("ids") List<Long> ids);

	int 	updateInnertransportType(@Param("id") Long id, @Param("innertransportType") String innertransportType);
	@Edit
	int changeStatus(TBusTrustCargoDTO tBusTrustCargoDTO);

	List<TBusVehicleTransferDTO> getVehicleList(Long trustId, Long trustCargoId);

	int changeDetailStatus(TBusVehicleTransferDTO tBusVehicleTransferDTO);

	void updateVehicleTransfer(TBusVehicleTransferPO tBusVehicleTransferPO);

	void updateDelFlagVehicle(TBusVehicleTransferPO vehicle);

	List<TBusTrustCargoQueryDTO> getAllList();

	int update(TBusTrustCargoDTO tBusTrustCargoDTO);

	TBusTrustCargoDTO getEmptyHeavy(String cargoCode);

	TBusCargoInfoDTO getCargoByTrustCargoId(@Param("id") Long id);

	int updateTrustPort(TBusTrustCargoDTO tBusTrustCargoDTO);


	TBusTrustCargoQueryDTO getListByTrustCargoId(Long trustCargoId);

    TBusTrustDTO getTrustByTrustId(@Param("trustId") Long trustId);

	List<ShipNameVoyageResultDTO> getShipNameVoyage(Long id);


	List<TMiscBillingDTO> getMiscFeeList(@Param("trustCargoId") Long trustCargoId);

	int getIsFinshedByBusinessNoAndTruck(String equipmentNo, String businessNo);

	int getBoundIsFinshedByIdAndNox(String equipmentNo, Long trustCargoId);

	Integer getPoundInfo(TBusVehicleTransferPO vehicle);

}