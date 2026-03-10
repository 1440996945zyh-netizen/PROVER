package com.yy.ppm.equipment.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMaintenanceProjectQuotaDTO;

public interface EMaintenanceProjectQuotaService {

    /** 查询维修定额项目（分页） */
    Pages<EMaintenanceProjectQuotaDTO> getList(EMaintenanceProjectQuotaDTO searchDTO, PageParameter parameter);

    /** 根据ID查询维修定额项目 */
    EMaintenanceProjectQuotaDTO getById(Long id);

    /** 新增/修改维修定额项目 */
    void save(EMaintenanceProjectQuotaDTO dto);

    /** 删除维修定额项目（逻辑删除） */
    void delete(Long id);
}

