package com.yy.ppm.largescreen.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.largescreen.bean.dto.SPortThroighputDTO;
import com.yy.ppm.largescreen.bean.dto.SPortThroighputExportDTO;
import com.yy.ppm.largescreen.bean.dto.SPortThroighputInfoDTO;
import com.yy.ppm.largescreen.bean.dto.SPortThroighputSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 港区吞吐量表(SPortThroighput)Mapper
 * @Description
 * @createTime 2024年03月15日 09:24:00
 */
@Repository
public interface SPortThroighputMapper {

    /**
     * 获取港区吞吐量表列表
     *
     * @param sPortThroighputSearchVo
     * @return
     */
    Page<SPortThroighputDTO> getPageList(SPortThroighputSearchDTO sPortThroighputSearchVo);


    /**
     * 导出港区吞吐量表列表
     *
     * @param sPortThroighputSearchDTO
     * @return
     */
    List<SPortThroighputDTO> exportList(SPortThroighputSearchDTO sPortThroighputSearchDTO);

    /**
     * 根据id获取港区吞吐量表
     *
     * @param id 主键
     * @return
     */
    SPortThroighputDTO getById(Long id);


    /**
     * 新增港区吞吐量表
     *
     * @param sPortThroighputDTO
     * @return
     */
    @Edit
    int insert(SPortThroighputDTO sPortThroighputDTO);

    /**
     * 批量新增港区吞吐量表
     *
     * @param sPortThroighputDTOS
     * @return
     */
    @Edit
    int insertList(@Param("sPortThroighputDTOS") List<SPortThroighputDTO> sPortThroighputDTOS);


    /**
     * 修改港区吞吐量表
     *
     * @param sPortThroighputDTO
     * @return
     */
    @Edit
    int update(SPortThroighputDTO sPortThroighputDTO);

    /**
     * 批量修改
     *
     * @param sPortThroighputDTOS
     * @return
     */
    @Edit
    int updateListById(@Param("sPortThroighputDTOS") List<SPortThroighputDTO> sPortThroighputDTOS);


    /**
     * 根据id删除港区吞吐量表
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);


    /**
     * 批量删除
     * 根据id删除港区吞吐量表
     *
     * @param ids 主键
     * @return
     */
    int deleteListByIds(@Param("ids") List<Long> ids);

    /**
     * 批量删除
     * 根据id删除港区吞吐量表
     *
     * @param sPortThroighputDTO
     * @return
     */
    int deleteByCondition(SPortThroighputDTO sPortThroighputDTO);

    Cursor<SPortThroighputExportDTO> getExportList(SPortThroighputSearchDTO searchDTO);
    @Edit
    void insertFileList(@Param("sPortThroighputInfoDTOS") List<SPortThroighputInfoDTO> sPortThroighputInfoDTOS);

}

