package com.yy.ppm.finance.service;

import com.yy.common.page.Pages;
import com.yy.ppm.finance.bean.dto.FinacialSharing.FinaceSharePlatformResDTO;
import com.yy.ppm.finance.bean.dto.FinacialSharing.FinaceSharePlatformSearchDTO;

public interface FinanceSharePlatformService {
    Pages<FinaceSharePlatformResDTO> getList(FinaceSharePlatformSearchDTO searchDTO);


}
