package com.yy.ppm.statement.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.appWork.bean.po.TYardTallyItemPO;
import com.yy.ppm.business.bean.dto.TBusHandoverListDTO;
import com.yy.ppm.business.bean.po.TBusCargoInfoPO;
import com.yy.ppm.business.bean.po.TBusTrustPO;
import com.yy.ppm.statement.bean.dto.busHandoverlist.*;
import com.yy.ppm.statement.bean.dto.storageSettle.VWeightInfo;
import com.yy.ppm.statement.bean.po.TBusHandoverlistPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-07 10:59
 */
@Component
public interface TBusHandoverlistUnloadMapper {

    Page<TDisShipvoyageItemDTO> listDisShipvoyageItem(TDisShipvoyageItemQueryDTO query);

    Page<TBusTrustDTO> listTrust(TBusTrustQueryDTO query);

    List<TBusHandoverlistDTO> listBusHandoverlist(@Param("shipvoyageItemId") Long shipvoyageItemId, @Param("trustId") Long trustId);

    BigDecimal listBusHandoverlistSumTon(@Param("shipvoyageItemId") Long shipvoyageItemId);

    List<TBusCargoInfoDTO> listBusCargoInfo(@Param("shipvoyageItemId") Long shipvoyageItemId, @Param("trustId") Long trustId);

    List<TBusCargoInfoDTO> getBusCargoInfos(List<Long> shipvoyageItemIds, @Param("trustId") Long trustId);

//    List<TBusCargoInfoDTO> listCargoInfoByCondition(SummarizingAnalyseSearchDTO searchDTO);

    int deleteFileBusiness(Long shipvoyageItemId);

    int insertFileBusiness(@Param("files") List<Map<String, Long>> files);

    int deleteBusHandoverlist(Long id);

    @Edit
    int insertBusHandoverlist(@Param("busHandoverlists") List<TBusHandoverlistPO> busHandoverlists);

    Map<String, Object> getTallyMeasure(@Param("cargoInfoId") Long cargoInfoId,@Param("shipvoyageItemId") Long shipvoyageItemId);

	List<TBusHandoverlistPO> getBusHandoverlist(@Param("shipvoyageItemId") Long shipvoyageItemId, @Param("trustId") Long trustId);

	List<TBusHandoverlistPO> listByShipVoyageItemIds(@Param("shipVoyageItemIds") List<Long> shipVoyageItemIds);

	Integer checkCostStatementCount(@Param("id") Long id);

    @Edit
    int updateHandoverListById(@Param("busHandoverlists") List<TBusHandoverlistPO> busHandoverlists);

    TBusHandoverlistPO getBusHandoverListById(@Param("id") Long id);

    /**
     * 根据船名航次子表id 获取交接清单信息
     * @param shipId
     * @return
     */
    List<TBusHandoverlistPO> getBusHandoverListByShipId(Long shipId);

    BigDecimal getListTon(TDisShipvoyageItemQueryDTO query);

    Integer getTrustTypeByCargoInfoNo(String cargoInfoNo);

    List<Long> getFileIdByShipvoyageItemId(Long shipvoyageItemId);

    VWeightInfo getJGWeightTonByCargoInfo(@Param("cargoInfoId") Long id);
    VWeightInfo getSGWeightTonByCargoInfo(@Param("cargoInfoId") Long id);

    List<TBusHandoverlistPO> getBusHandoverlistByTrustCargoId(@Param("trustCargoId") Long trustCargoId);

    Long getTrustCargoId(@Param("cargoInfoId") Long id,@Param("trustId") Long trustId);
    @Edit
    void addCargoInfos(List<TBusCargoInfoPO> addCargoinfos);

    void deleteBusHandoverlistByIds(List<Long> ids);

    void deleteCargoInfoByIds(List<Long> ids);
    @Edit
    void updateCargoInfos(List<TBusCargoInfoPO> updateCargoinfos);

    //判断通知单是否发布
    List<TBusTrustPO> get30TrustByCargoInfoId(Long cargoInfoId);
    //判断是否发布了通知单
    List<TBusTrustPO> getTrustByCargoInfoId(Long cargoInfoId);

    TBusCargoInfoPO getCargoInfoById(Long id);

    List<TBusHandoverListDTO> getHandoverListByTrustId(@Param("trustId") Long id);

    @Edit
    void addCargoInfoSingle(TBusCargoInfoPO cargoInfoPO);

    List<Long> checkHaveTrust(@Param("voyageId") Long voyageId);

    List<TYardTallyItemPO> getTallyInfo(@Param("shipvoyageItemId") Long shipvoyageItemId,@Param("cargoInfoIds") List<Long> collect);

    @Edit
    void updateHandoverTally(@Param("sourceList") List<TBusHandoverlistPO> doHandoverList);

    Integer getDynamicInfo(@Param("id") Long shipvoyageItemId,@Param("dynamicCode") String dynamicCode);
}
