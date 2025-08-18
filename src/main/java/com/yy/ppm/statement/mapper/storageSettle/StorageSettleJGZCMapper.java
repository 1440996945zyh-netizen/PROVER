package com.yy.ppm.statement.mapper.storageSettle;

import com.yy.ppm.dispatch.bean.po.TDisShipvoyageItemPO;
import com.yy.ppm.statement.bean.dto.storageSettle.VWeightInfo;
import com.yy.ppm.statement.bean.po.TCostStorageSettlePO;

import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-11-27 10:45
 */
public interface StorageSettleJGZCMapper {

    List<VWeightInfo> listWeightInfo(Long handoverlistId);

    TDisShipvoyageItemPO getShipvoyageItem(Long handoverlistId);

    List<Map<String, Object>> listShipvoyageItem(Long cargoInfoId);

    List<TCostStorageSettlePO> listStorageSettle(Long cargoInfoId);
}
