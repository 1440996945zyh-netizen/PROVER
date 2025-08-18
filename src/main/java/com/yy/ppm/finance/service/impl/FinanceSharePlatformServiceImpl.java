package com.yy.ppm.finance.service.impl;

import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.finance.bean.dto.FinacialSharing.FinaceSharePlatformResDTO;
import com.yy.ppm.finance.bean.dto.FinacialSharing.FinaceSharePlatformSearchDTO;
import com.yy.ppm.finance.mapper.FinaceSharePlatformMapper;
import com.yy.ppm.finance.service.FinanceSharePlatformService;
import com.yy.ppm.finance.service.FinancialSharingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Service
public class FinanceSharePlatformServiceImpl implements FinanceSharePlatformService {
    @Resource
    FinaceSharePlatformMapper mapper;
    @Autowired
    FinancialSharingService financialSharingService;

    private final List<String> FEE_INVOICE_BUSINESS_TYPE = Arrays.asList("zxJfdByInvoice","zlJfdByInvoice","zxJfdByCnDnInvoice","zlJfdByCnDnInvoice");
    private final List<String> INVOICE_BUSINESS_TYPE = Arrays.asList("FPKJ","FPZF","FPHC");


    @Override
    public Pages<FinaceSharePlatformResDTO> getList(FinaceSharePlatformSearchDTO searchDTO) {
        return PageHelperUtils.limit(searchDTO, () -> {
            return mapper.getList(searchDTO);
        });
    }
}
