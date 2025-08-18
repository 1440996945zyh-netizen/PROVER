package com.yy.ppm.produce.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.produce.bean.dto.portStorage.TPrdPortStorageDTO;
import com.yy.ppm.produce.bean.dto.portStorage.TPrdPortStorageDetailDTO;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-24 13:41
 */
public interface TPrdPortStorageDetailService {

    Pages<TPrdPortStorageDetailDTO> listPortStorageDetail(TPrdPortStorageDetailDTO query, PageParameter parameter);

    Pages<TPrdPortStorageDTO> listPortStorage(TPrdPortStorageDTO query, PageParameter parameter);

    void insertPortStorage(TPrdPortStorageDetailDTO prdPortStorageDetailDTO);

	TPrdPortStorageDTO getPortStorage(TPrdPortStorageDTO query);
}
