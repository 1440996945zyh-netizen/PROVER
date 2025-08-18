package com.yy.ppm.master.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.dto.MOperationTechnologyDTO;
import com.yy.ppm.master.bean.po.MOperationTechnologyPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 作业工艺Service
 * */
public interface MOperationTechnologyService {

    /**
     * 查询作业工艺列表
     */
    Pages<MOperationTechnologyPO> selectAllTechnique(String code, PageParameter pageQuery, String name);


    /**
     * 新增作业工艺
     * */
    void insertTechnique(MOperationTechnologyDTO bo);

    /**
     * 修改作业工艺
     * */
    void updateTechnique(MOperationTechnologyDTO bo);

    /**
     * 根据id查询某一个作业工艺
     * */
    MOperationTechnologyDTO selectTechniqueById(@Param("id")Long id);


    /**
     * 删除作业工艺
     * */
    void deleteTechniqueById(Long id);

    /**
     * 查询作业工艺列表
     */
    List<MOperationTechnologyPO> selectTechnique(String code, String name);

}
