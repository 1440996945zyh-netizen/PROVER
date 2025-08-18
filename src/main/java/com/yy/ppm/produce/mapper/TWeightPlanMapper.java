package com.yy.ppm.produce.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.produce.bean.dto.TWeightPlanDTO;
import com.yy.ppm.produce.bean.dto.TWeightPlanItemDTO;
import com.yy.ppm.produce.bean.dto.TWeightPlanSearchDTO;
import com.yy.ppm.produce.bean.dto.TWeightRecordDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 杂项过磅计划表(TWeightPlan)Mapper
 * @Description
 * @createTime 2023年12月05日 08:39:00
 */
@Repository
public interface TWeightPlanMapper {

    /**
     * 获取杂项过磅计划表列表
     *
     * @param tWeightPlanSearchVo
     * @return
     */
    Page<TWeightPlanDTO> getList(TWeightPlanSearchDTO tWeightPlanSearchVo);

    /**
     * 导出杂项过磅计划表列表
     *
     * @param tWeightPlanSearchDTO
     * @return
     */
    List<TWeightPlanDTO> exportList(TWeightPlanSearchDTO tWeightPlanSearchDTO);

    /**
     * 根据id获取杂项过磅计划表
     *
     * @param id 主键
     * @return
     */
    TWeightPlanDTO getById(Long id);

    /**
     * 新增杂项过磅计划表
     *
     * @param tWeightPlanDTO
     * @return
     */
    @Edit
    int insert(TWeightPlanDTO tWeightPlanDTO);

    /**
     * 修改杂项过磅计划表
     *
     * @param tWeightPlanDTO
     * @return
     */
    @Edit
    int update(TWeightPlanDTO tWeightPlanDTO);


    /**
     * 根据id删除杂项过磅计划表
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);

    int changeMainStatus(TWeightPlanDTO tWeightPlanDTO);

    List<TWeightPlanDTO> getListStatus();


    int getListByPound(String planNo);

    int getListByPlanNoAndIdNumber(String planNo,String idNumber);

    List<TWeightRecordDTO> getListByPlanNo(@Param("planNo") String planNo);

    List<TWeightPlanItemDTO> getItemListById(Long id);

    int getPoundByTruckNo(String truckNo, String planNo);

}

