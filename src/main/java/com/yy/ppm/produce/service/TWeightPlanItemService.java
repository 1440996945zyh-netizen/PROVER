package com.yy.ppm.produce.service;


import com.yy.common.page.Pages;
import com.yy.ppm.produce.bean.dto.TWeightPlanItemDTO;
import com.yy.ppm.produce.bean.dto.TWeightPlanItemSearchDTO;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName (TWeightPlanItem)Service
 * @Description
 * @createTime 2023年12月05日 08:39:00
 */
public interface TWeightPlanItemService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    Pages<TWeightPlanItemDTO> getList(TWeightPlanItemSearchDTO searchDTO);

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    TWeightPlanItemDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param tWeightPlanItemDTO
     * @return 是否成功
     */
    boolean doSave(TWeightPlanItemDTO tWeightPlanItemDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    boolean deleteById(Long id);

    int changeChildStatus(TWeightPlanItemDTO tWeightPlanItemDTO);
}

