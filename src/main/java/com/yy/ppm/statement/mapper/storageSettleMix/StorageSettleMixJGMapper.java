package com.yy.ppm.statement.mapper.storageSettleMix;

import com.yy.ppm.statement.bean.dto.storageSettleMix.TDisShipvoyageItemDTO;
import com.yy.ppm.statement.bean.dto.storageSettleMix.VWeightInfo;
import com.yy.ppm.statement.bean.po.TBusHandoverlistPO;
import com.yy.ppm.statement.bean.po.TCostStorageSettlePO;

import java.util.List;

public interface StorageSettleMixJGMapper {

    List<VWeightInfo> listJGWeightInfo(Long cargoInfoId);

    List<TBusHandoverlistPO> listZCHandoverlist(Long cargoInfoId);

    List<TDisShipvoyageItemDTO> listShipvoyageItem(Long cargoInfoId);

    List<VWeightInfo> listSGWeightInfo(Long cargoInfoId);

    List<TCostStorageSettlePO> listStorageSettle(Long cargoInfoId);
}
