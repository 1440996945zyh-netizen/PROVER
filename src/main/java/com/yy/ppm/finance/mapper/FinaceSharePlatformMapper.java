package com.yy.ppm.finance.mapper;

import com.github.pagehelper.Page;
import com.yy.ppm.finance.bean.dto.FinacialSharing.FinaceSharePlatformResDTO;
import com.yy.ppm.finance.bean.dto.FinacialSharing.FinaceSharePlatformSearchDTO;

public interface FinaceSharePlatformMapper {
    Page<FinaceSharePlatformResDTO> getList(FinaceSharePlatformSearchDTO searchDTO);

    FinaceSharePlatformResDTO getById(String operId);
}
