package com.yy.ppm.largescreen.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.largescreen.bean.SInportCarInfoDTO;
import com.yy.ppm.largescreen.bean.dto.SInportCarDTO;
import com.yy.ppm.largescreen.bean.dto.SInportCarDTOTemplate;
import com.yy.ppm.largescreen.bean.dto.SInportCarExportDTO;
import com.yy.ppm.largescreen.bean.dto.SInportCarSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 在港车辆表(SInportCar)Mapper
 * @Description
 * @createTime 2024年03月14日 10:42:00
 */
@Repository
public interface SInportCarMapper {

    /**
     * 获取在港车辆表列表
     *
     * @param sInportCarSearchVo
     * @return
     */
    Page<SInportCarDTO> getPageList(SInportCarSearchDTO sInportCarSearchVo);


    /**
     * 导出在港车辆表列表
     *
     * @param sInportCarSearchDTO
     * @return
     */
    List<SInportCarDTO> exportList(SInportCarSearchDTO sInportCarSearchDTO);

    /**
     * 根据id获取在港车辆表
     *
     * @param id 主键
     * @return
     */
    SInportCarDTO getById(Long id);


    /**
     * 新增在港车辆表
     *
     * @param sInportCarDTO
     * @return
     */
    @Edit
    int insert(SInportCarDTO sInportCarDTO);

    /**
     * 批量新增在港车辆表
     *
     * @param sInportCarDTOS
     * @return
     */
    @Edit
    int insertList(@Param("sInportCarDTOS") List<SInportCarDTO> sInportCarDTOS);


    /**
     * 修改在港车辆表
     *
     * @param sInportCarDTO
     * @return
     */
    @Edit
    int update(SInportCarDTO sInportCarDTO);

    /**
     * 批量修改
     *
     * @param sInportCarDTOS
     * @return
     */
    @Edit
    int updateListById(@Param("sInportCarDTOS") List<SInportCarDTO> sInportCarDTOS);


    /**
     * 根据id删除在港车辆表
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);


    /**
     * 批量删除
     * 根据id删除在港车辆表
     *
     * @param ids 主键
     * @return
     */
    int deleteListByIds(@Param("ids") List<Long> ids);

    /**
     * 批量删除
     * 根据id删除在港车辆表
     *
     * @param sInportCarDTO
     * @return
     */
    int deleteByCondition(SInportCarDTO sInportCarDTO);

    Cursor<SInportCarExportDTO> getExportList(SInportCarSearchDTO searchDTO);

    @Edit
    int insertFileList( @Param("sInportCarInfoDTOS") List<SInportCarInfoDTO> sInportCarInfoDTOS);

}

