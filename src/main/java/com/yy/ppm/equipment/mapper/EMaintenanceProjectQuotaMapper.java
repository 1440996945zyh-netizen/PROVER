package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMaintenanceProjectQuotaDTO;
import org.apache.ibatis.annotations.Param;



/**
 * 维修定额项目 Mapper
 */
public interface EMaintenanceProjectQuotaMapper {

    /**
     * 查询列表（分页）
     */
    Page<EMaintenanceProjectQuotaDTO> selectList(EMaintenanceProjectQuotaDTO searchDTO);

    /**
     * 根据ID查询
     */
    EMaintenanceProjectQuotaDTO selectById(@Param("id") Long id);

    /**
     * 新增（@Edit 自动注入 loginUserId/now）
     */
    @Edit
    void add(EMaintenanceProjectQuotaDTO dto);

    /**
     * 修改（@Edit 自动注入 loginUserId/now）
     */
    @Edit
    void update(EMaintenanceProjectQuotaDTO dto);

    /**
     * 删除（物理删除）
     */
    @Edit
    void delete(@Param("id") Long id);

    /**
     * 查询当天最大定额编号（用于生成下一条编号）
     */
    String selectMaxCodeToday();

    /**
     * 批量修改状态
     * @param dto 包含 ids 与 status
     */
    void updateStatusBatch(EMaintenanceProjectQuotaDTO dto);
}