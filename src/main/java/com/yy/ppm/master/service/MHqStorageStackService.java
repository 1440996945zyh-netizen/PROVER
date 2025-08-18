package com.yy.ppm.master.service;

import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.dto.MHqStorageStackDTO;
import com.yy.ppm.master.bean.dto.MHqStorageStackSearchDTO;

public interface MHqStorageStackService {

    Pages<MHqStorageStackDTO> getList(MHqStorageStackSearchDTO searchDTO);

    MHqStorageStackDTO getDetail(Long id);

    boolean doSave(MHqStorageStackDTO mHqStorageStackDTO);

    boolean deleteById(Long id);
}
