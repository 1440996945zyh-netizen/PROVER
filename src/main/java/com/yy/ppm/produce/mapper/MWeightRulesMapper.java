package com.yy.ppm.produce.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.produce.bean.dto.MWeightRulesDTO;
import com.yy.ppm.produce.bean.dto.MWeightRulesSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName (MWeightRules)Mapper
 * @Description
 * @createTime 2023年11月30日 17:20:00
 */
@Repository
public interface MWeightRulesMapper {

    /**
     * 获取列表
     *
     * @param mWeightRulesSearchVo
     * @return
     */
    Page<MWeightRulesDTO> getList(MWeightRulesSearchDTO mWeightRulesSearchVo);

    /**
     * 导出列表
     *
     * @param mWeightRulesSearchDTO
     * @return
     */
    List<MWeightRulesDTO> exportList(MWeightRulesSearchDTO mWeightRulesSearchDTO);

    /**
     * 根据id获取
     *
     * @param id 主键
     * @return
     */
    MWeightRulesDTO getById(Long id);

    /**
     * 新增
     *
     * @param mWeightRulesDTO
     * @return
     */
    @Edit
    int insert(MWeightRulesDTO mWeightRulesDTO);

    /**
     * 修改
     *
     * @param mWeightRulesDTO
     * @return
     */
    @Edit
    int update(MWeightRulesDTO mWeightRulesDTO);


    /**
     * 根据id删除
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);

    List<MWeightRulesDTO> getAllList();
}

