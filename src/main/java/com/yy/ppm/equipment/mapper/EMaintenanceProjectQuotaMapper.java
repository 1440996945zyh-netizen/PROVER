package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMaintenanceProjectQuotaDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 维修定额项目 Mapper
 * insert/update/delete 加 @Edit：让 DbUpdateAspect 自动注入 loginUserId、now
 */
public interface EMaintenanceProjectQuotaMapper {
    /**
     * 查询列表（分页由 PageHelper 控制）
     */
    Page<EMaintenanceProjectQuotaDTO> selectList(EMaintenanceProjectQuotaDTO searchDTO);
    /**
     * 根据ID查询
     */
    EMaintenanceProjectQuotaDTO selectById(@Param("id") Long id);
    /**
     * 新增
     */
    @Edit
    int insert(EMaintenanceProjectQuotaDTO quota);
    /**
     * 修改
     */
    @Edit
    int update(EMaintenanceProjectQuotaDTO quota);
    /**
     * 删除
     */
    @Edit
    int delete(@Param("id") Long id);
    /**
     * 查询当天最大定额编号（用于生成下一条编号）
     */
    String selectMaxCodeToday();
}