package com.yy.ppm.produce.service;

import com.yy.common.page.Pages;
import com.yy.ppm.produce.bean.dto.TPrdHqInOutwareDTO;
import com.yy.ppm.produce.bean.dto.TPrdHqInOutwareSearchDTO;

public interface TPrdHqInOutwareService {

    Pages<TPrdHqInOutwareDTO> getList(TPrdHqInOutwareSearchDTO searchDTO);

    TPrdHqInOutwareDTO getDetail(Long id);

    boolean doSave(TPrdHqInOutwareDTO tPrdHqInOutwareDTO);

    boolean deleteById(Long id);

    byte[] exportExcel(TPrdHqInOutwareSearchDTO searchDTO);
}
