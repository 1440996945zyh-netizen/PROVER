package com.yy.ppm.largescreen.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.largescreen.bean.dto.SPortStorageDTO;
import com.yy.ppm.largescreen.bean.dto.SPortStorageExportDTO;
import com.yy.ppm.largescreen.bean.dto.SPortStorageInfoDTO;
import com.yy.ppm.largescreen.bean.dto.SPortStorageSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName (SPortStorage)Mapper
 * @Description
 * @createTime 2024年03月14日 23:13:00
 */
@Repository
public interface SPortStorageMapper {

    /**
     * 获取列表
     *
     * @param sPortStorageSearchVo
     * @return
     */
    Page<SPortStorageDTO> getPageList(SPortStorageSearchDTO sPortStorageSearchVo);


    /**
     * 导出列表
     *
     * @param sPortStorageSearchDTO
     * @return
     */
    List<SPortStorageDTO> exportList(SPortStorageSearchDTO sPortStorageSearchDTO);

    /**
     * 根据id获取
     *
     * @param id 主键
     * @return
     */
    SPortStorageDTO getById(Long id);


    /**
     * 新增
     *
     * @param sPortStorageDTO
     * @return
     */
    @Edit
    int insert(SPortStorageDTO sPortStorageDTO);

    /**
     * 批量新增
     *
     * @param sPortStorageDTOS
     * @return
     */
    @Edit
    int insertList(@Param("sPortStorageDTOS") List<SPortStorageDTO> sPortStorageDTOS);


    /**
     * 修改
     *
     * @param sPortStorageDTO
     * @return
     */
    @Edit
    int update(SPortStorageDTO sPortStorageDTO);

    /**
     * 批量修改
     *
     * @param sPortStorageDTOS
     * @return
     */
    @Edit
    int updateListById(@Param("sPortStorageDTOS") List<SPortStorageDTO> sPortStorageDTOS);


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
     * @param sPortStorageDTO
     * @return
     */
    int deleteByCondition(SPortStorageDTO sPortStorageDTO);

    Cursor<SPortStorageExportDTO> getExportList(SPortStorageSearchDTO searchDTO);
    @Edit
    void insertFileList(@Param("sPortStorageInfoDTOS")List<SPortStorageInfoDTO> sPortStorageInfoDTOS);

}

