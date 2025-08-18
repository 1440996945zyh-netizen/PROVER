package com.yy.ppm.master.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.master.bean.dto.MHqCargoDTO;
import com.yy.ppm.master.bean.dto.MHqCargoSearchDTO;

public interface MHqCargoMapper {

    Page<MHqCargoDTO> getList(MHqCargoSearchDTO searchDTO);

    MHqCargoDTO getById(Long id);
    @Edit
    int insert(MHqCargoDTO mHqCargoDTO);
    @Edit
    int update(MHqCargoDTO mHqCargoDTO);

    int deleteById(Long id);
}
