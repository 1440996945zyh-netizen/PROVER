package com.yy.ppm.business.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.TBusCargoMix.TBusCargoMixRecordDTO;
import com.yy.ppm.business.bean.dto.TBusCargoMix.TBusCargoMixRecordQueryDTO;
import com.yy.ppm.business.bean.dto.TBusCargoMix.TPrdPortStorageDTO;
import com.yy.ppm.business.bean.dto.TBusCargoMix.TPrdPortStorageQueryDTO;
import com.yy.ppm.business.bean.dto.TBusRateDTO;
import com.yy.ppm.business.bean.po.*;
import com.yy.ppm.dispatch.bean.dto.ShipVoyageDto;
import com.yy.ppm.produce.bean.po.TPrdPortStorageDetailPO;
import com.yy.ppm.statement.bean.po.TCostStorageSettlePO;
import com.yy.ppm.statement.bean.po.TMiscBillingPO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface TBusCargoMixMapper {

    /**
     * 查询港存
     *
     * @param query
     * @return
     */
    List<TPrdPortStorageDTO> listPortStorage(TPrdPortStorageQueryDTO query);

    /**
     * 查询合同
     *
     * @param firstInDates
     * @return
     */
    List<Map<String, Object>> contracts(@Param("firstInDates") List<Map<String, Object>> firstInDates);

    /**
     * 新增票货混配记录
     *
     * @param cargoMixRecord
     * @return
     */
    @Edit
    int insertCargoMixRecord(TBusCargoMixRecordPO cargoMixRecord);

    /**
     * 新增票货混配明细
     *
     * @param details
     * @return
     */
    @Edit
    int insertCargoMixDetail(@Param("details") List<TBusCargoMixDetailPO> details);

    /**
     * 查询票货混配记录
     *
     * @param query
     * @return
     */
    Page<TBusCargoMixRecordDTO> listMix(TBusCargoMixRecordQueryDTO query);

    /**
     * 查询混配
     *
     * @param id
     * @return
     */
    TBusCargoMixRecordDTO getMix(Long id);

    int deleteCargoMixRecord(Long id);

    int deleteCargoMixDetail(Long mixRecordId);

    /**
     * ID查票货
     *
     * @param cargoInfoIds
     * @return
     */
    List<TBusCargoInfoPO> listCargoInfo(@Param("cargoInfoIds") List<Long> cargoInfoIds);

    /**
     * ID查第一次进场日期
     *
     * @param cargoInfoIds
     * @return
     */
    List<Map<String, Object>> listFirstInDate(@Param("cargoInfoIds") List<Long> cargoInfoIds);

    /**
     * 货物编码查名称
     *
     * @param cargoCode
     * @return
     */
    String getCargoNameByCode(String cargoCode);

    /**
     * 新增票货
     *
     * @param cargoInfo
     * @return
     */
    @Edit
    int insertCargoInfo(TBusCargoInfoPO cargoInfo);

    /**
     * 修改票货剩余货权量
     *
     * @param cargoInfos
     * @return
     */
    @Edit
    int updateCargoInfoSurplusRightsQuantity(@Param("cargoInfos") List<Map<String, Object>> cargoInfos);

    /**
     * 删除票货
     *
     * @param cargoInfoId
     * @return
     */
    int deleteCargoInfo(Long cargoInfoId);

    List<TPrdPortStorageDetailPO> listPortStorageDetail(@Param("cargoMixRecordIds") List<Long> cargoMixRecordIds);

    int updateMixCargoInfoIdStatus(TBusCargoMixRecordPO cargoMixRecord);

    TCostStorageSettlePO getStorageSettle(Long cargoInfoId);

    List<TBusCargoInfoPO> listTransferCargoInfo(Long parentId);

    List<TBusTrustCargoPO> listTrustCargo(Long cargoInfoId);

    List<TBusCargoMixDetailPO> listCargoMixDetail(Long cargoInfoId);

    String getShipWorkStartTime(@Param("cargoInfoId") String cargolnfold);

    String getJGTimeByCargoInfoId(@Param("cargoInfoId") String cargolnfold);

    ShipVoyageDto getShipVoyageInfo(@Param("id") Long shipvoyageItemId);

    List<TBusRateDTO> getBusRate(@Param("processCd") String procssCdHp);

    @Edit
    void insertMiscBilling(TMiscBillingPO tMiscBillingPO);

    TMiscBillingPO getMisnFee(@Param("id") Long cargoInfoId,@Param("processCd") String processCd);

    void deleteMiscFee(@Param("id") Long id,@Param("cargoInfoId") Long cargoInfoId);

    Map<String,Object> getStorageCargoStackTon(TBusCargoMixDetailPO v1);
}
