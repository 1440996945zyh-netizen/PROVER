package com.yy.ppm.produce.mapper;

import com.github.pagehelper.Page;
import com.yy.ppm.produce.bean.dto.portStorage.TPrdPortStorageDTO;
import com.yy.ppm.produce.bean.dto.portStorage.TPrdPortStorageDetailDTO;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-24 13:43
 */
public interface TPrdPortStorageDetailMapper {

    Page<TPrdPortStorageDetailDTO> listPortStorageDetail(TPrdPortStorageDetailDTO query);

    Page<TPrdPortStorageDTO> listPortStorage(TPrdPortStorageDTO query);

	TPrdPortStorageDTO getPortStorage(TPrdPortStorageDTO query);
}
