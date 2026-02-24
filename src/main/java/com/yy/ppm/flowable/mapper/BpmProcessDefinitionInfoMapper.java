package com.yy.ppm.flowable.mapper;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.flowable.bean.po.BpmProcessDefinitionInfoPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface BpmProcessDefinitionInfoMapper{

    /**
     * 根据processDefinitionIds批量查询
     * @param processDefinitionIds
     * @return
     */
    List<BpmProcessDefinitionInfoPO> selectListByProcessDefinitionIds(@Param("list") Collection<String> processDefinitionIds);

    /**
     * 新增
     * @param bpmProcessDefinitionInfoPO
     * @return
     */
    @Edit
    int insert(BpmProcessDefinitionInfoPO bpmProcessDefinitionInfoPO);

    /**
     * 根据表单id查询表单关联的模型id集合
     * @param formId
     * @return
     */
    List<String> selectModelIdsByFormId(Long formId);
}
