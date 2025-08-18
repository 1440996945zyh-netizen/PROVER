package com.yy.ppm.business.service;

import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.TBusCustomerEntrustDTO;
import com.yy.ppm.business.bean.dto.TBusCustomerEntrustReqDTO;
import com.yy.ppm.business.bean.dto.TBusEntrustDetailDTO;
import com.yy.ppm.business.bean.dto.TBusEntrustDetailReqDTO;

import java.util.List;

public interface BusCustomerEntrustService {
    Pages<TBusCustomerEntrustDTO> getList(TBusCustomerEntrustReqDTO query);

    List<TBusEntrustDetailDTO> getDetailList(TBusEntrustDetailReqDTO query);

    void saveOrUpdate(TBusCustomerEntrustDTO dto);

    Boolean delCustomerEntrust(Long entrustId);

    TBusCustomerEntrustDTO getEntrust(Long entrustId);

    TBusCustomerEntrustDTO getCustomerEntrustForAddTrust(Long entrustId);
}
