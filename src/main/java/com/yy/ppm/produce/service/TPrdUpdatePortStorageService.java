package com.yy.ppm.produce.service;

import com.github.pagehelper.Page;
import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.TPrdUpdatePortStorageReqDTO;
import com.yy.ppm.produce.bean.dto.workTicket.PoundToPortStorageDTO;

import java.util.Map;

public interface TPrdUpdatePortStorageService {


    Pages<PoundToPortStorageDTO> getList(TPrdUpdatePortStorageReqDTO query);

}
