package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.ECostBudgetManagementDTO;
import org.apache.ibatis.annotations.Param;

/**
 * 预算管理 Mapper
 */
public interface ECostBudgetManagementMapper {

    /** 查询列表（分页） */
    Page<ECostBudgetManagementDTO> selectList(ECostBudgetManagementDTO searchDTO);

    /** 根据ID查询 */
    ECostBudgetManagementDTO selectById(@Param("id") Long id);

    /** 新增 */
    @Edit
    void add(ECostBudgetManagementDTO dto);

    /** 修改 */
    @Edit
    void update(ECostBudgetManagementDTO dto);

    /** 删除 */
    @Edit
    void delete(@Param("id") Long id);

    /** 同一年份下费用类型是否重复 */
    Long countDuplicate(@Param("year") String year,
                        @Param("costType") String costType,
                        @Param("id") Long id);
}
