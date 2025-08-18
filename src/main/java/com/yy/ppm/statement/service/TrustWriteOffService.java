package com.yy.ppm.statement.service;

import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.TBusTrustDTO;
import com.yy.ppm.business.bean.dto.TBusTrustSearchDTO;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-10-11 13:49
 */
public interface TrustWriteOffService {

    public Pages<TBusTrustDTO> getWriteOffList(TBusTrustSearchDTO searchDTO);

    Map<String, Object> getWriteOffById(Long trustId);

    void writeOff(Long trustId, Integer checkNumber, BigDecimal checkTon);

    void cancelWriteOff(Long trustId);
}
