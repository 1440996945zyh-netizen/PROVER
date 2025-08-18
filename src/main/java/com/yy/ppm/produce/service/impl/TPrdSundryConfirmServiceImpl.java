package com.yy.ppm.produce.service.impl;

import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.TBusCargoInfoDTO;
import com.yy.ppm.produce.bean.dto.MWeightRulesDTO;
import com.yy.ppm.produce.bean.dto.TPrdSundryConfirmDTO;
import com.yy.ppm.produce.bean.dto.TPrdSundryConfirmSearchDTO;
import com.yy.ppm.produce.bean.po.TPoundPO;
import com.yy.ppm.produce.mapper.TPrdSundryConfirmMapper;
import com.yy.ppm.produce.service.TPrdSundryConfirmService;
import com.yy.ppm.statement.bean.po.TCostStorageSettlePO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TPrdSundryConfirmServiceImpl implements TPrdSundryConfirmService {

    @Autowired
    private TPrdSundryConfirmMapper tPrdSundryConfirmMapper;
    @Override
    public Pages<TPrdSundryConfirmDTO> getList(TPrdSundryConfirmSearchDTO searchDTO) {

        Pages<TPrdSundryConfirmDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return tPrdSundryConfirmMapper.getList(searchDTO);
        });
        return pages;
    }

    @Override
    public void confirm(Long id) {

        int count = tPrdSundryConfirmMapper.getById(id);
        if (count > 0) {
            throw new BusinessRuntimeException("该磅单已二次过磅");
        }
        TPoundPO po  = new TPoundPO();
        po.setNoteId(id);
        tPrdSundryConfirmMapper.confirm(po);
    }

    @Override
    public void revokeConfirm(Long id) {

        int count = tPrdSundryConfirmMapper.getById(id);
        if (count > 0) {
            throw new BusinessRuntimeException("该磅单已二次过磅");
        }
        TPoundPO po  = new TPoundPO();
        po.setNoteId(id);
        tPrdSundryConfirmMapper.revokeConfirm(po);
    }
}
