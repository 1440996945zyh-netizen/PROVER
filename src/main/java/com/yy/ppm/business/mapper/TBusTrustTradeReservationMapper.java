package com.yy.ppm.business.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.trustTradeReservation.TBusTrustCargoDTO;
import com.yy.ppm.business.bean.dto.trustTradeReservation.TBusTrustTradeReservationDTO;
import com.yy.ppm.business.bean.dto.trustTradeReservation.TBusTrustTradeReservationQueryDTO;
import com.yy.ppm.business.bean.po.TBusAssignFleetPO;
import com.yy.ppm.business.bean.po.TBusTrustTradeReservatCarPO;
import com.yy.ppm.business.bean.po.TBusTrustTradeReservationPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author linqi
 * @Description
 * @Date 2023-07-05 15:37
 */
public interface TBusTrustTradeReservationMapper {

    Page<TBusTrustCargoDTO> listTrustCargo(@Param("keyword") String keyword, @Param("trustCargoId") Long trustCargoId);

    TBusAssignFleetPO getAssignFleet(@Param("trustCargoId") Long trustCargoId, @Param("customerId") Long customerId);
    List<TBusAssignFleetPO> getAssignFleetList(@Param("trustCargoId") Long trustCargoId, @Param("customerId") Long customerId);

    List<TBusTrustTradeReservationPO> listTrustTradeReservationByTrustCargoIdAndCustomerId(@Param("trustCargoId") Long trustCargoId, @Param("customerId") Long customerId);

    @Edit
    int insertTrustTradeReservation(TBusTrustTradeReservationDTO trustTradeReservation);

    @Edit
    int insertTBusTrustTradeReservatCar(@Param("trustTradeReservatCars") List<TBusTrustTradeReservatCarPO> trustTradeReservatCars);

    @Edit
    int updateTrustTradeReservation(TBusTrustTradeReservationDTO trustTradeReservation);

    void deleteTrustTradeReservation(@Param("trustTradeReservationIds") List<Long> trustTradeReservationIds);

    int deleteTBusTrustTradeReservatCar(@Param("trustTradeReservationIds") List<Long> trustTradeReservationIds);

    int deleteTBusTrustTradeReservatCarById(@Param("id") Long id);

    int deleteTBusTrustTradeReservatCarByCondition(TBusTrustTradeReservatCarPO reservatCarPO);

    int updateTBusTrustTradeReservatCarById(@Param("po") Long id,TBusTrustTradeReservatCarPO po);

    Page<TBusTrustTradeReservationDTO> listTrustTradeReservation(TBusTrustTradeReservationQueryDTO query);

    TBusTrustTradeReservationDTO getByCondition(TBusTrustTradeReservationDTO trustTradeReservation);
    List<TBusTrustTradeReservationDTO> listByCondition(TBusTrustTradeReservationDTO trustTradeReservation);



    Map<String, Object> getAvailableQuantityAndTon(@Param("assignFleetId") Long assignFleetId, @Param("trustTradeReservationId") Long trustTradeReservationId);
}
