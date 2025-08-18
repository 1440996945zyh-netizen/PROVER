package com.yy.ppm.business.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.BusTrustDTO;
import com.yy.ppm.business.bean.dto.BusTrustParam;
import com.yy.ppm.business.bean.dto.*;
import com.yy.ppm.business.bean.dto.trust.TrustCargoDTO;
import com.yy.ppm.business.bean.po.*;
import com.yy.ppm.common.bean.dto.ResponsePopupTrustDTO;
import com.yy.ppm.dispatch.bean.po.TBusTrustLocationPO;
import com.yy.ppm.dispatch.bean.po.TDisShipvoyageItemPO;
import com.yy.ppm.finance.bean.dto.TFdBankCustomerPrepaymentDTO;
import com.yy.ppm.finance.bean.po.TFdBankCustomerPrepaymentPO;
import com.yy.ppm.master.bean.po.MTrustTypePO;
import com.yy.ppm.master.bean.po.MWorkProcessPO;
import com.yy.ppm.produce.bean.po.TPrdWaterElectricityPO;
import com.yy.ppm.statement.bean.po.TBusHandoverlistPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @ClassName 作业指令表(TBusTrust)Mapper
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月05日 09:21:00
 */
@Repository
public interface TBusTrustMapper {

/**
  * 获取作业指令表列表
  * @param tBusTrustSearchVo
  * @return
  */
 public Page<TBusTrustDTO> getList(TBusTrustSearchDTO tBusTrustSearchVo);
 public Page<BusTrustDTO> getTrustPlanPage(BusTrustParam busTrustParams);
 public Page<BusTrustDTO> getJSGDayNightPlanPage(BusTrustParam busTrustParams);
 public Page<BusTrustDTO> getJSGPlanPage(BusTrustParam busTrustParams);
 public BusTrustDTO getJSGPlan(BusTrustParam busTrustParams);


 public Page<BusTrustDTO> getTrustPlanPage2(BusTrustParam busTrustParams);
 public Page<BusTrustDTO> getJSGDayNightPlanPage2(BusTrustParam busTrustParams);
 public Page<BusTrustDTO> getJSGPlanPage2(BusTrustParam busTrustParams);
 public Map<String,Object> getPoundSum(BusTrustParam busTrustParams);
 public Map<String,Object> getPoundCount(BusTrustParam busTrustParams);
 public List<Map<String,Object>> getWeightPound(BusTrustParam busTrustParams);


 public Page<TBusTrustDTO> getStorageYardList(TBusTrustSearchDTO tBusTrustSearchVo);

 @Edit
 void reject(TBusTrustDTO dto);

/**
  * 导出作业指令表列表
  * @param tBusTrustSearchDTO
  * @return
  */
 public List<TBusTrustDTO> exportList(TBusTrustSearchDTO tBusTrustSearchDTO);

 /**
  * 根据id获取作业指令表
  * @param id 主键
  * @return
  */
 public TBusTrustDTO getById(Long id);

 public TBusTrustDTO getByTrustNo(String id);

 /**
  * 新增作业指令表
  * @param tBusTrustDTO
  * @return
  */
 @Edit
 public int insert(TBusTrustDTO tBusTrustDTO);

 /**
  * 修改作业指令表
  * @param tBusTrustDTO
  * @return
  */
 @Edit
 public int update(TBusTrustDTO tBusTrustDTO);

 /**
  * 修改作业通知单计划量
  * @param tBusTrustDTO
  * @return
  */
 @Edit
 public int updateQuantityTon(TBusTrustDTO tBusTrustDTO);

 /**
  * 不为null的更新
  * @param tBusTrustDTO
  * @return
  */
 @Edit
 public int updateNotNull(TBusTrustDTO tBusTrustDTO);


 /**
  * 根据id删除作业指令表
  * @param id 主键
  * @return
  */
 public int deleteById(Long id);

 Page<TBusCargoInfoDTO> getPortStorageInfo(@Param("companyId")Long companyId,@Param("tradeType")String tradeType,@Param("cargoAgentId")Long cargoAgentId,@Param("cargoOwnerId")String cargoOwnerId, @Param("isLuxiao") String isLuxiao, @Param("isShugang") String isShugang,@Param("scn") String scn,@Param("shipvoyageItemId") String shipvoyageItemId, @Param("cargoInfoNo") String cargoInfoNo, @Param("businessNo") String businessNo,@Param("trustType") String trustType);

 Page<TBusCargoInfoDTO> getOrderCargoName(@Param("billNo")String billNo,@Param("shipvoyageItemId") String shipvoyageItemId, @Param("cargoInfoNo") String cargoInfoNo);

 List<MWorkProcessPO> listWorkProcess();

 int updateStatus(TBusTrustDTO dto);

 TBusTrustPO getOtherTrust(@Param("workAccompanyingId") Long workAccompanyingId, @Param("currentId") Long currentId);

 List<TBusTrustCargoPO> listTrustCargoByCargoInfoId(@Param("cargoInfoIds") List<Long> cargoInfoIds);

 List<TBusTrustCargoPO> listTrustCargoByCargoInfoId2(@Param("cargoInfoIds") List<Long> cargoInfoIds);

 int deleteCargoInfo(@Param("cargoInfoIds") List<Long> cargoInfoIds);

 int cleanTrustCargo(@Param("trustCargoIds") List<Long> trustCargoIds);

 /*******************************************************************************************/

 TBusTrustPO getTrust(Long id);

 List<TFdBankCustomerPrepaymentDTO> getPrepayment(Long trustId);

 List<TBusTrustCargoPO> listTrustCargo(Long trustId);

 List<TBusCargoInfoDTO> listCargoInfo(@Param("cargoInfoIds") List<Long> cargoInfoIds);

 MWorkProcessPO getWorkProcess(String processCode);

 List<TAgreementInfo> listAgreementInfo(@Param("cargoInfoIds") List<String> cargoInfoIds);

 int insertAgreementInfo(TAgreementInfo agreementInfos);

 int insertTradeplan(@Param("tradeplans") List<TTradeplan> tradeplans);

 int insertInnertransport(@Param("innertransports") List<TInnertransport> innertransports);

 int insertTruckDriver(TTruckDriver truckDriver);

 int insertTruckInfo(TTruckInfo truckInfo);

 List<TTruckInfo> listTruckInfo(@Param("truckNumbers") List<String> truckNumbers);

 List<TTruckDriver> listTruckDriver(@Param("driverNos") List<String> driverNos);

 int insertTruckplan(TTruckplan truckplan);

 int deleteTradePlan(@Param("planCodes") List<String> planCodes);

 int deleteInnertransport(@Param("planCodes") List<String> planCodes);

 List<Map<String, Object>> listPlan(@Param("agreementInfoIds") List<String> agreementInfoIds);

 int deleteAgreementInfo(@Param("agreementInfoIds") List<Long> agreementInfoIds);

 int deleteTruckplan(@Param("planCode") String planCode, @Param("truckNumber") String truckNumber, @Param("driverIDNumber") String driverIDNumber);

 int updateTrustByWorkAccompanyingId(TBusTrustPO trust);

 int updateTrust(TBusTrustPO trust);

 int updateTrustCargo(@Param("trustCargos") List<TrustCargoDTO> trustCargos);

 List<TBusTrustCargoPO> listTrustCargoByWorkAccompanyingId(Long workAccompanyingId);

 List<TTradeplan> listTradeplan(@Param("planCodes") List<String> planCodes);

 List<TInnertransport> listInnertransport(@Param("planCodes") List<String> planCodes);

 List<TBusCustomerPO> listCustomer(@Param("customerIds") List<Long> customerIds);

 List<Map<String, Object>> listContract(@Param("customerId") Long cargoOwnerId, @Param("cargoCode") String cargoCode, @Param("tradeType") String tradeType);

 Map<String, Object> getPreferentialRate(Long contractId, String contractName,String cargoCode);
 List<Map<String, Object>> getPreferentialTieredRate(Long contractId, String contractName,String cargoCode);
 Map<String, Object> getStepAccumulation(Long contractId, String contractName,String cargoCode);

 List<TFdBankCustomerPrepaymentPO> listBankCustomerPrepayment(Long busTrustId);

 List<MTrustTypePO> listTrustType();

 List<Map<String, Object>> listShipvoyageItemByTrustIds(@Param("trustIds") List<Long> trustIds);
 List<TBusTrustDTO> listShipvoyageItemByTrustIdsNew(@Param("trustIds") List<Long> trustIds);

 TTruckplan getTruckplan(@Param("planCode") String planCode, @Param("truckNumber") String truckNumber, @Param("driverIDNumber") String driverIDNumber);

 Map<String, Object> getPlan(String planCode);

 int insertWeightInfo(TWeightInfo weightInfo);

 List<TWeightInfo> listWeightInfo(Long truckplanid);

 int deleteWeightInfo(Integer id);

 void updatePort(TBusTrustDTO tBustTrustDTO);

 List<TBusCargoInfoPO> listCargoInfoByTrustId(Long trustId);

 List<TBusTrustCargoPO> listTrustCargoById(@Param("ids") List<Long> ids);

 int updateAgreementInfo(@Param("contract_item_id") String contract_item_id, @Param("amAmount") BigDecimal amAmount);

 TAgreementInfo getAgreementInfo(@Param("contract_item_id") String contract_item_id);

 int updateTradePlan(@Param("planCode") String planCode, @Param("planAmount") BigDecimal planAmount);

 List<TBusTrustLocationPO> listTrustLocation(Long trustId);

 int updateShipvoyageItem(TDisShipvoyageItemPO shipvoyageItem);

 List<Map<String, Object>> listShipvoyageItemFile(Long id);

 List<ResponsePopupTrustDTO> getIsClearInfoByTrustIds(@Param("list") List<Long> trustIds);

 Integer getVehicleReservationByTrustId(@Param("id") Long id);

 List<TPrdWaterElectricityPO> listWaterElectricity(Long trustId);

 String getVerifyOverflow();

 List<TBusTrustPO> listTrust(@Param("ids") List<Long> ids);

 List<TBusHandoverlistPO> listHandoverlist(@Param("cargoInfoIds") List<Long> cargoInfoIds);

    TBusTrustDTO getTrustCargoInfo(TrustCargoDTO trustCargoDTO);

 @Edit
 int isStopStatus(TBusTrustCargoDTO tBusTrustCargoDTO);

 TBusCargoInfoDTO getisLogoutByCargoInfoId(Long cargoInfoId);

 int getHandoverlistByTrustId(Long trustId);

    void updateCustomerEntruust(TBusCustomerEntrustDTO tBusCustomerEntrustDTO);

 void delTrustInfoFromCustomerEntrustByTrustId(Long id);

 List<TBusCustomerEntrustDTO> getEntrustByTrustId(@Param("trustId") Long id);

 List<TBusEntrustDetailDTO> getEntrustDetailByTrustId(@Param("entrustId") Long id);

 List<TBusTrustCargoPO> getTrustCargoListByTrustId(@Param("trustId") Long trustId);

 List<Long> getDayNightByTbtcIds(@Param("trustCargoIds") List<Long> collect);



 List<Map<String,Object>> getPrePayInfoByTrustId(@Param("trustId") Long id);
 List<Map<String,Object>> getPrePayTypeByTrustId(@Param("cargoInfoIds") List<Long> id);

 List<TBusHandoverListDTO> getHandoverListInfoByTrustId(@Param("trustId") Long id);


 String getPaymentControlSwitch(@Param("paymentControlSwitch") String paymentControlSwitch);

 TBusTrustDTO getDetailAdd(Long id);

 Page<TrustStopLogRes> getStopLogList(TrustStopLogReq searchDTO);

 void insertStopLog(List<TrustStopLogRes> insertList);
}

