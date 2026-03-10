package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMaintenanceProjectQuotaDTO;
import org.apache.ibatis.annotations.Param;

/**
 * 维修定额项目Mapper
 *
 * @author system
 */
/**
 * 维修定额项目Mapper
 *
 * @author system
 */
public interface EMaintenanceProjectQuotaMapper {

    /** 查询列表（分页） */
    Page<EMaintenanceProjectQuotaDTO> getList(EMaintenanceProjectQuotaDTO searchDTO);

    /** 根据ID查询 */
    EMaintenanceProjectQuotaDTO getById(@Param("id") Long id);

    /** 新增 */
    @Edit
    void insert(EMaintenanceProjectQuotaDTO dto);

    /** 修改 */
    @Edit
    void update(EMaintenanceProjectQuotaDTO dto);

    /** 删除（物理删除） */
    void deleteById(@Param("id") Long id);


    /** 查询当天最新的定额编号（用于生成下一号） */
    String getMaxQuotaNo(@Param("prefix") String prefix);
}
