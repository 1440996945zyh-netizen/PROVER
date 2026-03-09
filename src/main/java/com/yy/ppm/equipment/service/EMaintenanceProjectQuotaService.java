package com.yy.ppm.equipment.service;


import com.yy.ppm.equipment.bean.dto.EMaintenanceProjectQuotaDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 维修定额项目 Service接口
 * 业务逻辑层
 */
public interface EMaintenanceProjectQuotaService {

    /**
     * 查询列表
     */
    List<EMaintenanceProjectQuotaDTO> list();

    /**
     * 根据ID查询
     */
    EMaintenanceProjectQuotaDTO get(Long id);

    /**
     * 新增
     */
    int add(EMaintenanceProjectQuotaDTO quota);

    /**
     * 修改
     */
    int update(EMaintenanceProjectQuotaDTO quota);

    /**
     * 删除
     */
    int delete(Long id);
}