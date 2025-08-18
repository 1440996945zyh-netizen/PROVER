package com.yy.ppm.master.service;


import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.dto.MOperationProcessDTO;
import com.yy.ppm.master.bean.dto.MOperationSubProcessDTO;
import com.yy.ppm.master.bean.po.MOperationProcessPO;
import com.yy.ppm.master.bean.po.MOperationSubProcessPO;

import java.util.List;

/**
 * 作业过程Service
 * */
public interface MOperationProcessService {


    /**
     * 查询子过程列表
     */
    Pages<MOperationSubProcessDTO> listSubProcess(String processCode, PageParameter pageQuery, String name);

    /**
     * 查询作业过程列表
     */
    Pages<MOperationProcessDTO> listOperationProcess(PageParameter pageQuery, String name);



    /**
     * 查询某个作业过程
     */
    MOperationProcessDTO selectOneById(Long id);

    /**
     * 查询某个子作业过程
     */
    MOperationSubProcessPO selectOneSubById(Long id);

    /**
     * 新增作业过程
     */
    void insertByBo(MOperationProcessPO bo);

    /**
     * 修改作业过程
     */
    void updateProcess(MOperationProcessPO bo);

    /**
     * 删除作业过程信息
     */
    int deleteById(List<Long> ids);

    /**
     * 修改子过程
     */
    void updateSubProcess(MOperationSubProcessPO bo);

    /**
     * 删除子过程信息
     */
    int deleteSubById(Long id);

    /**
     * 新增子过程
     * */
    void insertSub(MOperationSubProcessPO bo);

    /**
     * 查询作业过程
     * */
    List<MOperationProcessPO> selectOperationProcess(String name);

    /**
     * 查询所有作业过程及子过程
     * */
    Pages<MOperationProcessDTO> selectAll(PageParameter pageQuery, String name);
}
