package com.yy.ppm.equipment.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.PatrolStandardDTO;

/**
 * 巡检标准
 */
public interface PatrolStandardService {

    Pages<PatrolStandardDTO> getList(PatrolStandardDTO searchDTO, PageParameter parameter);

    PatrolStandardDTO getById(Long id);

    void add(PatrolStandardDTO dto);

    void update(PatrolStandardDTO dto);

    void delete(Long id);
}
