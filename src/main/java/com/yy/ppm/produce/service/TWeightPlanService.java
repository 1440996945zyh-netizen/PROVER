package com.yy.ppm.produce.service;


import com.yy.common.page.Pages;
import com.yy.ppm.produce.bean.dto.TWeightPlanDTO;
import com.yy.ppm.produce.bean.dto.TWeightPlanItemDTO;
import com.yy.ppm.produce.bean.dto.TWeightPlanSearchDTO;
import com.yy.ppm.produce.bean.dto.TWeightRecordDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 杂项过磅计划表(TWeightPlan)Service
 * @Description
 * @createTime 2023年12月05日 08:39:00
 */
public interface TWeightPlanService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    Pages<TWeightPlanDTO> getList(TWeightPlanSearchDTO searchDTO);

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    TWeightPlanDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param tWeightPlanDTO
     * @return 是否成功
     */
    boolean doSave(TWeightPlanDTO tWeightPlanDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    boolean deleteById(Long id);

    int changeMainStatus(TWeightPlanDTO tWeightPlanDTO);

    List<TWeightRecordDTO> getSundryList(String planNo);

    boolean examine(TWeightPlanDTO dto);


    List<TWeightPlanItemDTO> parseCars(MultipartFile file);
}

