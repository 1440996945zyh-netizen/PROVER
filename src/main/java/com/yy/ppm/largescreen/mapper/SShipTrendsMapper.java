package com.yy.ppm.largescreen.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.largescreen.bean.dto.SShipTrendsDTO;
import com.yy.ppm.largescreen.bean.dto.SShipTrendsExportDTO;
import com.yy.ppm.largescreen.bean.dto.SShipTrendsInfoDTO;
import com.yy.ppm.largescreen.bean.dto.SShipTrendsSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName (SShipTrends)Mapper
 * @Description
 * @createTime 2024年03月15日 09:35:00
 */
@Repository
public interface SShipTrendsMapper {

    /**
     * 获取列表
     *
     * @param sShipTrendsSearchVo
     * @return
     */
    Page<SShipTrendsDTO> getPageList(SShipTrendsSearchDTO sShipTrendsSearchVo);


    /**
     * 导出列表
     *
     * @param sShipTrendsSearchDTO
     * @return
     */
    List<SShipTrendsDTO> exportList(SShipTrendsSearchDTO sShipTrendsSearchDTO);

    /**
     * 根据id获取
     *
     * @param id 主键
     * @return
     */
    SShipTrendsDTO getById(Long id);


    /**
     * 新增
     *
     * @param sShipTrendsDTO
     * @return
     */
    @Edit
    int insert(SShipTrendsDTO sShipTrendsDTO);

    /**
     * 批量新增
     *
     * @param sShipTrendsDTOS
     * @return
     */
    @Edit
    int insertList(@Param("sShipTrendsDTOS") List<SShipTrendsDTO> sShipTrendsDTOS);


    /**
     * 修改
     *
     * @param sShipTrendsDTO
     * @return
     */
    @Edit
    int update(SShipTrendsDTO sShipTrendsDTO);

    /**
     * 批量修改
     *
     * @param sShipTrendsDTOS
     * @return
     */
    @Edit
    int updateListById(@Param("sShipTrendsDTOS") List<SShipTrendsDTO> sShipTrendsDTOS);


    /**
     * 根据id删除
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);


    /**
     * 批量删除
     * 根据id删除
     *
     * @param ids 主键
     * @return
     */
    int deleteListByIds(@Param("ids") List<Long> ids);

    /**
     * 批量删除
     * 根据id删除
     *
     * @param sShipTrendsDTO
     * @return
     */
    int deleteByCondition(SShipTrendsDTO sShipTrendsDTO);

    Cursor<SShipTrendsExportDTO> getExportList(SShipTrendsSearchDTO searchDTO);
    @Edit
    void insertFileList(@Param("sShipTrendsInfoDTOS") List<SShipTrendsInfoDTO> sShipTrendsInfoDTOS);

}

