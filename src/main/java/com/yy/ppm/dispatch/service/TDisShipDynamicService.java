package com.yy.ppm.dispatch.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.dispatch.bean.dto.disShipDynamic.TDisShipDynamicDTO;
import com.yy.ppm.dispatch.bean.dto.disShipDynamic.TDisShipDynamicQueryDTO;
import com.yy.ppm.dispatch.bean.dto.disShipDynamic.TDisShipvoyageDTO;
import com.yy.ppm.dispatch.bean.dto.disShipDynamic.TDisShipvoyageQueryDTO;
import com.yy.ppm.dispatch.bean.po.TDisLowerCabinPO;
import com.yy.ppm.dispatch.bean.po.TDisLowerDoorPO;
import com.yy.ppm.dispatch.bean.po.TDisShipDynamicPO;

import java.util.List;
import java.util.Map;

/**
 * @Author linqi
 * @Description
 * @Date 2023-07-12 11:05
 */
public interface TDisShipDynamicService {

    Pages<TDisShipvoyageDTO> listDisShipVoyage(TDisShipvoyageQueryDTO query, PageParameter parameter);
    Pages<TDisShipvoyageDTO> listDisShipVoyageApp(TDisShipvoyageQueryDTO query, PageParameter parameter);

    void updateDisShipvoyageStatus(TDisShipDynamicDTO disShipDynamic);
    StringBuffer updateDisShipvoyageStatusApp(TDisShipDynamicDTO disShipDynamic);

    void updateTeShuTingBoFei(TDisShipDynamicDTO disShipDynamic);

    List<TDisShipDynamicDTO> listDisShipDynamic(TDisShipDynamicQueryDTO query);

    void deleteDisShipDynamic(Long id);

    List<TDisLowerCabinPO> queryAll(TDisLowerCabinPO tDisLowerCabinPO);

    List<TDisLowerCabinPO> queryAllDoor(TDisLowerCabinPO tDisLowerCabinPO);

    List<Map<String, Object>> getListDevice(Long equipmentTypeId,String macName);

    void insert(TDisLowerCabinPO tDisLowerCabinPO);

    void addDoor(TDisLowerDoorPO tDisLowerDoorPO);

    void updateJxXc(TDisLowerCabinPO tDisLowerCabinPO);

    void deleteById(Long id);

    boolean updateDynamic(TDisShipDynamicPO disShipDynamic);

    TDisShipDynamicDTO getDetail(Long id);

    byte[] exportExcel(Long shipvoyageId);

    String getTrustRemark(TDisShipDynamicQueryDTO query);

}
