package com.yy.ppm.master.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.master.bean.dto.MStorageDTO;
import com.yy.ppm.master.bean.dto.MStorageSearchDTO;
import com.yy.ppm.master.bean.dto.MStorageStackDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 库场信息(MStorage)Mapper
 * @Description
 * @createTime 2023年06月05日 17:38:00
 */
@Repository
public interface MStorageMapper {

    /**
     * 获取库场信息列表
     *
     * @param mStorageSearchVo
     * @return
     */
    Page<MStorageDTO> getList(MStorageSearchDTO mStorageSearchVo);

    /**
     * 导出库场信息列表
     *
     * @param mStorageSearchDTO
     * @return
     */
    List<MStorageDTO> exportList(MStorageSearchDTO mStorageSearchDTO);

    /**
     * 根据id获取库场信息
     *
     * @param id 主键
     * @return
     */
    MStorageDTO getById(Long id);

    /**
     * 新增库场信息
     *
     * @param mStorageDTO
     * @return
     */
    @Edit
    int insert(MStorageDTO mStorageDTO);

    /**
     * 修改库场信息
     *
     * @param mStorageDTO
     * @return
     */
    @Edit
    int update(MStorageDTO mStorageDTO);


    /**
     * 根据id删除库场信息
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);


    /**
     * 获取库场信息列表
     *
     * @param storageCode
     * @return
     */
    List<MStorageStackDTO> getStackList(@Param("storageCode") String storageCode, @Param("stackName") String stackName);

    /**
     * 根据id获取库场信息
     *
     * @param id 主键
     * @return
     */
    MStorageStackDTO getStackById(Long id);

    /**
     * 新增库场信息
     *
     * @param mStorageStackDTO
     * @return
     */
    @Edit
    int insertStack(MStorageStackDTO mStorageStackDTO);

    /**
     * 修改库场信息
     *
     * @param mStorageStackDTO
     * @return
     */
    @Edit
    int updateStack(MStorageStackDTO mStorageStackDTO);


    /**
     * 根据id删除库场信息
     *
     * @param id 主键
     * @return
     */
    int deleteStackById(Long id);

    /**
     * 库场下垛位信息
     * @param storageCode
     * @return
     */
    int getChildrenData(String storageCode);
}

