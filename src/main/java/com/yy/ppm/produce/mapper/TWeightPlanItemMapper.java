package com.yy.ppm.produce.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.produce.bean.dto.TWeightPlanItemDTO;
import com.yy.ppm.produce.bean.dto.TWeightPlanItemSearchDTO;
import com.yy.ppm.produce.bean.po.TWeightPlanItemPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName (TWeightPlanItem)Mapper
 * @Description
 * @createTime 2023年12月05日 08:39:00
 */
@Repository
public interface TWeightPlanItemMapper {

    /**
     * 获取列表
     *
     * @param tWeightPlanItemSearchVo
     * @return
     */
    Page<TWeightPlanItemDTO> getList(TWeightPlanItemSearchDTO tWeightPlanItemSearchVo);

    /**
     * 导出列表
     *
     * @param tWeightPlanItemSearchDTO
     * @return
     */
    List<TWeightPlanItemDTO> exportList(TWeightPlanItemSearchDTO tWeightPlanItemSearchDTO);

    /**
     * 根据id获取
     *
     * @param id 主键
     * @return
     */
    TWeightPlanItemDTO getById(Long id);

    /**
     * 新增
     *
     * @param tWeightPlanItemDTO
     * @return
     */
    @Edit
    int insert(TWeightPlanItemDTO tWeightPlanItemDTO);

    /**
     * 修改
     *
     * @param tWeightPlanItemDTO
     * @return
     */
    @Edit
    int update(TWeightPlanItemDTO tWeightPlanItemDTO);


    /**
     * 根据id删除
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);

    @Edit
    void insertBatch(@Param("list") List<TWeightPlanItemDTO> list);

    void deleteByParentId(@Param("parentId") Long id);

    int changeChildStatus(TWeightPlanItemDTO tWeightPlanItemDTO);

    List<TWeightPlanItemDTO> getByParentId(@Param("parentId") Long id);
}

