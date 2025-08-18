package com.yy.ppm.business.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.trustTradeReservation.TBusTrustCargoDTO;
import com.yy.ppm.business.bean.dto.trustTradeReservation.TBusTrustTradeReservationDTO;
import com.yy.ppm.business.bean.dto.trustTradeReservation.TBusTrustTradeReservationQueryDTO;
import com.yy.ppm.business.bean.po.TBusTrustTradeReservatCarPO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @Author linqi
 * @Description
 * @Date 2023-07-05 15:37
 */
public interface TBusTrustTradeReservationService {

    List<TBusTrustCargoDTO> listTrustCargo(String keyword, Long trustCargoId, PageParameter parameter);

    void insertTrustTradeReservation(TBusTrustTradeReservationDTO trustTradeReservation);
    void insertTrustTradeReservationCar(TBusTrustTradeReservationDTO trustTradeReservation);

    void updateTrustTradeReservation(TBusTrustTradeReservationDTO trustTradeReservation);

    void deleteTrustTradeReservation(List<Long> trustTradeReservationIds);

    Pages<TBusTrustTradeReservationDTO> listTrustTradeReservation(TBusTrustTradeReservationQueryDTO query, PageParameter parameter);

    List<TBusTrustTradeReservatCarPO> parseCars(MultipartFile file);

    Map<String, Object> getAvailableQuantityAndTon(Long assignFleetId, Long trustTradeReservationId);
}
