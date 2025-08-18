package com.yy.ppm.produce.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.produce.bean.dto.TPrdDySumDTO;
import com.yy.ppm.produce.bean.dto.TPrdDySumSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName (TPrdDySum)Mapper
 * @Description
 * @createTime 2024年12月03日 17:07:00
 */
@Repository
public interface TPrdDySumMapper {

    /**
     * 获取列表
     *
     * @param tPrdDySumSearchVo
     * @return
     */
    Page<TPrdDySumDTO> getList(TPrdDySumSearchDTO tPrdDySumSearchVo);

    /**
     * 导出列表
     *
     * @param tPrdDySumSearchDTO
     * @return
     */
    List<TPrdDySumDTO> exportList(TPrdDySumSearchDTO tPrdDySumSearchDTO);

    /**
     * 根据id获取
     *
     * @param id 主键
     * @return
     */
    TPrdDySumDTO getById(Long id);

    /**
     * 新增
     *
     * @param dto
     * @return
     */
    @Edit
    int insert(TPrdDySumDTO dto);
    @Edit
    int insertList(List<TPrdDySumDTO> list);

    /**
     * 修改
     *
     * @param tPrdDySumDTO
     * @return
     */
    @Edit
    int update(TPrdDySumDTO tPrdDySumDTO);


    /**
     * 根据id删除
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);
}

