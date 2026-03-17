package com.yy.ppm.equipment.service;

import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMaterialCodeDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialCodeSearchDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialCodeTreeDTO;

import java.util.List;

/**
 * 物资代码Service接口
 * @author system
 */
public interface EMaterialCodeService {

    /**
     * 查询物资代码列表（分页）
     */
    Pages<EMaterialCodeDTO> getList(EMaterialCodeSearchDTO searchDTO);

    /**
     * 根据ID查询物资代码
     */
    EMaterialCodeDTO getById(Long id);

    /**
     * 新增物资代码
     */
    void save(EMaterialCodeDTO dto);

    /**
     * 删除物资代码
     */
    void deleteById(Long id);

    /**
     * 查询所有物资代码列表（无分页，用于下拉选择）
     */
    List<EMaterialCodeDTO> getAllList();
}

