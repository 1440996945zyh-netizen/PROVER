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
     * 新增（
     */
    @Edit
    int add(EMaintenanceProjectQuotaDTO quota);

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
     * 查询当天最大定额编号
     */
    String selectMaxCodeToday();
}