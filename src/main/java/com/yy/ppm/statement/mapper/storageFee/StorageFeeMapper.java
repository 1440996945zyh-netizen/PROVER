package com.yy.ppm.statement.mapper.storageFee;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.TBusCustomerDTO;
import com.yy.ppm.business.bean.po.TBusCargoInfoPO;
import com.yy.ppm.business.bean.po.TBusCargoMixRecordPO;
import com.yy.ppm.business.bean.po.TBusContractRatePO;
import com.yy.ppm.dispatch.bean.po.TDisShipvoyageItemPO;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementDetailDTO;
import com.yy.ppm.statement.bean.dto.storageFee.*;
import com.yy.ppm.statement.bean.po.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface StorageFeeMapper {

    /**
     * 票货列表
     *
     * @param query
     * @return
     */
    Page<TBusCargoInfoDTO> listCargoInfo(TBusCargoInfoQueryDTO query);

    List<Map<String,String>> getShipVoyages(List<String> cargoInfoList);
    List<Map<String,String>> getShipVoyagesForExport(List<Long> cargoInfoList);

    /**
     * 查询票货来源
     *
     * @param id
     * @return
     */
    String getCargoInfoSource(Long id);

    /**
     * 票货ID查船舶航次
     *
     * @param cargoInfoId
     * @return
     */
    TDisShipvoyageItemPO getShipvoyageItem(Long cargoInfoId);

    /**
     * 票货ID查集港记录
     *
     * @param cargoInfoId
     * @return
     */
    List<VWeightInfoPO> listJGWeightInfo(Long cargoInfoId);

    /**
     * ID查票货
     *
     * @param id
     * @return
     */
    TBusCargoInfoPO getCargoInfo(Long id);
    /**
     * 获取减免中的截止计费时间
     *      * @param id
     *      * @return
     */
    TBusCargoInfoPO getCargoInfoReduce(Long id);

    /**
     * 票货ID查混配记录
     *
     * @param cargoInfoId
     * @return
     */
    TBusCargoMixRecordPO getCargoMixRecord(Long cargoInfoId);

    /**
     * 票货ID查装船交接清单
     *
     * @param cargoInfoId
     * @return
     */
    List<TBusHandoverlistPO> listZCHandoverlist(Long cargoInfoId);

    /**
     * 票货ID查装船航次
     *
     * @param cargoInfoId
     * @return
     */
    List<TDisShipvoyageItemPO> listZCShipvoyageItem(Long cargoInfoId);

    /**
     * 票货ID查疏港陆销记录
     *
     * @param cargoInfoId
     * @return
     */
    List<VWeightInfoPO> listSGLXWeightInfo(Long cargoInfoId);

    /**
     * 父票货ID查货转出的票货
     *
     * @param parentCargoInfoId
     * @return
     */
    List<TBusCargoInfoPO> listTransferOutCargoInfo(Long parentCargoInfoId);

    /**
     * 票货ID查混配记录
     *
     * @param cargoInfoId
     * @return
     */
    List<Map<String, Object>> listMixOutWeight(Long cargoInfoId);

    /**
     * 票货ID查卸船交接清单
     *
     * @return
     */
    List<TBusHandoverlistPO> listXCHandoverlist(@Param("cargoInfoId") Long cargoInfoId);

    /**
     * 合同列表
     *
     * @param cargoInfoId
     * @param date
     * @return
     */
    List<Map<String, Object>> listContract(@Param("cargoInfoId") Long cargoInfoId, @Param("date") Date date);

    /**
     * 票货ID查堆存费结算明细
     *
     * @param cargoInfoId
     * @return
     */
    List<TCostStorageSettleDetailDTO> listStorageSettleDetail(@Param("cargoInfoId") Long cargoInfoId);

    /**
     * ID查合同费率
     *
     * @param id
     * @return
     */
    TBusContractRatePO getContractRate(Long id);

    /**
     * 票货ID查减免信息
     *
     * @param cargoInfoId
     * @return
     */
    TBusStackFeeReducePO getStackFeeReduce(Long cargoInfoId);

    /**
     * 新增堆存费结算
     *
     * @param storageSettle
     * @return
     */
    @Edit
    int insertStorageSettle(TCostStorageSettleDTO storageSettle);

    /**
     * 新增堆存费结算明细
     *
     * @param details
     * @return
     */
    @Edit
    int insertStorageSettleDetail(@Param("details") List<TCostStorageSettleDetailDTO> details);

    /**
     * 票货ID查堆存费结算列表
     *
     * @param cargoInfoId
     * @return
     */
    List<TCostStorageSettleDTO> listStorageSettle(Long cargoInfoId);

    /**
     * ID查堆存费结算
     *
     * @param id
     * @return
     */
    TCostStorageSettleDTO getStorageSettle(Long id);

    /**
     * ID删除堆存费结算
     *
     * @param id
     * @return
     */
    int deleteStorageSettle(Long id);

    /**
     * 根据结算ID删除堆存费结算明细
     *
     * @param storageSettleId
     * @return
     */
    int deleteStorageSettleDetail(Long storageSettleId);

    /**
     * 新增结算单
     *
     * @param statement
     * @return
     */
    @Edit
    int insertStatement(TCostStatementPO statement);

    /**
     * 新增结算单明细
     *
     * @param detail
     * @return
     */
    @Edit
    int insertStatementDetail(TCostStatementDetailPO detail);

    /**
     * 审核堆存费
     *
     * @param storageSettle
     * @return
     */
    @Edit
    int review(TCostStorageSettlePO storageSettle);

    /**
     * ID查结算单
     *
     * @param id
     * @return
     */
    TCostStatementPO getStatement(Long id);

    /**
     * 销审堆存费
     *
     * @param storageSettle
     * @return
     */
    int cancelReview(TCostStorageSettlePO storageSettle);

    /**
     * ID删除结算单
     *
     * @param id
     * @return
     */
    int deleteCostStatement(Long id);

    /**
     * 根据结算单ID删除结算单明细
     *
     * @param statementId
     * @return
     */
    int deleteCostStatementDetail(Long statementId);

    /**
     * 更新结算单状态
     *
     * @param costStatement
     */
    void updateCostStatement(TCostStatementPO costStatement);

    /**
     * 通过结算单id获取结算单
     *
     * @param tmpId
     * @return
     */
    TCostStatementPO getStatementById(Long tmpId);

    List<String> getShipAllBerthName(Long shipvoyageItemId);

    Page<TBusCargoInfoDTO> listStatementForStackFee(TBusCargoInfoQueryDTO query);

    List<TCostStorageSettleDTO> listStorageSettleById(@Param("storageSettleId") Long storageSettleId);

    String getSettleStatusByCargoInfoId(@Param("cargoInfoId") Long cargoInfoId);

    void updateCargoInfoStatementStatus(@Param("cargoInfoId") Long cargoInfoId,
                                        @Param("statusCode") String settleStatusCode,
                                        @Param("statusName") String settleStatusCodeName);

    List<TCostStatementDetailDTO> getStorageSettleStatementList(List<Long> ids);

    List<TCostStatementDetailDTO> getBGFeeListStatement(List<Long> ids);

    List<TCostStatementDetailDTO> getMISCStatementList(List<Long> ids);

    TBusCustomerDTO getCargoInfoWithCustomerInfo(@Param("id") Long aLong);

    List<TDisShipvoyageItemPO> listZCShipvoyageItemByCargoInfo(Long cargoInfoId);

    TBusCargoInfoDTO getTaxInvoiceCode(@Param("customerId") Long id);

    BigDecimal getHandoverlistTon(@Param("cargoInfoId") Long cargoInfoId);

    List<Map<String, Object>> getMixRecordList(Long cargoInfoId);

    Cursor<TBusCargoInfoExportDTO> pageExport(TBusCargoInfoQueryDTO query);
}
