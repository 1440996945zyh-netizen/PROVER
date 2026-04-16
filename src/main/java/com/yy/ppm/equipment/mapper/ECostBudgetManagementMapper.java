package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EContractInfoDTO;
import com.yy.ppm.equipment.bean.dto.ECostBudgetManagementDTO;
import com.yy.ppm.equipment.bean.dto.ECostSettlementApplyDTO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 预算管理 Mapper
 *
 * 对应表：E_COST_BUDGET_MANAGEMENT
 */
public interface ECostBudgetManagementMapper {

    /**
     * 查询预算管理列表（分页）
     *
     * @param searchDTO 查询条件
     * @return 分页数据
     */
    Page<ECostBudgetManagementDTO> selectList(ECostBudgetManagementDTO searchDTO);

    /**
     * 根据主键ID查询详情
     *
     * @param id 主键ID
     * @return 详情数据
     */
    ECostBudgetManagementDTO selectById(@Param("id") Long id);

    /**
     * 新增预算管理
     *
     * @param dto 请求参数
     */
    @Edit
    void add(ECostBudgetManagementDTO dto);

    /**
     * 修改预算管理
     *
     * @param dto 请求参数
     */
    @Edit
    void update(ECostBudgetManagementDTO dto);

    /**
     * 删除预算管理
     *
     * @param id 主键ID
     */
    @Edit
    void delete(@Param("id") Long id);

    /**
     * 校验同一年份下费用类型是否重复
     *
     * 使用场景：
     * 1. 新增时校验 year + maintenanceUnitId + costType 是否已存在
     * 2. 修改时校验 year + maintenanceUnitId + costType 是否与其他记录重复
     *
     * @param year 年份
     * @param maintenanceUnitId 维修单位ID
     * @param costType 费用类型
     * @param id 当前记录ID，修改时用于排除自己；新增时可传 null
     * @return 重复数量
     */
    Long countDuplicate(@Param("year") String year,
                        @Param("maintenanceUnitId") Long maintenanceUnitId,
                        @Param("costType") String costType,
                        @Param("id") Long id);

    List<ECostBudgetManagementDTO> getWarningUser(ECostBudgetManagementDTO dto);

    List<ECostBudgetManagementDTO> getBudgetManagementList();

    BigDecimal getSettlementAmount(ECostBudgetManagementDTO dto);

    List<EContractInfoDTO> getSettlementApply(ECostBudgetManagementDTO dto);

    BigDecimal getMaterialPurchaseAmount(ECostBudgetManagementDTO dto);

    int countNotification(Long id);
}