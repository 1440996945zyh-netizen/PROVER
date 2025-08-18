package com.yy.ppm.master.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.master.bean.dto.MOperationProcessDTO;
import com.yy.ppm.master.bean.dto.MOperationSubProcessDTO;
import com.yy.ppm.master.bean.po.MOperationProcessPO;
import com.yy.ppm.master.bean.po.MOperationSubProcessPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 作业过程Mapper接口
 */
public interface MOperationProcessMapper {

    /**
     * 查询子过程列表
     */
    Page<MOperationSubProcessDTO> selectAllSubProcess(@Param("processCode")String processCode, @Param("name") String name);

    /**
     * 查询作业过程列表
     */
    Page<MOperationProcessDTO> selectAllProcess(@Param("name") String name);

    /**
     * 根据父code列表查询子列表总数
     * */
    Long selectCount(@Param("processCode") List<String> codes);

    /**
     * 根据id查询作业过程
     * */
    MOperationProcessDTO selectOneById(@Param("id")Long id);


    /**
     * 根据id查询子作业过程
     * */
    MOperationSubProcessDTO selectOneSubById(@Param("id")Long id);

    /**
     * 新增作业过程
     */
    @Edit
    void insert(MOperationProcessPO bo);

    /**
     * 修改作业过程
     */
    @Edit
    void updateProcess(MOperationProcessPO bo);

    /**
     * 删除作业过程信息
     */
    void deleteById(@Param("list")List ids);

    /**
     * 新增子过程
     */
    @Edit
    void insertSubProcess(MOperationSubProcessPO bo);

    /**
     * 修改子过程
     */
    @Edit
    void updateSubProcess(MOperationSubProcessPO bo);

    /**
     * 删除子过程信息
     */
    int deleteSubById(@Param("id")Long id);

    /**
     * 查询作业过程
     * */
    List<MOperationProcessPO> selectOperationProcess(@Param("name")String name);

    /**
     * 查询总数
     * */
    String getMaxProcessCode();

    /**
     * 查询同一个父过程下子过程的总数
     * */
    String getMaxSubprocessCode(String code);

    /**
     * 查询工艺列表总数
     * */
    Long selectCountTechnique(@Param("list") List<String> codes);

}
