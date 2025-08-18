package com.yy.ppm.finance.mapper;

import com.yy.ppm.business.bean.dto.TBusCustomerDTO;
import com.yy.ppm.finance.bean.dto.TFdCreditDebitBillDetailDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShareInvoiceMapper {
    TBusCustomerDTO getCustomerInfoById(@Param("customerId") Long customerId);

    List<TFdCreditDebitBillDetailDTO> getCNDNListByParentId(@Param("cndnId") Long invoiceId);
}
