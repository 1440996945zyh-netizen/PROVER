package com.yy.ppm.master.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.master.bean.dto.MOperationTechnologyDTO;
import com.yy.ppm.master.bean.dto.MOperationTechnologyMachineDTO;
import com.yy.ppm.master.bean.dto.MOperationTechnologyWorkwarDTO;
import com.yy.ppm.master.bean.po.MOperationTechnologyMachinePO;
import com.yy.ppm.master.bean.po.MOperationTechnologyPO;
import com.yy.ppm.master.bean.po.MOperationTechnologyWorkerPO;
import com.yy.ppm.master.bean.po.MOperationTechnologyWorkwarPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 作业工艺Mapper接口
 *
 */
public interface MOperationTechnologyMapper{


    /**
     * 查询作业工艺列表
     */
    Page<MOperationTechnologyPO> selectAllTechnique(@Param("code")String code, @Param("name") String name);


    /**
     * 增加作业工艺
     * */
    @Edit
    void insertTechnique( MOperationTechnologyPO bo);


    /**
     * 修改作业工艺
     * */
    @Edit
    void updateTechnique(MOperationTechnologyPO bo);

    /**
     * 根据id查询某一个作业工艺
     * */
    MOperationTechnologyDTO selectTechniqueById(@Param("id")Long id);


    /**
     * 删除作业工艺
     * */
    void deleteTechniqueById(@Param("id") Long id);


    /**
     * 查询列表总数
     * */
    Long selectCount(@Param("list") List<String> codes);

    /**
     * 查询作业工艺列表
     */
    List<MOperationTechnologyPO> selectTechnique(@Param("code")String code, @Param("name") String name);

    /**
     * 查询机械，工人，工属具
     * */
    List<MOperationTechnologyMachineDTO> selectMachineById(@Param("id") Long id);
    List<MOperationTechnologyWorkerPO> selectWorkerById(@Param("id")Long id);
    List<MOperationTechnologyWorkwarDTO> selectWorkwarById(@Param("id")Long id);

    /**
     * 添加机械，工人，工属具
     * */
    void insertMachine(MOperationTechnologyMachinePO bo);
    void insertWorker(MOperationTechnologyWorkerPO bo);
    void insertWorkwar(MOperationTechnologyWorkwarPO bo);

    /**
     * 根据作业工艺id删除子配置
     * */
    void deleteMachineById(@Param("id") Long id);
    void deleteWorkerById(@Param("id")Long id);
    void deleteWorkwarById(@Param("id")Long id);

}
