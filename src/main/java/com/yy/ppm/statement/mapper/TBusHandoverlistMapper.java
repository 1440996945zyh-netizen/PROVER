package com.yy.ppm.statement.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
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
public interface TBusHandoverlistMapper {

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
    List<TBusHandoverlistPO> getBusHandoverListByShipId(Long shipId);

    BigDecimal getListTon(TDisShipvoyageItemQueryDTO query);

    Integer getTrustTypeByCargoInfoNo(String cargoInfoNo);

    List<Long> getFileIdByShipvoyageItemId(Long shipvoyageItemId);

    VWeightInfo getJGWeightTonByCargoInfo(@Param("cargoInfoId") Long id);
    VWeightInfo getSGWeightTonByCargoInfo(@Param("cargoInfoId") Long id);

    List<TBusHandoverlistPO> getBusHandoverlistByTrustCargoId(@Param("trustCargoId") Long trustCargoId);

    Long getTrustCargoId(@Param("cargoInfoId") Long id,@Param("trustId") Long trustId);
}
