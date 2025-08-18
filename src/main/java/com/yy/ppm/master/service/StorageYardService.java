package com.yy.ppm.master.service;


import com.yy.ppm.master.bean.dto.StorageYardDTO;

import java.util.List;

/**
 * 库场Service
 */
public interface StorageYardService {

    /**
     * 根据库场SID获取库场
     */
    StorageYardDTO getById(Long id);

    /**
     * 保存库场
     */
    Long save(StorageYardDTO storageYardDTO);

    /**
     * 根据parentid获取库场
     */
    List<StorageYardDTO> getByParentId(Long parentId);

    /**
     * 删除
     */
    Integer deleteById(Long id);
}
