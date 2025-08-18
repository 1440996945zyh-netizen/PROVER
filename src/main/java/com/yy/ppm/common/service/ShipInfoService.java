package com.yy.ppm.common.service;

import com.yy.ppm.common.bean.dto.CheckDTO;
import com.yy.ppm.common.bean.dto.TDisPoundInfoDTO;
import com.yy.ppm.common.enums.AutoNumEnum;
import com.yy.ppm.dispatch.bean.dto.TDisCostInfoPO;
import com.yy.ppm.dispatch.bean.dto.disShipDynamic.TDisShipDynamicDTO;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageDTO;
import com.yy.ppm.dispatch.bean.po.TDisLowerCabinPO;
import com.yy.ppm.produce.bean.dto.portStorage.InoutDetailQueryDTO;
import com.yy.ppm.produce.bean.dto.portStorage.TPrdPortStorageDTO;
import com.yy.ppm.produce.bean.dto.portStorage.TPrdPortStorageGbCargoInfoDTO;

import java.util.List;
import java.util.Map;

public interface ShipInfoService {

    public Map<String, Object> getSteps(Long shipVoyageId);

    public TDisShipvoyageDTO getShipVoyageInfo(Long shipVoyageId);

    public Map<String,Object> getShipDynamicInfo(Long shipVoyageId);

    List<TDisLowerCabinPO> getShipDoorInfo(TDisLowerCabinPO tDisLowerCabinPO);
    public List<TPrdPortStorageGbCargoInfoDTO> getPortTrendsInfo(Long shipVoyageId);

    public List<TPrdPortStorageDTO> listPortStorage(String cargoInfoNo);

    public Map<String, Object> getInoutDetail(InoutDetailQueryDTO query);

    List<TDisCostInfoPO> getCostInfo(Long shipVoyageId);

    List<TDisPoundInfoDTO> getPoundInfo(Long shipVoyageId);
}
