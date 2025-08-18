package com.yy.ppm.produce.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.produce.bean.dto.THqDataDTO;
import com.yy.ppm.produce.bean.dto.THqDataSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 海清数据补录表(THqData)Mapper
 * @Description
 * @createTime 2025年04月24日 17:23:00
 */
@Repository
public interface THqDataMapper {

    /**
     * 获取海清数据补录表列表
     * @param tHqDataSearchVo
     * @return
     */
    Page<THqDataDTO> getList(THqDataSearchDTO tHqDataSearchVo);

    List<THqDataDTO> getHqTallyData(Long tallyId);

    List<Map<String,String>> getHqByCargoInfoId(List<Long> ids);

    /**
     * 导出海清数据补录表列表
     *
     * @param tHqDataSearchDTO
     * @return
     */
    List<THqDataDTO> exportList(THqDataSearchDTO tHqDataSearchDTO);

    List<Long> getHqDataId(Long tallyId);

    Map<String,String> getProcess(Long tallyId);

    /**
     * 根据id获取海清数据补录表
     *
     * @param id 主键
     * @return
     */
    THqDataDTO getById(Long id);

    List<THqDataDTO> getByIds(List<Long> ids);

    Map<String,String> getShipVoyage(Long tallyId);

    /**
     * 新增海清数据补录表
     *
     * @param tHqDataDTO
     * @return
     */
    @Edit
    int insert(THqDataDTO tHqDataDTO);

    @Edit
    int insertList(@Param("list") List<THqDataDTO> list);

    /**
     * 修改海清数据补录表
     *
     * @param tHqDataDTO
     * @return
     */
    @Edit
    int update(THqDataDTO tHqDataDTO);
    @Edit
    int updateList(@Param("list") List<THqDataDTO> list);


    /**
     * 根据id删除海清数据补录表
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);
    int deleteByIds(@Param("list") List<THqDataDTO> list);
    int deleteHqTallyByDataIds(@Param("list") List<THqDataDTO> list);

    int updateByIds(Map<String,Object> condition);
}

