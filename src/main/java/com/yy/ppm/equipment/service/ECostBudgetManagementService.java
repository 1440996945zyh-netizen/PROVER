package com.yy.ppm.equipment.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.ECostBudgetManagementDTO;

/**
 * 预算管理 Service
 */
public interface ECostBudgetManagementService {

    /** 分页查询 */
    Pages<ECostBudgetManagementDTO> list(ECostBudgetManagementDTO searchDTO, PageParameter parameter);

    /** 根据ID查询 */
    ECostBudgetManagementDTO get(Long id);

    /** 新增 */
    void add(ECostBudgetManagementDTO dto);

    /** 修改 */
    void update(ECostBudgetManagementDTO dto);

    /** 删除 */
    void delete(Long id);
}
