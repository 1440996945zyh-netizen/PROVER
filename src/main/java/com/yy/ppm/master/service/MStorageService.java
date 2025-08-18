package com.yy.ppm.master.service;


import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.dto.MStorageDTO;
import com.yy.ppm.master.bean.dto.MStorageSearchDTO;
import com.yy.ppm.master.bean.dto.MStorageStackDTO;

import java.util.List;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 库场信息(MStorage)Service
 * @Description
 * @createTime 2023年06月05日 17:38:00
 */
public interface MStorageService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    Pages<MStorageDTO> getList(MStorageSearchDTO searchDTO);

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    MStorageDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param mStorageDTO
     * @return 是否成功
     */
    boolean doSave(MStorageDTO mStorageDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    boolean deleteById(Long id);


    /**
     * 获取垛位列表（翻页）
     *
     * @param storageCode
     * @return 对象列表
     */
    List<MStorageStackDTO> getStackList(String storageCode, String stackName);

    /**
     * 查询单条垛位记录
     *
     * @param id
     * @return 实体
     */
    MStorageStackDTO getStackDetail(Long id);

    /**
     * 保存垛位
     *
     * @param mStorageStackDTO
     * @return 是否成功
     */
    boolean doSaveStack(MStorageStackDTO mStorageStackDTO);

    /**
     * 删除垛位
     *
     * @param id
     * @return 是否成功
     */
    boolean deleteStackById(Long id);

}

