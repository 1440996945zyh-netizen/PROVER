package com.yy.ppm.equipment.mapper;

import com.yy.ppm.equipment.bean.dto.EMaintenanceProjectQuotaDTO;

import java.util.List;

/**
 * 维修定额项目 Mapper
 *
 * 作用：
 * 负责数据库的 CRUD 操作
 *
 * Service层通过该接口调用数据库
 */
public interface EMaintenanceProjectQuotaMapper {

    /**
     * 根据ID查询维修定额项目
     *
     * @param id 主键ID
     * @return DTO对象
     */
    EMaintenanceProjectQuotaDTO selectById(Long id);


    /**
     * 查询维修定额项目列表
     *
     * @return 定额项目集合
     */
    List<EMaintenanceProjectQuotaDTO> selectList();


    /**
     * 新增维修定额项目
     *
     * @param quota DTO对象
     * @return 影响行数
     */
    int insert(EMaintenanceProjectQuotaDTO quota);


    /**
     * 修改维修定额项目
     *
     * @param quota DTO对象
     * @return 影响行数
     */
    int update(EMaintenanceProjectQuotaDTO quota);


    /**
     * 删除维修定额项目
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int delete(Long id);


    /**
     * 查询当天最大的定额编号
     *
     * 用于生成新的定额编号
     *
     * 示例：
     * DE-2026-03-09-0003
     */
    String selectMaxCodeToday();
}
