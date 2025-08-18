package com.yy.ppm.dispatch.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.dispatch.bean.dto.disShipDynamic.TDisShipDynamicDTO;
import com.yy.ppm.dispatch.bean.dto.disShipDynamic.TDisShipvoyageDTO;
import com.yy.ppm.dispatch.bean.dto.disShipDynamic.TDisShipvoyageQueryDTO;
import com.yy.ppm.dispatch.bean.po.*;
import com.yy.ppm.produce.bean.dto.workTicket.TPrdWorkTiTckInfoDTO;
import com.yy.ppm.statement.bean.dto.busHandoverlist.TDisShipvoyageItemDTO;
import com.yy.ppm.statement.bean.po.TBusHandoverlistPO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author linqi
 * @Description
 * @Date 2023-07-12 11:06
 */
public interface TDisShipDynamicMapper {

    Page<TDisShipvoyageDTO> listDisShipVoyage(TDisShipvoyageQueryDTO query);
    Page<TDisShipvoyageDTO> listDisShipVoyageOrderByBerthId(TDisShipvoyageQueryDTO query);
    Page<TDisShipvoyageDTO> listDisShipVoyageOrderByLeabePortTime(TDisShipvoyageQueryDTO query);

    Page<TDisShipvoyageDTO> listDisShipVoyageApp(TDisShipvoyageQueryDTO query);

    TDisShipvoyageDTO getDisShipVoyage(Long id);

    List<TDisShipDynamicDTO> listDisShipDynamic(TDisShipDynamicPO disShipDynamic);

    List<TDisShipDynamicDTO> listByShipVoyageIds(@Param("ids") List<Long> ids);

    @Edit
    int insertDisShipDynamic(TDisShipDynamicDTO shipDynamic);

    @Edit
    int updateDisShipVoyage(TDisShipvoyagePO disShipvoyage);
    @Edit
    int updateLeavePortTime(TDisShipvoyagePO disShipvoyage);

    @Edit
    int updateDisShipVoyageItem(TDisShipvoyageItemPO disShipvoyageItem);

    @Edit
    int insertDisTugServiceRecord(@Param("tugs") List<TDisTugServiceRecordPO> tugs);

    List<TDisShipvoyageItemPO> listDisShipVoyageItem(Long shipvoyageId);

    int deleteDisShipDynamic(Long id);

    @Edit
    int updateDisShipVoyageAllowNull(TDisShipvoyagePO disShipvoyage);

    @Edit
    int updateDisShipVoyageItemAllowNull(TDisShipvoyageItemPO disShipvoyageItem);

    int deleteDisTugServiceRecord(Long shipDynamicId);
    List<TDisTugServiceRecordPO> selectDisTugServiceRecord(Long shipDynamicId);

    List<TDisLowerCabinPO> queryAll(TDisLowerCabinPO tDisLowerCabinPO);

    List<TDisLowerCabinPO> queryAllDoor(TDisLowerCabinPO tDisLowerCabinPO);

    List<Map<String, Object>> getListDevice(@Param("equipmentTypeId") Long equipmentTypeId,@Param("macName") String macName);

    @Edit
    int insert(TDisLowerCabinPO tDisLowerCabinPO);

    int deleteById(Long id);

    TDisShipDynamicPO getLastTigongDateTime(@Param("shipvoyageId") Long shipvoyageId, @Param("dynamicTypeCode") String dynamicTypeCode);
    @Edit
    int updateDynamic(TDisShipDynamicPO dto);

    @Edit
    int updateSjsbStatusDynamic(TDisShipDynamicPO dto);

    TDisShipDynamicDTO getById(Long id);

    List<TDisShipDynamicDTO> getByShipvoyageId(Long shipvoyageId);

    List<TDisShipDynamicDTO> getByShipVoyageItemId(Long shipVoyageItemId);
    List<TDisShipDynamicDTO> getByShipVoyageItemIds(List<Long> shipVoyageItemIds);

    @Edit
    void updateJxXc(TDisLowerCabinPO tDisLowerCabinPO);

    Map<String, Object> getStatus(Long id);
    
	TDisShipvoyageItemDTO getDisShipVoyageItem(@Param("shipvoyageItemId") Long shipvoyageItemId);


    @Edit
    void insertDoor(TDisLowerCabinPO tDisLowerCabinPO);

    void deleteDoor(List<Long> ids);

    String getDressEvil(Long shipvoyageId);

    String getEvilTime(Long shipvoyageId);

    String getIsEvil(Long shipvoyageItemId);

    Integer getIsKb(Long shipvoyageItemId);

    int getStopCost(Long shipvoyageItemId);

    Cursor<TDisLowerCabinExportPO> exportExcel(TDisLowerCabinPO tDisLowerCabinPO);

    List<TDisShipDynamicPO> getDynamicListByShipvoyageId(Long shipvoyageId);

    TDisShipDynamicPO getDynamicById(Long id);

    List<Map<String,String>> getDynamicByCode(@Param("id") Long id);

    List<TDisShipvoyageItemPO> getShipVoyageItemIdById(Long shipVoyageId);

    int getHandoverList(@Param("shipvoyageItemId") Long id);

    List<String> getTrustRemark(@Param("id") Long shipvoyageItemId);

    @Edit
    void updateDoor(TDisLowerCabinPO tDisLowerCabinPO);

    void delByIds(@Param("ids") List<Long> collect);
}


