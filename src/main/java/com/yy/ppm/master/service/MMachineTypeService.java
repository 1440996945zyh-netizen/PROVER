package com.yy.ppm.master.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.dto.MMachineTypeDTO;
import com.yy.ppm.master.bean.po.MMachineTypeModelPO;
import com.yy.ppm.master.bean.po.MMachineTypePO;

import java.util.List;
import java.util.Map;

/**
 * 机械类型Service
 * */
public interface MMachineTypeService {
    /**
     * 查询机械类型列表
     * */
    Pages<MMachineTypeDTO> listBMachineType(PageParameter pageQuery, String name);

    /**
     * 根据id查询机械类型
     * */
    MMachineTypeDTO selectBMachineTypeById(String id);

    /**
     * 修改机械类型
     * */
    void saveBMachineType(MMachineTypeDTO bo);

    /**
     * 删除机械类型
     * */
    void deleteBMachineType(Long ids);

    /**
     * 查询机械类型列表
     * */
    List<MMachineTypeModelPO> listBMachineTypeModel();

    List<Map<String, String>> getMacModelByTypeCode(String id);
}
