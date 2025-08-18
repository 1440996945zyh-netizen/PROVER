package com.yy.ppm.statement.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.statement.bean.dto.busHandoverlist.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-07 10:58
 */
public interface TBusHandoverlistService {

    Pages<TDisShipvoyageItemDTO> listDisShipvoyageItem(TDisShipvoyageItemQueryDTO query, PageParameter parameter);

    Pages<TBusTrustDTO> listTrust(TBusTrustQueryDTO query, PageParameter parameter);

    List<TBusHandoverlistDTO> listBusHandoverlist(Long shipvoyageItemId, Long trustId);

    List<TBusCargoInfoDTO> listBusCargoInfo(Long shipvoyageItemId, Long trustId);

    StringBuffer updateBusHandoverlist(UpdateBusHandoverlistDTO dto);

    BigDecimal getListTon(TDisShipvoyageItemQueryDTO query);
}
