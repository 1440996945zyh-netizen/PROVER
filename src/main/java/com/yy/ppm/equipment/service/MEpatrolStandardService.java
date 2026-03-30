package com.yy.ppm.equipment.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.MEpatrolStandardDTO;

/**
 * 巡检标准
 */
public interface MEpatrolStandardService {

    Pages<MEpatrolStandardDTO> getList(MEpatrolStandardDTO searchDTO, PageParameter parameter);

    MEpatrolStandardDTO getById(Long id);

    void add(MEpatrolStandardDTO dto);

    void update(MEpatrolStandardDTO dto);

    void delete(Long id);
}
