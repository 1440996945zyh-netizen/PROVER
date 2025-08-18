package com.yy.ppm.produce.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.produce.bean.dto.THqTallyDTO;
import com.yy.ppm.produce.bean.dto.THqTallySearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 海清数据理货表(THqTally)Mapper
 * @Description
 * @createTime 2025年04月24日 19:35:00
 */
@Repository
public interface THqTallyMapper {

    /**
     * 获取海清数据理货表列表
     *
     * @param tHqTallySearchVo
     * @return
     */
    Page<THqTallyDTO> getList(THqTallySearchDTO tHqTallySearchVo);

    /**
     * 导出海清数据理货表列表
     *
     * @param tHqTallySearchDTO
     * @return
     */
    List<THqTallyDTO> exportList(THqTallySearchDTO tHqTallySearchDTO);

    /**
     * 根据id获取海清数据理货表
     *
     * @param id 主键
     * @return
     */
    THqTallyDTO getById(Long id);

    /**
     * 新增海清数据理货表
     *
     * @param tHqTallyDTO
     * @return
     */
    @Edit
    int insert(THqTallyDTO tHqTallyDTO);

    @Edit
    int insertList(@Param("list") List<THqTallyDTO> list);

    /**
     * 修改海清数据理货表
     *
     * @param tHqTallyDTO
     * @return
     */
    @Edit
    int update(THqTallyDTO tHqTallyDTO);


    /**
     * 根据id删除海清数据理货表
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);
}

