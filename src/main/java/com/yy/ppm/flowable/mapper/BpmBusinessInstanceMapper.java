package com.yy.ppm.flowable.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.flowable.bean.dto.BpmBusinessConfigDTO;
import com.yy.ppm.flowable.bean.dto.BpmBusinessConfigSearchDTO;
import com.yy.ppm.flowable.bean.dto.BpmBusinessInstanceDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description 业务数据与流程实例关系数据
 */
@Mapper
public interface BpmBusinessInstanceMapper {

    /**
     * 新增
     */
    @Edit
    void insert(BpmBusinessInstanceDTO dto);

    /**
     * 更新节点名称以及审批人信息
     * @param bpmBusinessInstanceDTO
     */
    void updateByProcInstId(BpmBusinessInstanceDTO bpmBusinessInstanceDTO);
}
