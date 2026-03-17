package com.yy.ppm.equipment.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMaintenanceProjectQuotaDTO;

import java.util.List;


/**
 * 维修定额项目 Service
 * 对应表：E_IC_TENANCE_PROJECT_QUOTA
 * 功能：列表查询、详情查询、新增、修改、删除
 */
public interface EMaintenanceProjectQuotaService {

    /**
     * 分页查询
     */
    Pages<EMaintenanceProjectQuotaDTO> list(EMaintenanceProjectQuotaDTO searchDTO, PageParameter parameter);

    /**
     * 根据ID查询
     */
    EMaintenanceProjectQuotaDTO get(Long id);

    /**
     * 新增
     */
    void add(EMaintenanceProjectQuotaDTO dto);

    /**
     * 修改
     */
    void update(EMaintenanceProjectQuotaDTO dto);

    /**
     * 删除
     */
    void delete(Long id);
}
