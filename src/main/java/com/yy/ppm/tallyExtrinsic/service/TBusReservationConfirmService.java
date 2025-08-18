package com.yy.ppm.tallyExtrinsic.service;


import com.alibaba.fastjson2.JSONObject;
import com.yy.ppm.tallyExtrinsic.bean.dto.DbCargoInfoDTO;
import com.yy.ppm.tallyExtrinsic.bean.dto.DbTruckDTO;
import com.yy.ppm.tallyExtrinsic.bean.dto.TBusReservationConfirmDTO;
import com.yy.ppm.tallyExtrinsic.bean.dto.TBusReservationConfirmSearchDTO;

import java.util.List;

/**
 * @ClassName 前沿确认
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月05日 09:21:00
 */
public interface TBusReservationConfirmService {


    TBusReservationConfirmDTO getConfirm(TBusReservationConfirmSearchDTO searchDTO);

    /**
     * 删除前沿确认
     * @param id
     */
    int deleteConfirm(Long id);

    List<DbCargoInfoDTO> getPhByShip(String shipVoyageItemId);

    /**
     * 查询前沿确认到达列表
     * @param searchDTO
     * @return
     */
    List<TBusReservationConfirmDTO> getConfirmList(TBusReservationConfirmSearchDTO searchDTO);

    /**
     * 校验吨包件数
     * @param params
     * @return
     */
    boolean checkTallyQuantity(JSONObject params);

    List<DbTruckDTO> getTruckList(String cargoInfoId, String truckNo, String shipVoyageItemId);
}

