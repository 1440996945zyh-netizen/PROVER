package com.yy.ppm.master.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.master.bean.dto.MHqStorageStackDTO;
import com.yy.ppm.master.bean.dto.MHqStorageStackSearchDTO;

public interface MHqStorageStackMapper {

    Page<MHqStorageStackDTO> getList(MHqStorageStackSearchDTO searchDTO);

    MHqStorageStackDTO getById(Long id);
    @Edit
    int insert(MHqStorageStackDTO mHqStorageStackDTO);
    @Edit
    int update(MHqStorageStackDTO mHqStorageStackDTO);

    int deleteById(Long id);
}
