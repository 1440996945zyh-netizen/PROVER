package com.yy.ppm.statement.mapper.storageSettle;

import com.yy.ppm.dispatch.bean.po.TDisShipvoyageItemPO;
import com.yy.ppm.statement.bean.dto.storageSettle.VWeightInfo;

import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-11-24 9:13
 */
public interface StorageSettleXCMapper {

    TDisShipvoyageItemPO getShipvoyageItem(Long handoverlistId);

    List<VWeightInfo> listWeightInfo(Long handoverlistId);

    List<Map<String, Object>> listLoadShipvoyageItem(Long cargoInfoId);
}
