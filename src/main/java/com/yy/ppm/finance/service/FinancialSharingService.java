package com.yy.ppm.finance.service;


import com.yy.common.page.Pages;
import com.yy.ppm.finance.bean.dto.TFdInvoiceDTO;
import com.yy.ppm.finance.bean.dto.TFdInvoiceDetailDTO;
import com.yy.ppm.finance.bean.dto.TFdInvoiceSearchDTO;

/**
 * 山东港口财务共享平台-接口对接-应收管理
 */
public interface FinancialSharingService {


    /**
     * 发送计费单 businessType 默认传空
     * @param  id
     * @return
     */
    public void sendBilling(Long id,String isLease,boolean cnDn,String hc,String businessType);



}

