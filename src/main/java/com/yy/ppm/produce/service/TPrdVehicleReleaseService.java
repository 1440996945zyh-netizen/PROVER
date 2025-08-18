package com.yy.ppm.produce.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.produce.bean.dto.TPrdVehicleReleaseDTO;
import com.yy.ppm.produce.bean.po.TPrdVehicleReleasePO;

public interface TPrdVehicleReleaseService {

    Pages<TPrdVehicleReleasePO> getList(TPrdVehicleReleaseDTO query, PageParameter parameter);

    Pages<TPrdVehicleReleasePO> getSubList(TPrdVehicleReleaseDTO query, PageParameter parameter);

    boolean auditStatusById(Long id);

    boolean auditRevokeStatusById(Long id);
}
