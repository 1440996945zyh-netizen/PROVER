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
     * 查询维修定额项目列表（分页）
     */
    Pages<EMaintenanceProjectQuotaDTO> list(EMaintenanceProjectQuotaDTO searchDTO, PageParameter parameter);

    /**
     * 根据主键ID查询详情
     */
    EMaintenanceProjectQuotaDTO get(Long id);

    /**
     * 新增
     */
    int add(EMaintenanceProjectQuotaDTO quota);

    /**
     * 修改（必须包含 id）
     */
    int update(EMaintenanceProjectQuotaDTO quota);

    /**
     * 删除（物理删除）
     */
    int delete(Long id);
}
