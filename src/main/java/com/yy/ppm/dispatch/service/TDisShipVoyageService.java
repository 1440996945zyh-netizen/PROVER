package com.yy.ppm.dispatch.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageDTO;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageQueryDTO;
import com.yy.ppm.dispatch.bean.po.TDisShipvoyagePO;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author linqi
 * @Description
 * @Date 2023-07-04 11:13
 */
public interface TDisShipVoyageService {

    void insertDisShipVoyageForecast(TDisShipvoyageDTO disShipvoyage);

    void insertDisShipVoyage(TDisShipvoyageDTO disShipvoyage);

    String updateDisShipVoyage(TDisShipvoyageDTO disShipvoyage);


    void deleteDisShipvoyage(List<Long> ids);

    Pages<TDisShipvoyageDTO> listDisShipVoyage(TDisShipvoyageQueryDTO query, PageParameter parameter);

    void downShipWorkReport(Long shipVoyageId, HttpServletResponse response);

    void voidDisShipvoyage(List<Long> ids, String delRemark);

    void rejectionDisShipvoyage(List<Long> ids, String rejectionRemark);

    void receiveDisShipvoyage(List<Long> ids);

    Long changeAmount(BigDecimal dwt, String tradeType);

    String getLastArrivalType(Long shipId);
    String getLastArrivalType(Long voyageId,Long shipId);
    void updateShipVoyageItem(TDisShipvoyageDTO disShipvoyage, TDisShipvoyagePO currentDisShipvoyage);

    List<Map<String, Object>> getSecCargoCate();

}
