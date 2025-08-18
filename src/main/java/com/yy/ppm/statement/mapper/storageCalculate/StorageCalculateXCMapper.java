package com.yy.ppm.statement.mapper.storageCalculate;

import com.yy.ppm.dispatch.bean.po.TDisShipvoyageItemPO;
import com.yy.ppm.statement.bean.po.VWeightInfoPO;
import com.yy.ppm.statement.bean.po.TBusHandoverlistPO;

import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-11-24 9:13
 */
public interface StorageCalculateXCMapper {

    TDisShipvoyageItemPO getShipvoyageItem(Long cargoInfoId);

    List<TBusHandoverlistPO> listHandoverlist(Long cargoInfoId);

    List<VWeightInfoPO> listWeightInfo(Long cargoInfoId);

    List<Map<String, Object>> listLoadShipvoyageItem(Long cargoInfoId);
}
