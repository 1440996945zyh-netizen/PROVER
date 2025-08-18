package com.yy.ppm.statement.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.TBusCustomerDTO;
import com.yy.ppm.business.bean.dto.TBusRateDTO;
import com.yy.ppm.statement.bean.dto.CostBillDtoSheetTemplate;
import com.yy.ppm.statement.bean.dto.MiscSearchDTO;
import com.yy.ppm.statement.bean.dto.TMiscBillingDTO;
import com.yy.ppm.statement.bean.dto.TMiscBillingExportDTO;
import com.yy.ppm.statement.bean.po.TMiscBillingPO;

import java.util.List;

public interface MiscBillingService {

    List<TBusRateDTO> getRateList(TBusRateDTO tBusRateDTO);

    void saveMiscBilling(TMiscBillingPO po);

    Pages<TMiscBillingDTO> getList(MiscSearchDTO dto);

    int delete(Long id);

    TMiscBillingDTO getMiscById(Long id);

    void publishMisc(Long id);

    void revokeMisc(Long id);

    List<TMiscBillingDTO> getProcessByRateId(String rateId);

    Pages<TMiscBillingDTO> getListForCargo(MiscSearchDTO dto);

    boolean charging(Long id);

    boolean cancleCharging(Long id);

    Pages<TMiscBillingDTO> getlistForInvoiceApply(MiscSearchDTO dto);

    void addInvoiceApply(TMiscBillingPO po);

    void auditInvoiceApply(Long id);

    void removeAuditInvoiceApply(Long id);

    CostBillDtoSheetTemplate printFeeList(List<Long> ids);

    TBusCustomerDTO getCustomerInfo(Long id);

    byte[]  pageExport(MiscSearchDTO dto);
}
