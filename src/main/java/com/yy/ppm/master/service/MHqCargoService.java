package com.yy.ppm.master.service;

import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.dto.MHqCargoDTO;
import com.yy.ppm.master.bean.dto.MHqCargoSearchDTO;

public interface MHqCargoService {

    Pages<MHqCargoDTO> getList(MHqCargoSearchDTO searchDTO);

    MHqCargoDTO getDetail(Long id);

    boolean doSave(MHqCargoDTO mHqCargoDTO);

    boolean deleteById(Long id);
}
