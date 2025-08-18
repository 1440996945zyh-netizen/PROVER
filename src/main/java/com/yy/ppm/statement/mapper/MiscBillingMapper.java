package com.yy.ppm.statement.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.TBusCustomerDTO;
import com.yy.ppm.business.bean.dto.TBusRateDTO;
import com.yy.ppm.dispatch.bean.dto.ShipVoyageDto;
import com.yy.ppm.statement.bean.dto.MiscSearchDTO;
import com.yy.ppm.statement.bean.dto.TMiscBillingDTO;
import com.yy.ppm.statement.bean.dto.TMiscBillingExportDTO;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementDTO;
import com.yy.ppm.statement.bean.po.TCostStatementDetailPO;
import com.yy.ppm.statement.bean.po.TCostStatementPO;
import com.yy.ppm.statement.bean.po.TMiscBillingPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther yangcl
 * @Description
 * @Date 2023-09-07 10:59
 */
public interface MiscBillingMapper {

   List<TBusRateDTO> getRateList(TBusRateDTO tBusRateDTO);

   @Edit
   int addMiscBilling(TMiscBillingPO po);
   @Edit
   int updateByPrimaryKey(TMiscBillingPO po);

   Page<TMiscBillingDTO> getList(MiscSearchDTO dto);

   TMiscBillingDTO getMiscBillingById(@Param("id")Long id);

   int deleteMisc(@Param("id")Long id);

   @Edit
   int updateStatus(TMiscBillingDTO dto);

   @Edit
   int insertCostStatement(TCostStatementPO po);

   @Edit
   int insertCostStatementDetail(TCostStatementDetailPO po);

   void deleteCostStatement(@Param("id")Long statementId);

   void deleteCostStatementDetail(@Param("id")Long statementId);

   /**
    *
    * 加水接电打印账单专用
    * @param shipvoyageId
    * @param shipvoyageItemId
    * @param customerId
    * @return
    */
   List<TCostStatementPO> getCostStatementList(@Param("shipvoyageId")Long shipvoyageId,@Param("shipvoyageItemId")Long shipvoyageItemId,@Param("customerId")Long customerId);

   List<TMiscBillingDTO> getProcessByRateId(@Param("rateId")String rateId);

   Page<TMiscBillingDTO> getListForCargo(MiscSearchDTO dto);

   TCostStatementDTO getCostStatementById(@Param("statementId") Long statementId);

   List<TMiscBillingDTO> getListByStatementIdAndRate(@Param("otherStatementId") Long statementId,
                                                     @Param("processCode") String processCode);
   @Edit
   int chargingUpdate(TMiscBillingPO dataPo);

   Page<TMiscBillingDTO> getlistForInvoiceApply(MiscSearchDTO dto);

   List<TMiscBillingDTO> getMiscFeeListByIds(@Param("list") List<Long> ids);

   List<String> getShipAllBerthName(Long voyageId);

    TBusCustomerDTO getCustomerInfoByMiscId(@Param("id") Long id);

   List<TMiscBillingExportDTO> pageExportList(MiscSearchDTO dto);
}
