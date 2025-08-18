package com.yy.ppm.statement.mapper.storageSettleMix;

import com.yy.ppm.dispatch.bean.po.TDisShipvoyageItemPO;
import com.yy.ppm.statement.bean.dto.storageSettleMix.VWeightInfo;
import com.yy.ppm.statement.bean.po.TBusHandoverlistPO;

import java.util.List;
import java.util.Map;

public interface StorageSettleMixXCMapper {

    TDisShipvoyageItemPO getShipvoyageItem(Long cargoInfoId);

    List<TBusHandoverlistPO> listHandoverlist(Long cargoInfoId);

    List<VWeightInfo> listWeightInfo(Long cargoInfoId);

    List<Map<String, Object>> listLoadShipvoyageItem(Long cargoInfoId);
}
