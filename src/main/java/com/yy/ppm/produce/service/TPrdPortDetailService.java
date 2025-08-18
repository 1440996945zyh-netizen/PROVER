package com.yy.ppm.produce.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.produce.bean.dto.portStorage.*;
import com.yy.ppm.produce.bean.po.TPrdPortStorageDetailPO;

import java.util.List;
import java.util.Map;

public interface TPrdPortDetailService {

    Pages<TPrdPortStorageDTO> listPortStorage(TPrdPortStorageQueryDTO query, PageParameter parameter);

    Map<String, Object> summaryQuantityTon(TPrdPortStorageQueryDTO query);

    byte[] exportPortStorage(TPrdPortStorageQueryDTO query);

}
