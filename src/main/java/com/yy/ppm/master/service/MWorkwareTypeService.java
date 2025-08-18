package com.yy.ppm.master.service;


import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.dto.MWorkwareTypeDTO;
import com.yy.ppm.master.bean.po.MWorkwareTypePO;

import java.util.List;

/**
 * 工属具Service接口
 *
 */
public interface MWorkwareTypeService {

    /**
     * 查询工属具类型列表
     * */
    Pages<MWorkwareTypeDTO> listBWorkwareType(PageParameter pageQuery, String name);

    /**
     * 根据id查询工属具类型
     * */
    MWorkwareTypePO selectBWorkwareTypeById(Long id);

    /**
     * 新增工属具类型
     * */
    void insertBWorkwareType(MWorkwareTypeDTO bo);

    /**
     * 修改工属具类型
     * */
    void updateBWorkwareType(MWorkwareTypeDTO bo);

    /**
     * 删除工属具类型
     * */
    void deleteBWorkwareType(Long id);
}
