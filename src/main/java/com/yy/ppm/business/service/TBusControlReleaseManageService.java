package com.yy.ppm.business.service;

import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.TBusReleaseManageDTO;
import com.yy.ppm.business.bean.dto.TBusReleaseManageSearchDTO;

public interface TBusControlReleaseManageService {

    Pages<TBusReleaseManageDTO> getList(TBusReleaseManageSearchDTO searchDTO);

    boolean release(TBusReleaseManageDTO tBusReleaseManageDTO);

    boolean revokeRelease(Long id);

    TBusReleaseManageDTO getDetail(Long id);
}
