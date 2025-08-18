package com.yy.ppm.business.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.assignFleet.TBusTrustCargoDTO;
import com.yy.ppm.business.bean.dto.assignFleet.TBusTrustCargoQueryDTO;
import com.yy.ppm.business.bean.po.TBusAssignFleetPO;

import java.util.List;

/**
 * @Author linqi
 * @Description
 * @Date 2023-07-05 09:31
 */
public interface TBusAssignFleetService {

    Pages<TBusTrustCargoDTO> listTrustCargo(TBusTrustCargoQueryDTO query, PageParameter parameter);

    void updateAssignFleet(Long trustCargoId, List<TBusAssignFleetPO> assignFleets);

    void insertAssignFleet(Long trustCargoId, List<TBusAssignFleetPO> assignFleets);
}
