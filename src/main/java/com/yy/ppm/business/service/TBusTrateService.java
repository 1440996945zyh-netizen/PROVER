package com.yy.ppm.business.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.trate.TBusTrateDTO;
import com.yy.ppm.business.bean.dto.trate.TBusTrateQueryDTO;

import java.math.BigDecimal;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-11-08 11:49
 */
public interface TBusTrateService {

    void verifyUnique(TBusTrateDTO trate);

    void verifyUnique(TBusTrateDTO trate, Long ignoreId);

    void insertTrate(TBusTrateDTO trate);

    void deleteTrate(Long id);

    void updateTrate(TBusTrateDTO trate);

    Pages<TBusTrateDTO> listTrate(PageParameter parameter, TBusTrateQueryDTO query);

    void release(Long id);

    void cancelRelease(Long id);

    void updateOriginAccNumber(Long trateItemId, BigDecimal originAccNumber);
}
