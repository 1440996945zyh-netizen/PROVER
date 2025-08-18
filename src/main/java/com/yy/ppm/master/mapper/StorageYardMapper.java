package com.yy.ppm.master.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.master.bean.dto.StorageYardDTO;

import java.util.List;

/**
 * 库场Dao
 */
public interface StorageYardMapper {

    /**
     * 根据id获取库场
     */
    StorageYardDTO getById(Long id);

    /**
     * 新增库场
     */
    @Edit
    int insert(StorageYardDTO storageYardDTO);

    /**
     * 修改库场
     */
    @Edit
    int update(StorageYardDTO storageYardDTO);

    /**
     * 根据父id获取库场信息
     */
    List<StorageYardDTO> getByParentId(Long parentId);

    StorageYardDTO getByParentBHTId(String bhtId);

    List<StorageYardDTO> getByCondition(StorageYardDTO searchDTO);

}
