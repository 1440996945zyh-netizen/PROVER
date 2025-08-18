package com.yy.ppm.tallyExtrinsic.mapper;


import com.yy.ppm.tallyExtrinsic.bean.dto.DbCargoInfoDTO;
import com.yy.ppm.tallyExtrinsic.bean.dto.DbTruckDTO;
import com.yy.ppm.tallyExtrinsic.bean.dto.TBusReservationConfirmDTO;
import com.yy.ppm.tallyExtrinsic.bean.dto.TBusReservationConfirmSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName 前沿确认
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2024年06月13日 13:09:00
 */
@Repository
public interface TBusReservationConfirmMapper {

    /**
     * 根据门机编号、状态查询司机确认到达信息
     * @param searchDTO
     * @return
     */
    List<TBusReservationConfirmDTO> getConfirm(TBusReservationConfirmSearchDTO searchDTO);
    List<TBusReservationConfirmDTO> getConfirmList(TBusReservationConfirmSearchDTO searchDTO);
    /**
     * 根据id查询司机确认离开信息
     */
    TBusReservationConfirmDTO getById(TBusReservationConfirmSearchDTO searchDTO);

    int deleteConfirm(@Param("id") Long id);

    List<DbCargoInfoDTO> getPhByShip(String shipVoyageItemId);

    int confirmEnd(Long id);

    List<DbTruckDTO> getTruckList(@Param("cargoInfoId") String cargoInfoId, @Param("truckNo") String truckNo, @Param("shipVoyageItemId") String shipVoyageItemId);
}

