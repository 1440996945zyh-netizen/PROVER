package com.yy.ppm.statement.mapper.storageCalculate;

import com.yy.ppm.dispatch.bean.po.TDisShipvoyageItemPO;
import com.yy.ppm.statement.bean.po.VWeightInfoPO;
import com.yy.ppm.statement.bean.po.TBusHandoverlistPO;

import java.util.List;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-11-27 10:45
 */
public interface StorageCalculateJGMapper {

    List<VWeightInfoPO> listJGWeightInfo(Long cargoInfoId);

    List<TBusHandoverlistPO> listZCHandoverlist(Long cargoInfoId);

    List<TDisShipvoyageItemPO> listShipvoyageItem(Long cargoInfoId);

    List<VWeightInfoPO> listSGWeightInfo(Long cargoInfoId);
}
