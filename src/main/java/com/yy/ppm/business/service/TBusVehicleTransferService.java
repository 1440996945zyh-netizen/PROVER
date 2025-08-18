package com.yy.ppm.business.service;

import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.TBusTrustCargoDTO;
import com.yy.ppm.business.bean.dto.TBusTrustCargoQueryDTO;
import com.yy.ppm.business.bean.dto.TBusVehicleTransferDTO;
import com.yy.ppm.statement.bean.dto.TMiscBillingDTO;

import java.util.List;
import java.util.Map;

public interface TBusVehicleTransferService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    public Pages<TBusTrustCargoQueryDTO> getList(TBusTrustCargoQueryDTO searchDTO);
    
    /**
     * 
     * @param trustId
     * @return
     */
	public List<Map<String, Object>> getTrustCagroDispatchSecondary(Long trustId, Long trustCargoId);
	
	/**
	 * 新增倒运设备信息
	 * @param tBusVehicleTransferDTO
	 */
	public void insertVehicleTransferList(TBusVehicleTransferDTO tBusVehicleTransferDTO);
	
	/**
	 * 查询过磅数据
	 * @param trustCargoId
	 * @return
	 */
	public List<TBusVehicleTransferDTO> getVehicleTransferList(Long trustCargoId);

	void updateInnertransportType(Long id, String innertransportType);

	int changeStatus(TBusTrustCargoDTO tBusTrustCargoDTO);

	List<TBusVehicleTransferDTO> getVehicleList(Long trustId, Long trustCargoId);

	int changeDetailStatus(TBusVehicleTransferDTO tBusVehicleTransferDTO);

	boolean doSave(TBusTrustCargoDTO tBusTrustCargoDTO);

	TBusTrustCargoDTO getEmptyHeavy(String cargoCode);

	TBusTrustCargoDTO getTrustCargoById(Long id);

	List<TMiscBillingDTO> getMiscFeeList(Long trustCargoId);
}