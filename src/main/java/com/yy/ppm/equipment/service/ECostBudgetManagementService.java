package com.yy.ppm.equipment.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.ECostBudgetManagementDTO;

import java.util.List;

/**
 * 预算管理 Service
 *
 * 定义预算管理模块的业务接口
 */
public interface ECostBudgetManagementService {

    /**
     * 分页查询预算管理列表
     *
     * @param searchDTO 查询条件
     * @param parameter 分页参数
     * @return 分页结果
     */
    Pages<ECostBudgetManagementDTO> list(ECostBudgetManagementDTO searchDTO, PageParameter parameter);

    /**
     * 根据主键ID查询详情
     *
     * @param id 主键ID
     * @return 详情数据
     */
    ECostBudgetManagementDTO get(Long id);

    /**
     * 新增预算管理
     *
     * @param dto 请求参数
     */
    void add(ECostBudgetManagementDTO dto);

    /**
     * 修改预算管理
     *
     * @param dto 请求参数
     */
    void update(ECostBudgetManagementDTO dto);

    /**
     * 删除预算管理
     *
     * @param id 主键ID
     */
    void delete(Long id);

    List<ECostBudgetManagementDTO> getWarningUser(ECostBudgetManagementDTO dto);
}
