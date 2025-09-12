package com.yy.ppm.master.service;

import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.dto.MDictDataDTO;
import com.yy.ppm.master.bean.dto.MPrintDTO;
import com.yy.ppm.master.bean.dto.MPrintSearchDTO;

import java.util.List;

public interface MPrintService {
    /**
     * 新增、修改字典
     * @param po
     */
    void insert(MPrintDTO po);

     public Pages<MPrintDTO> getList(MPrintSearchDTO mPrintSearchDTO);

    void update(MPrintDTO po);

    void deleteById(Long id);

    MPrintDTO getDetail(Long id);

    List<MPrintDTO> getModelTypeList(MPrintSearchDTO mPrintSearchDTO);
}
