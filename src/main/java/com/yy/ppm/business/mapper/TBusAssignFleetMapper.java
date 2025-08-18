package com.yy.ppm.business.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.assignFleet.TBusTrustCargoDTO;
import com.yy.ppm.business.bean.dto.assignFleet.TBusTrustCargoQueryDTO;
import com.yy.ppm.business.bean.dto.trustTradeReservation.TBusTrustTradeReservationDTO;
import com.yy.ppm.business.bean.po.TBusAssignFleetPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author linqi
 * @Description
 * @Date 2023-07-05 09:32
 */
public interface TBusAssignFleetMapper {

    Page<TBusTrustCargoDTO> listTrustCargo(TBusTrustCargoQueryDTO query);

    TBusTrustCargoDTO getTrustCargo(Long trustCargoId);

    List<TBusAssignFleetPO> listAssignFleet(Long trustCargoId);

    List<TBusTrustTradeReservationDTO> listTrustTradeReservation(@Param("trustCargoId") Long trustCargoId, @Param("customerIds") List<Long> customerIds);

    @Edit
    int insertAssignFleet(@Param("assignFleets") List<TBusAssignFleetPO> assignFleets);

    @Edit
    int updateAssignFleet(TBusAssignFleetPO assignFleet);

    int deleteAssignFleet(@Param("trustCargoId") Long trustCargoId, @Param("customerIds") List<Long> customerIds);
}
